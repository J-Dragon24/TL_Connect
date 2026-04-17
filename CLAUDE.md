# TL Connect - Clean Architecture

## Core Principles

1. **Dependency Rule**: Source code dependencies only point inward.
2. **Domain is Pure**: `domain/` has ZERO framework imports.
3. **UseCase = Business Logic**: Controllers delegate, don't contain logic.
4. **Simplicity**: Don't create artifacts unless they add value.

---

## Layer Structure

```
src/main/java/com/tl_connect/

┌─────────────────────────────────────────────────────────────────┐
│  presentation/           Layer 4: INTERFACE ADAPTERS            │
│    controller/          - HTTP endpoints                        │
│    dto/request/          - API request DTOs (with validation)   │
│    dto/response/         - API response DTOs                     │
│    mapper/               - Map: request ↔ domain                 │
│    utility/              - HTTP helpers (ResponseHelper)         │
└─────────────────────────────────────────────────────────────────┘
                                ↓
┌─────────────────────────────────────────────────────────────────┐
│  application/           Layer 3: APPLICATION                    │
│    usecase/             - Business logic per action             │
│    port/out/            - External service interfaces           │
│    dto/                  - UseCase input/output (plain DTOs)     │
│    mapper/               - Map: domain ↔ application DTO         │
└─────────────────────────────────────────────────────────────────┘
                                ↓
┌─────────────────────────────────────────────────────────────────┐
│  domain/                Layer 2: DOMAIN (INNERMOST)             │
│    model/               - Pure entities                         │
│    repository/          - Repository interfaces (ports)         │
│    service/             - ONLY cross-aggregate logic           │
│    valueobject/         - Immutable value types                  │
└─────────────────────────────────────────────────────────────────┘
                                ↓
┌─────────────────────────────────────────────────────────────────┐
│  infrastructure/       Layer 1: FRAMEWORKS & DRIVERS             │
│    persistence/        - Repository implementations (impl/)      │
│    persistence/entity/  - JPA entities (@Entity, @Table)        │
│    persistence/mapper/  - Map: domain ↔ entity                   │
│    security/port/      - Security Port interfaces               │
│    security/impl/      - JWT, OAuth, Token implementations        │
│    external/port/      - External service Port interfaces         │
│    external/impl/      - File storage, Notification, Payment impl │
│    importer/            - Mini-module: file import functionality  │
│    config/              - Spring/System configurations            │
│    utility/             - Pure functions ONLY (no side effects)  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Mapper Responsibility

| Mapping | Who Handles | Note |
|---------|-----------|------|
| `request (API) → command (Application)` | `presentation/mapper/` | Maps API input to Application layer |
| `domain → response (API)` | `presentation/mapper/` | Maps Domain to API output |
| `domain ↔ entity` | `infrastructure/persistence/mapper/` | Maps Domain ↔ JPA Entity |
| `domain → application DTO` | `application/mapper/` | If needed |

**CRITICAL:** Presentation mapper maps **API Request → Application Command**, NOT to Domain directly. Domain objects are created by Domain or Application layer only.

**Wrong:**
```
presentation/mapper/ → creates new Student(name, email)  ❌
```

**Correct:**
```
presentation/mapper/ → CreateStudentCommand(name, email)
UseCase → Student.create(name, email)  ✅
```

---

## Utility Placement

| Utility | Correct Layer | Reason |
|---------|--------------|--------|
| `ResponseHelper` | `presentation/utility/` | HTTP response formatting |
| `FileHelper` | `infrastructure/utility/` | File system operations |
| `NotificationHelper` | `infrastructure/utility/` | External notification system |
| `FileParseHelper` | `infrastructure/utility/` | File parsing (CSV, Excel) |

**Rule:** Utility belongs to the layer that owns the concern it serves.

---

## Port (Interface) Pattern

UseCase gọi infrastructure qua **interface (Port)**, không gọi trực tiếp implementation.

### Hai loại Port

| Port Type | Đặt ở | Implements ở | Ví dụ |
|-----------|--------|--------------|-------|
| **Persistence Port** | `domain/repository/` | `infrastructure/persistence/adapter/` | `StudentRepository` |
| **External Service Port** | `application/port/out/` | `infrastructure/external/` | `FileStoragePort`, `NotificationPort` |

### External Service Port - Ví dụ

```java
// application/port/out/FileStoragePort.java
public interface FileStoragePort {
    String upload(byte[] data, String filename, String contentType);
    void delete(String url);
}

