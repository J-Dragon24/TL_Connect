# TL Connect AI Coding Instructions

- This is a Spring Boot monolith rooted at `src/main/java/com/tl_connect/dev`. The entrypoint is `src/main/java/com/tl_connect/dev/Main.java` with `@SpringBootApplication`, `@EnableScheduling`, and `@EnableAsync`.
- The code is organized by feature module under `com.tl_connect.dev.modules.*`, not by strict separate `presentation/application/domain/infrastructure` packages.

## Key patterns

- Controllers are thin. They usually validate auth via `Authentication` and `JwtUserInfo`, then delegate to `@Service` classes.
- Responses are normalized through `ResponseHelper.success(...)` and `ResponseHelper.internalError(...)` from `com.tl_connect.dev.core.common.ultility.ResponseHelper`.
- Most service methods are annotated with `@Transactional`. Service layer contains business logic and orchestrates repository calls.
- Repositories are Spring Data JPA `@Repository` interfaces and entities are in `entity/` subpackages.
- A common error idiom is `repository.findById(...).orElseThrow(() -> new NotFoundException(...));`.
- Authentication failures are thrown as `UnauthorizeException` rather than returning `ResponseEntity` directly.

## Important modules

- `student`, `student_class`, `course_class`, `subject`, `study_program`, `semester`, `schedule`, `tuition`, `payment`, `oauth`, `notification`
- Payment and refund flows use Redis via `RedisTemplate` and `core/config/RedisConfig.java`.
- API surface is versioned under `/api/v1/...` in controllers.

## Build and runtime

- Use the Gradle wrapper from project root: `./gradlew build`, `./gradlew test`, `./gradlew bootRun`.
- Local config is loaded from `src/main/resources/application.yml` with `spring.profiles.active: local`.
- Database is PostgreSQL on `jdbc:postgresql://localhost:5432/ThangLong` and Hibernate DDL is disabled (`ddl-auto: none`). Schema scripts live under `schema/`.
- Redis is defined in `compose.yaml` and required by payment refund caching logic.

## Project-specific guidance

- Prefer existing module structure over introducing new cross-module packages unless the feature is genuinely shared.
- For new HTTP endpoints, keep controllers minimal and use service classes for business logic.
- Keep DTOs in module `dto/` packages and projections in `projection/` packages when queries return custom views.
- Avoid creating new global utility classes unless they belong to `core/` and serve a reusable concern.

## Notes for AI edits

- Respect the established `ResponseHelper` wrapper format in controllers.
- When changing persistence behavior, update the matching repository or service method, not controller logic.
- If adding a new scheduled or async task, note that the app already enables scheduling and async execution.
- Use `CLAUDE.md` as a high-level architecture reference, but follow the actual `com.tl_connect.dev.modules.*` package layout in source.