// application/port/out/NotificationPort.java
public interface NotificationPort {
    void sendEmail(String to, String subject, String body);
}

// infrastructure/external/impl/BackblazeFileStorageImpl.java
public class BackblazeFileStorageImpl implements FileStoragePort {
    @Override
    public String upload(byte[] data, String filename, String contentType) {
        // implementation cụ thể
    }
}

// infrastructure/external/impl/FirebaseNotificationImpl.java
public class FirebaseNotificationImpl implements NotificationPort {
    @Override
    public void sendEmail(String to, String subject, String body) { ... }
}

// application/usecase/student/UpdateStudentAvatarUseCase.java
@Service
@RequiredArgsConstructor
public class UpdateStudentAvatarUseCase {

    private final StudentRepository studentRepository;   // domain port
    private final FileStoragePort fileStoragePort;       // application port

    @Transactional
    public void execute(Long studentId, byte[] fileData, String contentType) {
        // 1. Upload qua port - UseCase không biết implementation nào
        String avatarUrl = fileStoragePort.upload(fileData, "avatar.jpg", contentType);

        // 2. Update student
        Student student = studentRepository.findById(studentId);
        student.updateAvatarUrl(avatarUrl);
        studentRepository.save(student);
    }
}
```

### Pure Function Utility - Gọi trực tiếp

Utility **không có side effect** (pure function) có thể gọi trực tiếp:

```java
// infrastructure/utility/StringHelper.java  hoặc shared/
public final class StringHelper {
    public static String slugify(String input) { ... }  // Pure, no side effects
}

// application/usecase/CreateStudentUseCase.java
public CreateStudentUseCase {
    public void execute(CreateStudentCommand cmd) {
        String slug = StringHelper.slugify(cmd.getName());  // ✅ OK - pure function
    }
}
```

### Khi nào tạo Port?

| Tình huống | Cần Port? | Lý do |
|------------|-----------|--------|
| Single implementation (chỉ có 1 cách lưu trữ) | ❌ Không | Over-engineering |
| Có thể thay đổi implementation (file, notification) | ✅ Có | Testability, flexibility |
| Side effect operations (email, file, payment) | ✅ Có qua Port | Hard to test otherwise |
| Pure function (format, convert) | ❌ Không | Gọi trực tiếp |

---

## Directory Breakdown

### 1. Domain Layer

**RULE: Zero framework dependencies. No `@Entity`, no Spring annotations.**

```
domain/
├── model/
│   ├── Student.java                    # id, name, enroll(), updateProfile()
│   ├── Subject.java
│   └── [Entity].java                   # Pure POJO
│
├── repository/                          # Interfaces ONLY - no implementations
│   ├── StudentRepository.java
│   └── [Entity]Repository.java
│
├── service/                            # ONLY cross-aggregate logic
│   ├── GradingDomainService.java       # computeFinalGrade(student, subject)
│   └── EnrollmentDomainService.java    # validateEnrollment(...)
│   # AVOID: Services that just delegate to repository
│
└── valueobject/
    ├── StudentId.java
    ├── Email.java
    └── Grade.java
```

### 2. Application Layer

**RULE: Business logic. No HTTP, no persistence.**

```
application/
├── usecase/
│   ├── student/
│   │   ├── CreateStudentUseCase.java   # creates Student domain object
│   │   ├── GetStudentUseCase.java      # getStudent(id)
│   │   ├── UpdateStudentUseCase.java   # updateStudent(id, request)
│   │   └── DeleteStudentUseCase.java
│   │
│   └── [module]/
│       └── [Action]UseCase.java
│
├── port/out/                            # External service interfaces
│   ├── FileStoragePort.java
│   ├── NotificationPort.java
│   └── [X]Port.java
│
└── dto/                                # Plain DTOs - no validation
    ├── student/
    │   ├── CreateStudentRequest.java   # name, email, dob...
    │   └── StudentResponse.java        # id, name, email...
    └── [module]/
        └── [Action]Request.java
```

**Note:** Application DTOs may be the same as presentation DTOs. Only separate if transformation is needed between layers.

### 3. Infrastructure Layer

**RULE: Implements domain repository interfaces. Contains framework code.**

```
infrastructure/
├── persistence/
│   ├── repository/                     # Repository implementations (impl, not adapter)
│   │   ├── JpaStudentRepository.java    # implements StudentRepository
│   │   └── Jpa[Entity]Repository.java
│   │
│   ├── entity/                         # JPA entities (@Entity, @Table)
│   │   ├── StudentEntity.java
│   │   └── [Entity]Entity.java
│   │
│   └── mapper/
│       ├── StudentEntityMapper.java     # toEntity(domain), toDomain(entity)
│       └── [Entity]EntityMapper.java
│
├── security/
│   ├── port/                           # Security Port interfaces
│   │   ├── AuthenticationPort.java      # interface
│   │   └── TokenPort.java              # interface
│   │
│   └── impl/                           # Security implementations
│       ├── JwtService.java             # implements AuthenticationPort
│       ├── OAuthService.java
│       └── RefreshTokenService.java
│
├── external/                           # Third-party service implementations
│   ├── port/                           # External service Port interfaces
│   │   ├── FileStoragePort.java        # interface
│   │   ├── NotificationPort.java      # interface
│   │   └── PaymentPort.java           # interface
│   │
│   └── impl/
│       ├── BackblazeFileStorageImpl.java    # implements FileStoragePort
│       ├── FirebaseNotificationImpl.java     # implements NotificationPort
│       └── VNPayPaymentImpl.java            # implements PaymentPort
│
├── importer/                           # Mini-module: File Import
│   ├── usecase/
│   │   └── ImportStudentsUseCase.java
│   ├── service/
│   │   └── FileParseService.java
│   └── accessor/
│       ├── CsvRowAccessor.java
│       └── ExcelRowAccessor.java
│
├── config/
│   ├── RedisConfig.java
│   └── B2Config.java
│
└── utility/                            # Pure helper functions ONLY
    ├── StringHelper.java               # slugify, normalize
    ├── DateHelper.java                 # format, parse (no side effects)
    └── [PureHelper].java
```

**IMPORTANT:**
- **`persistence/adapter/` → `persistence/repository/`** - "impl" rõ ràng hơn "adapter"
- **External services → `external/port/` + `external/impl/`** - FileHelper, NotificationHelper là implementations, không phải utility
- **`importer/`** - Đây là mini-module, không phải utility. Có usecase, service riêng.
- **`security/`** - Cần Port interface (AuthenticationPort, TokenPort) để UseCase gọi qua interface
```

### 4. Presentation Layer

**RULE: HTTP handling. Validate input, call UseCase, format response.**

```
presentation/
├── controller/
│   ├── StudentController.java
│   ├── AcademicResultController.java
│   └── [Module]Controller.java
│
├── dto/
│   ├── request/
│   │   ├── CreateStudentRequest.java   # @NotBlank, @Email, @Valid
│   │   └── [Action]Request.java
│   │
│   └── response/
│       ├── StudentApiResponse.java
│       └── ErrorResponse.java
│
├── mapper/
│   └── StudentApiMapper.java            # Map: request ↔ domain
│
└── utility/
    └── ResponseHelper.java              # HTTP response formatting
```

---

## Entity First, Domain Service When Needed

**Priority:**
1. **Entity method** - When logic belongs to single entity
2. **UseCase** - When logic involves use case orchestration
3. **Domain Service** - ONLY when logic crosses aggregate boundaries

```java
// domain/model/Student.java
public class Student {
    public void updateProfile(String name, Email email) {
        // ✅ Logic belongs to Student entity
    }

    public void changeMajor(MajorId majorId) {
        // ✅ Logic belongs to Student entity
    }
}

// domain/service/EnrollmentValidationDomainService.java
public class EnrollmentValidationDomainService {
    public EnrollmentResult validateEnrollment(Student student, Course course) {
        // ✅ ONLY when logic requires coordination across aggregates
        // - Check prerequisites
        // - Check capacity
        // - Check schedule conflicts
    }
}
```

---

## Dependency Flow

```
┌─────────────────┐
│   Controller    │  presentation/
└────────┬────────┘
         │ (1) request → command
         ↓
┌─────────────────┐
│    UseCase     │  application/
└────────┬───────┘
         │ (2) creates/manipulates domain
         │ (3) calls Application Port
         ↓
┌─────────────────────┐
│   Domain Model     │  domain/
└────────┬───────────┘
         │ (4) calls Domain Port (Repository interface)
         ↓
┌───────────────────────┐
│  DomainRepository    │  domain/ (interface only)
└────────┬──────────────┘
         │ (5) implemented by
         ↓
┌───────────────────────────┐
│ JpaStudentRepository     │  infrastructure/persistence/repository/
└────────────┬──────────────┘
             │ (6) uses
             ↓
┌─────────────────┐
│    Entity      │  infrastructure/persistence/entity/
└─────────────────┘

┌───────────────────────────────────────────────────────┐
│  Application Port → Implementation                    │
│  application/port/out/ → infrastructure/external/impl│
└───────────────────────────────────────────────────────┘
```

**Key Point:** Domain objects created by Domain/Application only. UseCase calls infrastructure through Ports (interfaces).

---

## Example: Student Module (Clean Architecture)

```
domain/
├── model/
│   └── Student.java                      # Pure entity
├── repository/
│   └── StudentRepository.java            # interface
└── valueobject/
    └── Email.java                        # immutable

application/
├── usecase/student/
│   ├── CreateStudentUseCase.java        # creates Student via Student.create()
│   ├── GetStudentUseCase.java
│   ├── UpdateStudentAvatarUseCase.java  # uses FileStoragePort
│   └── dto/
│       └── StudentResponse.java          # plain DTO
│
└── port/out/
    ├── FileStoragePort.java              # interface
    └── NotificationPort.java             # interface

infrastructure/
├── persistence/
│   ├── repository/
│   │   └── JpaStudentRepository.java     # implements StudentRepository
│   ├── entity/
│   │   └── StudentEntity.java           # @Entity
│   └── mapper/
│       └── StudentEntityMapper.java      # domain ↔ entity
│
├── external/
│   ├── port/
│   │   └── FileStoragePort.java          # interface
│   └── impl/
│       └── BackblazeFileStorageImpl.java # implements FileStoragePort
│
├── security/
│   ├── port/
│   │   └── AuthenticationPort.java       # interface
│   └── impl/
│       └── JwtService.java               # implements AuthenticationPort
│
└── utility/
    └── StringHelper.java                 # pure function only

presentation/
├── controller/
│   └── StudentController.java
├── dto/
│   ├── request/CreateStudentRequest.java # @Valid
│   └── response/StudentApiResponse.java
├── mapper/
│   └── StudentApiMapper.java             # Request → Command (NOT Domain)
└── utility/
    └── ResponseHelper.java
```

**Note:** Controller → mapper → Command (Application), NOT Domain. UseCase creates Domain and calls infrastructure through Ports.

---

## Anti-Patterns to Avoid

| Anti-Pattern | Problem | Solution |
|-------------|---------|----------|
| Business logic in Controller | Hard to test | Move to UseCase |
| CRUD in Domain Service | Should be in Entity | Move to Entity |
| JPA entity in domain model | Framework coupling | Separate Entity ↔ Model |
| Utility in wrong layer | ResponseHelper in infra | Move to presentation |
| Port for single impl | Over-engineering | Skip Port |
| Domain Service for single-entity logic | Not needed | Move to Entity |
| Separate Command + Request | Redundant | Pick one: Command OR Request |
| Presentation creates Domain objects | Domain coupled to input | UseCase/Domain creates Domain |
| UseCase calls utility with side-effect directly | Hard to test | Use Port interface |
