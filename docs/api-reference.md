# TL Connect API Reference
> **BaseUrl**: http://localhost:8080  
> **Api Version**: 1  
> **Content-Type**: application/json (trừ các endpoint dùng multipart/form-data)
---
  
### Mục lục
---
1. [Response Format chung](#1-response-format-chung)
2. [Mã lỗi (Response Codes)](#2-mã-lỗi-response-codes)
3. [Authentication](#3-authentication)
4. [OAuth2 – Đăng nhập](#4-oauth2--đăng-nhập)
5. [Student – Quản lý thông tin](#5-student--quản-lý-thông-tin)
6. [Study Program - Chương trình đào tạo](#6-study-program--chương-trình-đào-tạo)
7. [Schedule - Lịch học](#7-schedule--lịch-học)
8. [Exam - Lịch thi](#8-exam--lịch-thi)
9. [Mark - Kết quả học tập](#9-mark--kết-quả-học-tập)
10. [Notification - Thông báo](#10-notification---thông-báo)
11. [Application - Đơn từ](#11-application--đơn-từ)
12. [News - Tin tức](#12-news--tin-tức)
13. [Semester - Kỳ học](#13-semester---ky-hoc)
14. [Course class - Lớp học phần](#14-course-class---lớp-học-phần)
15. [Department - Bộ môn](#15-department---bộ-môn)
16. [Faculty - Khoa](#16-faculty---khoa)
17. [Lecturer - Giảng viên](#17-lecturer---giảng-viên)
18. [Academic advisor - Cố vấn học tập](#18-academic-advisor---cố-vấn-học-tập)
19. [Major - Ngành học](#19-major---ngành-học)
20. [Student Class - Lớp sinh viên](#20-student-class---lớp-sinh-viên)
21. [Subject - Môn học](#21-subject---môn-học)
22. [Tuition - Học phí](#22-tuition---học-phí)
23. [Notification Template - Mẫu thông báo](#23-notification-template---mẫu-thông-báo)
24. [Payment - Thanh toán](#24-payment---thanh-toán)

## 1. Response Format chung
Tất cả response đều theo cấu trúc JSON thống nhất:
```json
{
  "code": 0,
  "data": { ... },
  "message": "Operation completed successfully"
}
```  
| Field | Type | Description |
|------|-----|-----|
| code | int | Mã kết quả (>= 0: thành công, < 0: lỗi) |
| data | object | Dữ liệu trả về |
| message | string | Mô tả kết quả |
---
## 2. Mã lỗi (Response Codes)

| Code | HTTP Status | Ý nghĩa |
|------|-----|-----|
| 0 | 200 | Thành công |
| -1 | 400 | Input không hợp lệ |
| -2 | 404 | Không tìm thấy |
| -3 | 401 | Chưa xác thực / Token không hợp lệ |
| -4 | 403 | Bị từ chối |
| -5 | 400 | Lỗi validation |
| -10 | 500 | Lỗi server nội bộ |
| -13 | 502 | Lỗi external API |
| -25 | 409 | Đã tồn tại |
| -26 | 400 | Bad request |
---
## 3. Authentication
**Headers bắt buộc cho các route yêu cầu xác thực**  
  
> Authorization: Bearer &lt;JWT&gt;

JWT token được cấp sau khi đăng nhập thành công qua /api/v1/oauth2/login.  

---
## 4. OAuth2 – Đăng nhập
### 4.1. POST /api/v1/oauth2/login

Đăng nhập bằng Microsoft OAuth2 ID Token.

- **Auth**: Không yêu cầu
- **Content-Type**: application/json
**Request body**:  

```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiIs..."
}
```  

| Field | Type | Required | Description |
|------|-----|-----|-----|
| accessToken | string | ✅ | Microsoft OAuth2 Access Token từ Microsoft Azure AD |

**Response – Đăng nhập thành công (code 0):**:

```json
{
  "code": 0,
  "data": {
    "microsoftId": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
    "email": "abc@gmail.com",
    "name": "John Doe",
    "avatar": "https://graph.microsoft.com/...",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "message": "Login successful"
}
```  

**Response – Không tìm thấy user (code -2):**

```json
{
  "code": -2,
  "data": null,
  "message": "User not found"
}
```

**Response – Token không hợp lệ (code -3):**

```json
{
  "code": -3,
  "data": null,
  "message": "Invalid ID token"
}
```  
**Test cases:**

- ✅ idToken hợp lệ, user tồn tại → code 0 + JWT token
- ❌ idToken hợp lệ, user chưa tồn tại → code -2
- ❌ idToken rỗng / thiếu → code -1, HTTP 400
- ❌ idToken invalid / hết hạn → code -3, HTTP 401
---
## 5. Student – Quản lý thông tin
### 5.1. GET /api/v1/students/me
Sinh viên lấy thông tin cá nhân.
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng

**Response thành công (code 0):**

```json
{
    "code": 0,
    "message": "Student info retrieved successfully",
    "data": {
        "student_code": "SV2021001",
        "full_name": "Pham Minh Duc",
        "date_of_birth": "2003-05-10",
        "gender": "NAM",
        "class_code": "KHMT2021",
        "academic_advisor": "Nguyen Van An",
        "start_year": 2021,
        "end_year": 2026,
        "major": {
            "major_code": "KHMT",
            "major_name": "Khoa học máy tính",
            "faculty": "Công nghệ thông tin"
        },
        "training_type": "CHINH_QUY",
        "identity_card": {
            "card_number": "079203001111",
            "card_type": "CCCD",
            "issued_date": "2021-01-10",
            "issued_place": "Cục CS QLHC về TTXH - HCM"
        },
        "contact": {
            "phone_number": "0911111111",
            "address": "12 Nguyen Trai, HCM",
            "email": "duc.personal@gmail.com"
        },
        "academic_info": {
            "cohort": "K2021",
            "position": "Lớp trưởng"
        },
        "emergency_contact": {
            "name": "Pham Van Bo",
            "phone_number": "0981111111",
            "address": "12 Nguyen Trai, HCM",
            "relationship": null
        }
    }
}
```  

**Response – User chưa đăng nhập (code -3):**

```json
{
  "code": -3,
  "data": null,
  "message": "Authentication required"
}
```  
**Test cases:**

- ✅ token hợp lệ → code 0 + thông tin sinh viên
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ student id không tồn tại trong db → code -2, HTTP 404
---
### 5.2. GET /api/v1/students/me/class
Lấy thông tin lớp hành chính của sinh viên.
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng

**Response thành công (code 0):**:

```json
{
    "code": 0,
    "message": "Student class info retrieved successfully",
    "data": {
        "class_code": "KHMT2021",
        "major_name": "Khoa học máy tính",
        "start_year": 2021,
        "academic_advisor": {
            "lecturer_code": "GV001",
            "full_name": "Nguyen Van An",
            "phone_number": "0901234567",
            "email": "an.nguyen@university.edu.vn"
        },
        "students": [
            {
                "student_code": "SV2021001",
                "full_name": "Pham Minh Duc",
                "gender": "NAM"
            }
        ]
    }
}
```  

**Response – User chưa đăng nhập (code -3):**

```json
{
  "code": -3,
  "data": null,
  "message": "Authentication required"
}
```  
**Test cases:**

- ✅ token hợp lệ → code 0 + thông tin lớp hành chính của sinh viên
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ Không có lớp ứng với student id → code -2, HTTP 404
---  
### 5.3. POST /api/v1/admin/students/create
Thêm 1 sinh viên.
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: application/json

**Request body:**:

```json
{
  "student_code": "A46049",
  "full_name": "Nguyễn Văn An",
  "date_of_birth": "2007-01-25",
  "gender": "NAM",
  "student_class_code": "KHMT2021",
  "major_code": "KHMT",
  "start_year": 2024,
  "end_year": 2028,
  "training_type": "CHINH_QUY",

  "identity_card": {
    "card_number": "012345678901",
    "card_type": "CCCD",
    "issued_date": "2020-06-15",
    "issued_place": "..."
  },

  "contact": {
    "phone_number": "0912345678",
    "address": "...",
    "email": "..."
  },

  "academic_info": {
    "cohort": "2022-2026",
    "position": "Lớp trưởng"
  },

  "emergency_contact": {
    "name": "...",
    "phone_number": "...",
    "address": "...",
    "relationship": "Cha"
  }
}
```  
| Field | Type | Required | Description |
|------|-----|-----|-----|
| student_code | string | ✅ | Mã sinh viên |
| full_name | string | ✅ | Họ tên sinh viên |
| date_of_birth | string | ✅ | Ngày sinh |
| gender | string | ✅ | Giới tính |
| student_class_code | string | ✅ | Mã lớp hành chính |
| major_code | string | ✅ | Mã ngành học |
| start_year | int | ✅ | Năm bắt đầu học |
| end_year | int | ✅ | Năm kết thúc học |
| training_type | string | ✅ | Loại hình đào tạo |
| card_number | string | ✅ | Số thẻ căn cước |
| card_type | string | ✅ | Loại thẻ căn cước |
| issued_date | string | ❌ | Ngày cấp thẻ căn cước |
| issued_place | string | ❌ | Nơi cấp thẻ căn cước |
| phone_number | string | ❌ | Số điện thoại |
| address | string | ❌ | Địa chỉ |
| email | string | ❌ | Email |
| cohort | string | ✅ | Thông tin học tập |
| position | string | ❌ | Thông tin học tập |
| name | string | ❌ | Thông tin liên hệ khẩn cấp |
| phone_number | string | ❌ | Thông tin liên hệ khẩn cấp |
| address | string | ❌ | Địa chỉ |
| relationship | string | ❌ | Thông tin liên hệ khẩn cấp |
**Response thành công (code 0):**:

```json
{
  "code": 0,
  "message": "Student created successfully",
  "data": 9
}
```  

**Response – User chưa đăng nhập (code -3):**

```json
{
  "code": -3,
  "data": null,
  "message": "Authentication required"
}
```  

**Response – Student code đã tồn tại (code -25):**

```json
{
    "code": -25,
    "message": "Student code already exists",
    "data": null
}
```

**Response – Trường thông tin sai hoặc bỏ trống (code -1):**

```json
{
    "code": -1,
    "message": "Giới tính không được để trống",
    "data": null
}
```
**Test cases:**

- ✅ token hợp lệ → code 0 + id sinh viên
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ Student code already exists → code -25, HTTP 409
- ❌ Trường thông tin sai hoặc bỏ trống → code -1, HTTP 400
---  

### 5.4. POST /api/v1/admin/students/import
Import danh sách sinh viên từ file xlsx/csv.
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: multipart/form-data

**Form data field:**

| Field | Type | Required | Description |
|------|-----|-----|-----|
| file | File | ✅ | File chứa data sinh viên |

**Response thành công (code 0):**

```json
```json
{
    "code": 0,
    "message": "File imported successfully",
    "data": {
        "total": 2,
        "success": 2,
        "failed": 0
    }
}
```

**Response – User chưa đăng nhập (code -3):**

```json
{
  "code": -3,
  "data": null,
  "message": "Authentication required"
}
```

**Response – Sai định dạng file (code -1):**

```json
{
  "code": -1,
  "message": "File must be CSV or Excel (.csv, .xlsx, .xls)",
  "data": null
}
```

**Response – File không có tên (code -1):**

```json
{
  "code": -1,
  "message": "File name is null",
  "data": null
}
```

**Response – Thiếu file (code -1):**

```json
{
  "code": -1,
  "message": "File is missing",
  "data": null
}
```


**Test cases:**

- ✅ token hợp lệ → code 0 + thông tin import
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ Sai định dạng file → code -1, HTTP 400
- ❌ File Excel không đúng định dạng → code -1, HTTP 400
- ❌ File Excel có dữ liệu không hợp lệ → code -1, HTTP 400
---  
### 5.5. GET /api/v1/admin/students/all
Lấy danh sách tất cả sinh viên.
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng
- **Query param (optional)**:

| Field | Type | Required | Description |
|------|-----|-----|-----|
| page | int | ❌ | Số trang (mặc định: 0), page size cố định 50 |

**Response thành công (code 0):**

```json
{
    "code": 0,
    "message": "Get all students successfully",
    "data": {
        "content": [
             {
            "id": 1,
            "student_code": "SV2022002",
            "full_name": "Dang Van Minh",
            "date_of_birth": "2004-03-07",
            "gender": "NAM",
            "class_code": "KTPM2022",
            "major_code": "KTPM",
            "academic_advisor": null,
            "start_year": 2022,
            "end_year": 2026,
            "training_type": "CHINH_QUY",
            "identity_card": { 
              "card_number": "079203005555",
              "card_type": "CCCD",
              "issued_date": "2022-02-28",
              "issued_place": "Cục CS QLHC về TTXH - HCM"
             },
            "contact": { 
              "phone_number": "0955555555",
              "address": "90 Le Duan, HCM",
              "email": "minh.personal@gmail.com"
             },
            "academic_info": { 
              "cohort": "K2021",
              "position": null
             },
            "emergency_contact": { 
              "name": "Dang Van Gio",
              "phone_number": "0985555555",
              "address": "90 Le Duan, HCM",
              "relationship": null
             }
          }
        ],
        "page": 0,
        "size": 50,
        "total_elements": 6,
        "total_pages": 1,
        "first": true,
        "last": true
    }
}
```

**Response – User chưa đăng nhập (code -3):**

```json
{
  "code": -3,
  "data": null,
  "message": "Authentication required"
}
```

**Test cases:**

- ✅ token hợp lệ → code 0 + danh sách sinh viên
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
---  

### 5.6. POST /api/v1/admin/students/update/`{studentId}`/basic

Cập nhật thông tin cá nhân sinh viên.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: application/json

**Request body:**
```json
{
  "student_code": "SV001",
  "full_name": "Nguyen Van A",
  "date_of_birth": "2000-01-01",
  "gender": "NAM",
  "card_number": "123456789",
  "card_type": "CCCD",
  "issued_date": "2020-01-01",
  "issued_place": "Hà Nội",
  "phone_number": "0912345678",
  "address": "Hà Nội",
  "email": "abc@gmail.com",
  "emergency_contact_name": "Nguyen Van B",
  "emergency_contact_phone_number": "0987654321",
  "emergency_contact_address": "Hà Nội",
  "emergency_contact_relationship": "Cha"
}
```

**Response thành công (code 0):**

```json
{
    "code": 0,
    "message": "Student updated successfully",
    "data": 1
}
```

**Response – User chưa đăng nhập (code -3):**

```json
{
  "code": -3,
  "data": null,
  "message": "Authentication required"
}
```

**Response – Student code đã tồn tại (code -25):**

```json
{
    "code": -25,
    "message": "Student code already exists",
    "data": null
}
```

**Test cases:**

- ✅ token hợp lệ → code 0 + id sinh viên
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ Student code already exists → code -25, HTTP 409
- ❌ Trường thông tin sai hoặc bỏ trống → code -1, HTTP 400
---

### 5.7. POST /api/v1/admin/students/update/`{studentId}`/academic

Cập nhật thông tin học tập.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: application/json

**Request body:**
```json
{
  "student_class_code": "KHMT2021",
  "major_code": "KHMT",
  "training_type": "CHINH_QUY",
  "start_year": 2021,
  "end_year": 2025,
  "cohort": "K2021",
  "position": "Lớp trưởng"
}
```

**Response thành công (code 0):**

```json
{
    "code": 0,
    "message": "Student academic info updated successfully",
    "data": 1
}
```

**Response – User chưa đăng nhập (code -3):**

```json
{
  "code": -3,
  "data": null,
  "message": "Authentication required"
}
```

**Response – Student code đã tồn tại (code -25):**

```json
{
    "code": -25,
    "message": "Student code already exists",
    "data": null
}
```

**Test cases:**

- ✅ token hợp lệ → code 0 + id sinh viên
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ Student code already exists → code -25, HTTP 409
- ❌ Trường thông tin sai hoặc bỏ trống → code -1, HTTP 400
---

### 5.8. POST /api/v1/admin/students/delete/`{studentId}`

Xóa sinh viên.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Student deleted successfully",
  "data": 1
}
```

**Test cases:**

- ✅ token hợp lệ → code 0 + id sinh viên
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
---

## 6. Study Program - Chương trình đào tạo
### 6.1. GET /api/v1/study-programs
Lấy thông tin chương trình đào tạo của các ngành sinh viên đang theo học.  
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng

**Response thành công (code 0):**:

```json
{
    "code": 0,
    "message": "Study programs retrieved successfully",
    "data": [
        {
            "student_code": "SV2021001",
            "study_program_code": "CTDT-KHMT-2024",
            "study_program_name": "Chương trình đào tạo KHMT 2024",
            "is_primary": true,
            "start_year": 2022
        },
        {
            "student_code": "SV2021001",
            "study_program_code": "CTDT-HTTT-2024",
            "study_program_name": "Chương trình đào tạo HTTT 2024",
            "is_primary": false,
            "start_year": 2022
        }
    ]
}
```  

**Response – User chưa đăng nhập (code -3):**

```json
{
  "code": -3,
  "data": null,
  "message": "Authentication required"
}
```  
**Test cases:**

- ✅ token hợp lệ → code 0 + thông tin chương trình đào tạo của các ngành sinh viên đang theo học
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
---  
### 6.2. GET /api/v1/study-programs/`{studyProgramCode}`
Lấy thông tin chi tiết chương trình đào tạo.  
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng
- **Path param:**

| Field | Type | Required | Description |
|------|-----|-----|-----|
| studyProgramCode | string | ✅ | Mã chương trình đào tạo |
**Response thành công (code 0):**:

```json
{
  "code": 0,
  "message": "Study program retrieved successfully",
  "data": {
    "study_program_name": "Chương trình đào tạo KHMT 2024",
    "study_program_code": "CTDT-KHMT-2024",
    "year_start": 2024,
    "total_credits": 132,
    "major": {
      "major_name": "Khoa học máy tính",
      "major_code": "KHMT",
      "faculty": "Công nghệ thông tin"
    },
    "semesters": [
      {
        "semester_name": "HK1 2025-2026",
        "semester_start_date": "2025-09-01",
        "semester_end_date": "2026-01-15",
        "subjects": [
          {
            "id": 1,
            "subject_code": "INT1001",
            "subject_name": "Nhập môn lập trình",
            "credits": 3,
            "is_required": true,
            "elective_group": null,
            "lecture_hours": 30,
            "practice_hours": 15,
            "subject_prerequisite": [
              {
                "id": 1,
                "min_subjects_required": 1,
                "description": "Hoàn thành 1 trong các môn sau",
                "items": [
                  {
                    "subject_code": "INT0001",
                    "subject_name": "Tin học cơ bản"
                  }
                ]
              }
            ],
            "faculty": "Công nghệ thông tin",
            "department": "Khoa học máy tính"
          }
        ]
      }
    ]
  }
}
```  

**Response – User chưa đăng nhập (code -3):**

```json
{
  "code": -3,
  "data": null,
  "message": "Authentication required"
}
```  
**Test cases:**

- ✅ token hợp lệ + studyProgramCode hợp lệ → code 0 + thông tin chương trình đào tạo
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ studyProgramCode không tồn tại trong db → code -2, HTTP 404
---

### 6.3. GET /api/v1/admin/study-programs/all
Lấy danh sách chương trình đào tạo (Admin).

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Query param**:

| Field | Type | Required | Description |
|------|-----|-----|-----|
| page | int | ❌ | Số trang (mặc định 0) |
| size | int | ❌ | Số phần tử mỗi trang (mặc định 10) |
| start_year | int | ✅ | Năm bắt đầu |

---

**Response thành công (code 0):**
```json
{
  "code": 0,
  "message": "Study programs retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "study_program_code": "CTDT-KHMT-2024",
        "study_program_name": "Chương trình KHMT 2024",
        "major_code": "KHMT",
        "start_year": 2024,
        "total_credits": 132,
        "training_type": "CHINH_QUY"
      }
    ],
    "page": 0,
    "size": 10,
    "total_elements": 1,
    "total_pages": 1,
    "first": true,
    "last": true
  }
}
```
---
### 6.4. GET /api/v1/admin/study-programs/`{id}`

Lấy chi tiết chương trình đào tạo theo ID.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)

**Response:**
```json
{
  "code": 0,
  "message": "Study program retrieved successfully",
  "data": {
    "study_program_name": "Chương trình đào tạo KHMT 2024",
    "study_program_code": "CTDT-KHMT-2024",
    "year_start": 2024,
    "total_credits": 132,
    "major": {
      "major_name": "Khoa học máy tính",
      "major_code": "KHMT",
      "faculty": "Công nghệ thông tin"
    },
    "semesters": [
      {
        "semester_name": "HK1 2025-2026",
        "semester_start_date": "2025-09-01",
        "semester_end_date": "2026-01-15",
        "subjects": [
          {
            "id": 1,
            "subject_code": "INT1001",
            "subject_name": "Nhập môn lập trình",
            "credits": 3,
            "is_required": true,
            "elective_group": null,
            "lecture_hours": 30,
            "practice_hours": 15,
            "subject_prerequisite": [
              {
                "id": 1,
                "min_subjects_required": 1,
                "description": "Hoàn thành 1 trong các môn sau",
                "items": [
                  {
                    "subject_code": "INT0001",
                    "subject_name": "Tin học cơ bản"
                  }
                ]
              }
            ],
            "faculty": "Công nghệ thông tin",
            "department": "Khoa học máy tính"
          }
        ]
      }
    ]
  }
}
```
---
### 6.5. POST /api/v1/admin/study-programs

Tạo chương trình đào tạo.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: application/json

**Request body:**
```json
{
  "study_program_code": "CTDT-KHMT-2024",
  "study_program_name": "Chương trình KHMT 2024",
  "major_id": 1,
  "start_year": 2024,
  "total_credits": 132,
  "training_type": "CHINH_QUY"
}
```

**Response:**
```json
{
  "code": 0,
  "message": "Study program created successfully",
  "data": 1
}
```
---

### 6.6. POST /api/v1/admin/study-programs/update/`{id}`

Cập nhật chương trình đào tạo.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)

**Request body (tất cả optional):**
```json
{
  "study_program_code": "CTDT-KHMT-2025",
  "study_program_name": "CTDT mới",
  "major_id": 2,
  "start_year": 2025,
  "total_credits": 140,
  "training_type": "CHINH_QUY"
}
```

**Response:**
```json
{
  "code": 0,
  "message": "Study program updated successfully",
  "data": null
}
```
---
### 6.7. POST /api/v1/admin/study-programs/delete/`{id}`

Xóa chương trình đào tạo.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)

**Response:**
```json
{
  "code": 0,
  "message": "Study program deleted successfully",
  "data": null
}
```
---
### 6.8. POST /api/v1/admin/study-programs/`{id}`/subjects/create

Thêm môn học vào CTĐT.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)

**Request body:**
```json
{
  "subject_id": 1,
  "semester_id": 1,
  "elective_group": "GROUP_1",
  "is_required": true
}
```

**Response:**
```json
{
  "code": 0,
  "message": "Subject added to study program successfully",
  "data": null
}
```
---
### 6.9. POST /api/v1/admin/study-programs/subjects/update/`{id}`

Cập nhật môn học trong CTĐT.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)

**Request body:**
```json
{
  "semester_id": 2,
  "is_required": false,
  "elective_group": "GROUP_2"
}
```

**Response:**
```json
{
  "code": 0,
  "message": "Subject updated to study program successfully",
  "data": null
}
```
---
### 6.10. POST /api/v1/admin/study-programs/subjects/delete/`{id}`

Xóa môn khỏi CTĐT.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)

**Response:**
```json
{
  "code": 0,
  "message": "Subject removed from study program successfully",
  "data": null
}
```
---
### Test cases
- ✅ Token hợp lệ → code 0
- ❌ Token thiếu / invalid → code -3
- ❌ ID không tồn tại → code -2
- ❌ Validation fail → code -1
---

## 7. Schedule - Lịch học
### 7.1. GET /api/v1/student/schedules/day-of-week
Lấy thông tin lịch học từng ngày trong tuần của sinh viên.  
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng
- **Query param (optional)**:  

| Field | Type | Required | Description |
|------|-----|-----|-----|
| day_of_week | int | ❌ | Thứ 1-7 (1: Monday, 2: Tuesday, ... 7: Sunday) |  


**Ví dụ request:**
```http
GET /api/v1/student/schedules/day-of-week?day_of_week=1
```  

**Response thành công (code 0):**:
```json
{
    "code": 0,
    "message": "Day of week schedule retrieved successfully",
    "data": {
        "course_classes": [
            {
                "class_code": "INT1002-01",
                "day_of_week": 2,
                "subject_name": "Cấu trúc dữ liệu & giải thuật",
                "subject_code": "INT1002",
                "start_period": 6,
                "end_period": 8,
                "start_time": "11:30:00",
                "end_time": "14:00:00",
                "room": "B201",
                "lecturer": {
                    "lecturer_code": "GV001",
                    "full_name": "Nguyen Van An",
                    "phone_number": "0901234567",
                    "email": "an.nguyen@university.edu.vn"
                }
            }
        ]
    }
}
```  

**Response – User chưa đăng nhập (code -3):**

```json
{
  "code": -3,
  "data": null,
  "message": "Authentication required"
}
```  
**Test cases:**

- ✅ token hợp lệ + dayOfWeek hợp lệ → code 0 + thông tin lịch học
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ dayOfWeek không hợp lệ → code -2, HTTP 404
  
### 7.2. GET /api/v1/student/schedules/weekly
Lấy thông tin lịch học theo tuần của sinh viên.  
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng
- **Query param (optional)**: 
| Field | Type | Required | Description |
|------|-----|-----|-----|
| start_date | string | ❌ | Ngày bắt đầu của tuần (YYYY-MM-DD) |  
| end_date | string | ❌ | Ngày kết thúc của tuần (YYYY-MM-DD) |  

**Ví dụ request:**
```
GET /api/v1/student/schedules/weekly?start_date=2022-02-01&end_date=2022-09-01
```  
  
**Response thành công (code 0):**:
```json
{
    "code": 0,
    "message": "Weekly schedule retrieved successfully",
    "data": {
        "semester": "HK1 2025-2026",
        "week": 18,
        "start_date": "2026-01-01",
        "end_date": "2026-09-01",
        "daily_schedules": [
            {
                "course_classes": [
                    {
                        "class_code": "INT1001-01",
                        "day_of_week": 6,
                        "subject_name": "Nhập môn lập trình",
                        "subject_code": "INT1001",
                        "start_period": 1,
                        "end_period": 3,
                        "start_time": "07:00:00",
                        "end_time": "09:30:00",
                        "room": "A101",
                        "lecturer": {
                            "lecturer_code": null,
                            "full_name": "Nguyen Van An",
                            "phone_number": null,
                            "email": "an.nguyen@university.edu.vn"
                        }
                    }
                ]
            }
        ]
    }
}
```  

**Response – User chưa đăng nhập (code -3):**

```json
{
  "code": -3,
  "data": null,
  "message": "Authentication required"
}
```  

**Response – Sai định dạng ngày (code -1):**

```json
{
  "code": -1,
  "data": null,
  "message": "Invalid date format"
}
``` 
**Test cases:**

- ✅ token hợp lệ + ngày hợp lệ → code 0 + thông tin lịch học
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ sai định dạng ngày → code -1, HTTP 400
- ❌ không tồn tại học kỳ thích hợp trong db → code -2, HTTP 404  

### 7.3. GET /api/v1/student/schedules/semester
Lấy thông tin lịch học theo học kỳ của sinh viên.  
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng
- **Query param**: 
| Field | Type | Required | Description |
|------|-----|-----|-----|
| HocKy | string | ✅ | Mã học kỳ |  

**Ví dụ request:**
```
GET /api/v1/student/schedules/semester?HocKy=HK1 2022-2023
```  

**Response thành công (code 0):**:
```json
{
    "code": 0,
    "message": "Semester schedule retrieved successfully",
    "data": {
        "semester": "HK1 2025-2026",
        "course_classes": [
            {
                "class_code": "INT1001-01",
                "day_of_week": 6,
                "subject_name": "Nhập môn lập trình",
                "subject_code": "INT1001",
                "start_period": 1,
                "end_period": 3,
                "start_time": "07:00:00",
                "end_time": "09:30:00",
                "room": "A101",
                "lecturer": {
                    "lecturer_code": null,
                    "full_name": "Nguyen Van An",
                    "phone_number": null,
                    "email": "an.nguyen@university.edu.vn"
                }
            }
        ]
    }
}
```  

**Response – User chưa đăng nhập (code -3):**

```json
{
  "code": -3,
  "data": null,
  "message": "Authentication required"
}
```    
**Test cases:**

- ✅ token hợp lệ + học kỳ hợp lệ → code 0 + thông tin lịch học
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ không tồn tại học kỳ thích hợp trong db → code -2, HTTP 404
---
### 7.4. GET /api/v1/admin/schedules/`{courseClassId}`
Lấy chi tiết lịch học của lớp học phần.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Path param**:

| Field | Type | Required | Description |
|------|-----|-----|-----|
| courseClassId | long | ✅ | ID lớp học phần |

**Response thành công (code 0):**
```json
{
  "code": 0,
  "message": "Get detail schedule successfully",
  "data": [
    {
      "id": 1,
      "dayOfWeek": 2,
      "startPeriod": 1,
      "endPeriod": 3,
      "startTime": "07:00:00",
      "endTime": "09:30:00",
      "room": "A101"
    }
  ]
}
```

**Test cases:**

- ✅ token hợp lệ + courseClassId hợp lệ → code 0 + thông tin lịch học
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ courseClassId không tồn tại → code -2, HTTP 404
---
### 7.5. POST /api/v1/admin/schedules/create/`{courseClassId}`

Tạo mới lịch học cho lớp học phần.

- **Auth**: Bắt buộc
- **Content-Type**: application/json

**Request body:**

```json
[
  {
    "id": 1,
    "dayOfWeek": 2,
    "startPeriod": 1,
    "endPeriod": 3,
    "startTime": "07:00:00",
    "endTime": "09:30:00",
    "room": "A101"
  }
]
```

| Field | Type | Required | Description |
|------|-----|-----|-----|
| id | long | ✅ | ID schedule |
| dayOfWeek | int | ✅ | Thứ trong tuần (1-7) |
| startPeriod | int | ✅ | Tiết bắt đầu |
| endPeriod | int | ✅ | Tiết kết thúc |
| startTime | string | ✅ | Giờ bắt đầu (HH:mm:ss) |
| endTime | string | ✅ | Giờ kết thúc |
| room | string | ✅ | Phòng học |

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Create schedule successfully",
  "data": null
}
```

**Test cases:**

- ✅ token hợp lệ + courseClassId hợp lệ → code 0 + thông tin lịch học
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ courseClassId không tồn tại → code -2, HTTP 404
---
### 7.6. PUT /api/v1/admin/schedules/update/`{courseClassId}`

Cập nhật lịch học.

- **Auth**: Bắt buộc
- **Content-Type**: application/json

**Request body:**

```json
{
  "dayOfWeek": 2,
  "startPeriod": 4,
  "endPeriod": 6,
  "startTime": "09:30:00",
  "endTime": "12:00:00",
  "room": "B201"
}
```

| Field | Type | Required | Description |
|------|-----|-----|-----|
| dayOfWeek | int | ❌ | Thứ trong tuần (1-7) |
| startPeriod | int | ❌ | Tiết bắt đầu |
| endPeriod | int | ❌ | Tiết kết thúc |
| startTime | string | ❌ | Giờ bắt đầu (HH:mm:ss) |
| endTime | string | ❌ | Giờ kết thúc |
| room | string | ❌ | Phòng học |


**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Update schedule successfully",
  "data": null
}
```

**Test cases:**

- ✅ token hợp lệ + courseClassId hợp lệ → code 0 + thông tin lịch học
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ courseClassId không tồn tại → code -2, HTTP 404
---
### 7.7. DELETE /api/v1/admin/schedules/delete/`{courseClassId}`

Xóa toàn bộ lịch học của lớp học phần.

- **Auth**: Bắt buộc

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Delete schedule successfully",
  "data": null
}
```

**Test cases:**

- ✅ courseClassId hợp lệ → code 0
- ❌ courseClassId không tồn tại → code -2
- ❌ dữ liệu invalid (time/period) → code -5
- ❌ chưa đăng nhập → code -3
---
## 8. Exam - Lịch thi
### 8.1. GET /api/v1/student/exams
Lấy thông tin lịch thi của sinh viên.  
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng
- **Query param**: 
| Field | Type | Required | Description |
|------|-----|-----|-----|
| HocKy | string | ✅ | Mã học kỳ |  
  
**Ví dụ request:**
```
GET /api/v1/student/exams?HocKy=HK1 2022-2023
```  
**Response thành công (code 0):**:
```json
{
    "code": 0,
    "message": "HK1 2025-2026",
    "data": {
        "semester_name": "HK1 2025-2026",
        "exam_schedules": [
            {
                "subject_code": "INT1001",
                "subject_name": "Nhập môn lập trình",
                "class_code": "INT1001-01",
                "exam_date": "2022-01-10",
                "start_time": "07:30:00",
                "end_time": "09:30:00",
                "exam_room": "P101",
                "exam_location": "Co so 1",
                "exam_format": "TRAC_NGHIEM",
                "exam_type": "GIUA_KY",
                "exam_attempt": 1,
                "attendance_status": "ATTENDED"
            }
        ]
    }
}
```  

**Response – User chưa đăng nhập (code -3):**

```json
{
  "code": -3,
  "data": null,
  "message": "Authentication required"
}
```    
**Test cases:**

- ✅ token hợp lệ + học kỳ hợp lệ → code 0 + thông tin lịch thi
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ không tồn tại học kỳ thích hợp trong db → code -2, HTTP 404

**Ví dụ request:**
```
GET /api/v1/admin/exam?semesterId=1&facultyId=1&page=0&size=10
```
**Response thành công (code 0):**
```json
{
    "code": 0,
    "message": "Get exam schedule successfully",
    "data": {
        "page": 0,
        "size": 10,
        "total_elements": 10,
        "total_pages": 1,
        "content": [
            {
                "id": 1,
                "subject_code": "INT1001",
                "subject_name": "Nhập môn lập trình",
                "class_code": "INT1001-01",
                "exam_date": "2022-01-10",
                "start_time": "07:30:00",
                "end_time": "09:30:00",
                "exam_room": "P101",
                "exam_location": "Co so 1",
                "exam_format": "TRAC_NGHIEM",
                "exam_type": "GIUA_KY",
                "exam_attempt": 1,
                "attendance_status": "ATTENDED"
            }
        ]
    }
}
```
**Response – User chưa đăng nhập (code -3):**
```json
{
  "code": -3,
  "data": null,
  "message": "Authentication required"
}
```
**Test cases:**

- ✅ token hợp lệ + học kỳ hợp lệ → code 0 + thông tin lịch thi
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ không tồn tại học kỳ thích hợp trong db → code -2, HTTP 404
---
### 8.2. GET /api/v1/admin/exam
Lấy danh sách lịch thi (phân trang + filter).
- **Auth**: Bắt buộc (Admin)
- **Query param**:
| Field | Type | Required | Description |
|------|-----|-----|-----|
| semesterId | long | ✅ | ID học kỳ |
| facultyId | long | ❌ | ID khoa |
| page | int | ❌ | Trang (default = 0) |
| size | int | ❌ | Số phần tử (default = 10) |

**Response thành công (code 0):**
```json
{
  "code": 0,
  "message": "Get exam schedule successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "subjectCode": "INT1001",
        "subjectName": "Nhập môn lập trình",
        "examDate": "2026-01-10",
        "startTime": "08:00:00",
        "endTime": "10:00:00",
        "examRoom": "A101",
        "examLocation": "Cơ sở 1",
        "examFormat": "Offline",
        "examType": "Final",
        "note": ""
      }
    ],
    "page": 0,
    "size": 10,
    "total_elements": 1,
    "total_pages": 1,
    "first": true,
    "last": true
  }
}
```
---
### 8.3. POST /api/v1/admin/exam/create
Tạo lịch thi.
- **Auth**: Bắt buộc (Admin)
- **Content-Type**: application/json

**Request body:**
```json
{
  "subjectId": 1,
  "semesterId": 5,
  "examDate": "2026-01-10",
  "startTime": "08:00:00",
  "endTime": "10:00:00",
  "examRoom": "A101",
  "examLocation": "Cơ sở 1",
  "examFormat": "Offline",
  "examType": "Final",
  "note": "Thi tập trung"
}
```
| Field | Type | Required | Description |
|------|-----|-----|-----|
| subjectId | long | ✅ | ID môn học |
| semesterId | long | ✅ | ID học kỳ |
| examDate | date | ✅ | Ngày thi |
| startTime | time | ✅ | Giờ bắt đầu |
| endTime | time | ✅ | Giờ kết thúc |
| examRoom | string | ✅ | Phòng thi |
| examLocation | string | ❌ | Địa điểm |
| examFormat | string | ❌ | Hình thức (Online/Offline) |
| examType | string | ❌ | Loại thi |
| note | string | ❌ | Ghi chú |

**Response:**
```json
{
  "code": 0,
  "message": "Create exam schedule successfully",
  "data": 1
}
```
---
### 8.4. POST /api/v1/admin/exam/update/`{id}`

Cập nhật lịch thi.

- **Auth**: Bắt buộc (Admin)
- **Content-Type**: application/json

**Path param:**

| Field | Type | Description |
|------|-----|-----|
| id | long | ID lịch thi |

**Request body (optional fields):**
```json
{
  "subjectId": 2,
  "examDate": "2026-01-12",
  "startTime": "09:00:00",
  "endTime": "11:00:00",
  "examRoom": "B202"
}
```

**Response:**
```json
{
  "code": 0,
  "message": "Update exam schedule successfully",
  "data": null
}
```
--- 
### 8.5. POST /api/v1/admin/exam/delete/`{id}`

Xóa lịch thi.

- **Auth**: Bắt buộc (Admin)

**Response:**
```json
{
  "code": 0,
  "message": "Delete exam schedule successfully",
  "data": null
}
```
---  
## 9. Mark - Kết quả học tập
### 9.1. GET /api/v1/student/marks
Lấy thông tin kết quả học tập của sinh viên.  
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng
- **Query param**: 
| Field | Type | Required | Description |
|------|-----|-----|-----|
| ctdt | string | ✅ | Tên chương trình đào tạo |  

**Ví dụ request:**
```
GET /api/v1/student/marks?ctdt=CTDT-KHMT-2021
```  
**Response thành công (code 0):**:
```json
{
"code": 0,
"message": "Academic result fetched successfully",
"data": {
    "study_program": "CTDT-KHMT-2025",
        "semester_results": [
            {
                "semester": "HK1 2026-2027",
                "subject_results": [
                    {
                        "subject_code": "INT2001",
                        "subject_name": "Cơ sở dữ liệu",
                        "credits": 3,
                        "attendance_score": 10.00,
                        "midterm_score": 7.00,
                        "final_score": 7.50,
                        "score10": 6.50,
                        "score4": 2.50,
                        "letter_grade": "C",
                        "is_pass": true
                    }
                ],
                "semester_summary": {
                    "credits_registered": null,
                    "credits_passed": 6,
                    "semester_gpa": 2.80,
                    "conduct_score": 80,
                    "cumulative_gpa": 3.10
                }
            }
        ]
    }
}
```  

**Response – User chưa đăng nhập (code -3):**

```json
{
  "code": -3,
  "data": null,
  "message": "Authentication required"
}
```    
**Test cases:**

- ✅ token hợp lệ + chương trình đào tạo hợp lệ → code 0 + thông tin kết quả học tập
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ không tồn tại chương trình đào tạo thích hợp trong db → code -2, HTTP 404
---  

### 9.2. POST /api/v1/admin/academic-results/create
Tạo kết quả học tập cho sinh viên.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: application/json

**Request body:**
```json
{
  "studentId": 1,
  "subjectId": 10,
  "semesterId": 5,
  "credits": 3,
  "attendanceScore": 10,
  "midtermScore": 8,
  "finalScore": 7,
  "score10": 8.5,
  "score4": 3.5,
  "letterGrade": "A",
  "isPass": true
}
```

**Field:**

| Field | Type | Required | Description |
| ----- | ---- | -------- | ----------- |
| studentId | long | ✅ | ID sinh viên |
| subjectId | long | ✅ | ID môn học |
| semesterId | long | ✅ | ID học kỳ |
| credits | int | ✅ | Số tín chỉ |
| attendanceScore | decimal | ✅ | Điểm chuyên cần |
| midtermScore | decimal | ✅ | Điểm giữa kỳ |
| finalScore | decimal | ✅ | Điểm cuối kỳ |
| score10 | decimal | ✅ | Điểm hệ 10 |
| score4 | decimal | ✅ | Điểm hệ 4 |
| letterGrade | string | ✅ | Điểm chữ (A-F) |
| isPass | boolean | ✅ | Đạt / Không đạt |

**Response thành công (code 0):**
```json
Response thành công (code 0):

{
  "code": 0,
  "message": "Student subject result created successfully",
  "data": 1
}
```
**Response lỗi:**

```json
{
  "code": -1,
  "data": null,
  "message": "Validation failed"
}
```
---  
### 9.3. POST /api/v1/admin/academic-results/import
Import kết quả học tập từ file.

- **Auth**: Bắt buộc
- **Content-Type**: multipart/form-data

**Form data:**
| Field | Type | Required | Description |
|------|-----|-----|-----|
| file | File | ✅ | File CSV/Excel |

**Cấu trúc file import (header):**

| Cột | Bắt buộc |
| ----- | -------- |
| Mã sinh viên | ✅ |
| Mã môn học | ✅ |
| Học kỳ | ✅ |
| Điểm chuyên cần | ❌ |
| Điểm giữa kỳ | ❌ |
| Điểm cuối kỳ | ❌ |
| Điểm hệ số 10 | ❌ |
| Điểm hệ số 4 | ❌ |
| Loại | ❌ |
| Đạt | ❌ |

**Response:**
```json
{
  "code": 0,
  "message": "Academic result imported successfully",
  "data": {
    "total": 100,
    "success": 95,
    "failed": 5,
    "errors": []
  }
}
```
---  

### 9.4. POST /api/v1/admin/academic-results/update/`{id}`
Cập nhật kết quả học tập.

- **Auth**: Bắt buộc
- **Content-Type**: application/json

**Request body (tất cả optional):**
```json
{
  "semesterId": 6,
  "attendanceScore": 9,
  "midtermScore": 7,
  "finalScore": 8,
  "score10": 8.2,
  "score4": 3.2,
  "letterGrade": "B",
  "isPass": true
}
```  

**Path param:**
| Field | Type | Description |
|------|-----|-----|
| id | Long | ID kết quả |

**Response:**
```json
{
  "code": 0,
  "message": "Student subject result updated successfully",
  "data": null
}
```
---

### 9.5. POST /api/v1/admin/academic-results/delete/`{id}`
Xóa kết quả học tập.

- **Auth**: Bắt buộc

**Path param:**
| Field | Type | Description |
|------|-----|-----|
| id | Long | ID kết quả |

**Response:**
```json
{
  "code": 0,
  "message": "Student subject result deleted successfully",
  "data": null
}
```
---

## 10. Notification - Thông báo
### 10.1. GET /api/v1/notification/prepare

Lấy thông tin chuẩn bị để filter notification (context của user).

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng

**Response thành công (code 0):**
```json
{
  "code": 0,
  "message": "Get prepare notification successfully",
  "data": {
    "studentClassId": 1,
    "oauthUserId": 10,
    "facultyId": 2,
    "courseClassIds": [101, 102],
    "topics": ["SYSTEM", "ACADEMIC"]
  }
}
```
### 10.2. POST /api/v1/notification
Lấy tất cả thông tin thông báo của sinh viên.  
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng

**Request body:**
```json
{
  "oauthUserId": 10,
  "facultyId": 2,
  "studentClassId": 1,
  "courseClassIds": [101, 102]
}
```

**Query params:**
| Field | Type | Required | Description |
|------|-----|-----|-----|
| page | int | ❌ | default = 0 |
| size | int | ❌ | default = 10 |

**Response thành công (code 0):**:
```json
{
  "code": 0,
  "message": "Get all notification successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "Thông báo học phí",
        "isRead": false,
        "createdAt": "2026-04-01T10:00:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10
  }
}
```  

**Response – User chưa đăng nhập (code -3):**

```json
{
    "code": -3,
    "data": null,
    "message": "Authentication required"
}
```    
**Test cases:**

- ✅ token hợp lệ → code 0 + thông tin thông báo
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401

### 10.3. GET /api/v1/notification/id
Lấy thông tin chi tiết thông báo.
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng
- **Path param**:
| Field | Type | Required | Description |
|------|-----|-----|-----|
| id | string | ✅ | ID thông báo |  

**Response thành công (code 0):**:
```json
{
    "code": 0,
    "message": "Get detail notification successfully",
    "data": {
        "title": "Cap nhat cong thong tin",
        "content": "Da cap nhat giao dien moi",
        "createdBy": "Admin",
        "targetType": "GLOBAL",
        "deadLine": null,
        "referenceType": null,
        "referenceId": null,
        "createdAt": "2026-04-02T14:10:36.333465"
    }
}
```  

**Response – User chưa đăng nhập (code -3):**

```json
{
    "code": -3,
    "data": null,
    "message": "Authentication required"
}
```    
**Test cases:**

- ✅ token hợp lệ + thông báo tồn tại → code 0 + thông tin thông báo
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ thông báo không tồn tại → code -2, HTTP 404
---

### 10.4. POST /api/v1/notification/unread-count

Đếm số notification chưa đọc.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng

**Request body:**
```json
{
  "oauthUserId": 10,
  "facultyId": 2,
  "studentClassId": 1,
  "courseClassIds": [101, 102]
}
```

**Response thành công (code 0):**
```json
{
  "code": 0,
  "message": "Count unread notification successfully",
  "data": {
    "count": 5
  }
}
```
---
### 10.5. GET /api/v1/admin/notification/all

Lấy danh sách tất cả notification (admin).

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng

**Query params:**
| Field | Type | Required | Description |
|------|-----|-----|-----|
| page | int | ❌ | default = 0 |
| size | int | ❌ | default = 10 |

**Response thành công (code 0):**
```json
{
  "code": 0,
  "message": "Get all notification successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "Thông báo học phí",
        "content": "Đóng học phí trước ngày...",
        "createdBy": "Admin",
        "targetType": "STUDENT",
        "targetId": 1001,
        "referenceId": 55,
        "referenceType": "TUITION",
        "deadLine": "2026-04-10",
        "isImportant": true
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 50,
    "totalPages": 5
  }
}
```
---
### 10.6. POST /api/v1/admin/notification/send

Gửi notification.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: application/json
**Request body:**
```json
{
  "templateId": 1,
  "title": "Thông báo học phí",
  "content": "Bạn cần đóng học phí",
  "targetType": "STUDENT",
  "targetIds": [1001, 1002],
  "referenceId": 55,
  "referenceType": "TUITION",
  "createdBy": "Admin",
  "deadLine": "2026-04-10",
  "isImportant": true
}
**Response thành công (code 0):**
```json
{
  "code": 0,
  "message": "Create notification successfully",
  "data": null
} 
```
---
### 10.7. POST /api/v1/admin/notification/update/`{id}`

Cập nhật notification.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: application/json
**Request body:**
```json
{
  "title": "Thông báo mới",
  "content": "Nội dung cập nhật",
  "createdBy": "Admin",
  "targetType": "STUDENT",
  "targetId": 1001,
  "referenceId": 55,
  "referenceType": "TUITION",
  "isImportant": false,
  "deadLine": "2026-04-15"
}
```
**Response thành công (code 0):**
```json
{
  "code": 0,
  "message": "Update notification successfully",
  "data": null
} 
```
---
### 10.8. POST /api/v1/admin/notification/delete/`{id}`

Xóa notification.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng
**Path param:**
| Field | Type | Required | Description |
|------|-----|-----|-----|
| id | Long | ✅ | ID notification |

**Response thành công (code 0):**
```json
{
  "code": 0,
  "message": "Delete notification successfully",
  "data": null
} 
```
---
## 11. Application - Đơn từ
### 11.1. GET /api/v1/applications/types
Lấy danh sách loại đơn.
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng

**Response thành công (code 0):**:
```json
{
  "code": 0,
  "message": "List of applications",
  "data": [
      {
          "id": 1,
          "code": "HOC_BONG",
          "name": "Don xin hoc bong"
      },
      {
          "id": 2,
          "code": "NGHI_HOC",
          "name": "Don xin nghi hoc"
      },
      {
          "id": 3,
          "code": "CHUYEN_NGANH",
          "name": "Don xin chuyen nganh"
      },
      {
          "id": 4,
          "code": "XAC_NHAN_SV",
          "name": "Xac nhan sinh vien"
      },
      {
          "id": 5,
          "code": "HOAN_THI",
          "name": "Don xin hoan thi"
      },
      {
          "id": 6,
          "code": "PHU_CAP_KTX",
          "name": "Don xin phu cap ky tuc xa"
      }
  ]
}
```  

**Response – User chưa đăng nhập (code -3):**

```json
{
    "code": -3,
    "data": null,
    "message": "Authentication required"
}
```    
**Test cases:**

- ✅ token hợp lệ → code 0 + danh sách loại đơn
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401

### 11.2. POST /api/v1/applications/submit
Nộp đơn.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: multipart/form-data

**Form data fields:**
| Field | Type | Required | Description |
|------|-----|-----|-----|
| file | File[] | ✅ | Danh sách file PDF (có thể upload nhiều file) |
| application-type | number | ✅ | loại đơn |
| content | string | ❌ | nội dung đơn |

**Validation:**

- File không được rỗng → `File is empty`
- Application type không được rỗng → `Application type is empty`
- Mỗi file phải là PDF → `Only valid PDF files are allowed`
- Mỗi file ≤ 5MB → `File size should be less than 5MB`

  
**Response thành công (code 0):**:
```json
### Response thành công (code 0):
```json
{
  "code": 0,
  "message": "Application created successfully",
  "data": [
    "file1.pdf",
    "file2.pdf"
  ]
}
```  
**Response – Thiếu file (code -1):**

```json
{
    "code": -1,
    "data": null,
    "message": "File is empty"
}
``` 

**Response – Thiếu loại đơn (code -1):**

```json
{
    "code": -1,
    "data": null,
    "message": "Application type is empty"
}
``` 
  
**Response – Sai định dạng file (code -1):**

```json
{
    "code": -1,
    "data": null,
    "message": "Only valid PDF files are allowed"
}
``` 

**Response – File quá lớn (code -1):**

```json
{
    "code": -1,
    "data": null,
    "message": "File size should be less than 5MB"
}
```

**Response – User chưa đăng nhập (code -3):**

```json
{
    "code": -3,
    "data": null,
    "message": "Authentication required"
}
```  

**Response – Lỗi server nội bộ (code -10):**

```json
{
    "code": -10,
    "data": null,
    "message": "Application failed to create"
}
```  
**Test cases:**

- ✅ token hợp lệ + file hợp lệ + loại đơn hợp lệ → code 0 + tên file
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ file rỗng / sai định dạng / quá lớn → code -1, HTTP 400
- ❌ loại đơn rỗng → code -1, HTTP 400
---

## 12. News - Tin tức
### 12.1. GET /api/v1/news/top5
Lấy top 5 tin tức.
- **Auth**: Không áp dụng
- **Content-Type**: Không áp dụng 
  
**Response thành công (code 0):**:
```json
{
  "code": 0,
  "message": "Get top 5 news successfully",
  "data": [
    {
        "title": "Su kien cong nghe",
        "excerpt": "Hoi thao AI",
        "imageUrl": "img4.jpg",
        "newsUrl": "link4",
        "source": "Truong",
        "publishDate": "2026-06-01",
        "createdAt": "2026-04-01T10:19:06.213032"
    }
  ]
}
```  

**Test cases:**

- ✅ không áp dụng auth → code 0 + danh sách tin tức
  
### 12.2. GET /api/v1/news
Lấy tất cả tin tức.
- **Auth**: Không áp dụng
- **Content-Type**: Không áp dụng
  
**Response thành công (code 0):**:
```json
{
  "code": 0,
  "message": "Get all news successfully",
  "data": {
      "content": [
          {
              "title": "Su kien cong nghe",
              "excerpt": "Hoi thao AI",
              "imageUrl": "img4.jpg",
              "newsUrl": "link4",
              "source": "Truong",
              "publishDate": "2026-06-01",
              "createdAt": "2026-04-01T10:19:06.213032"
          }
      ],
      "page": 0,
      "size": 10,
      "totalElements": 4,
      "totalPages": 1,
      "first": true,
      "last": true
  }
}
```  

**Test cases:**

- ✅ không áp dụng auth → code 0 + danh sách tin tức
---

## 13. Semester - Kỳ học
### 13.1. GET /api/v1/semester/student
Lấy thông tin kỳ học của sinh viên.
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng

**Response thành công (code 0):**:
```json
{
  "code": 0,
  "message": "Student semesters retrieved successfully",
  "data": [
    {
      "id": 1,
      "semester_name": "HK1 2025-2026",
      "semester_code": "HK1_2025",
      "academic_years": "2025-2026",
      "semester_number": 1,
      "start_date": "2025-09-01",
      "end_date": "2026-01-15",
      "is_active": true
    }
  ]
}
```  

**Response – User chưa đăng nhập (code -3):**

```json
{
  "code": -3,
  "data": null,
  "message": "Authentication required"
}
```    

**Test cases:**

- ✅ token hợp lệ → code 0 + thông tin kỳ học
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ student id không tồn tại trong db → code -2, HTTP 404
---  
### 13.2. GET /api/v1/admin/semesters/all

Lấy danh sách học kỳ (có phân trang).

- **Auth**: Bắt buộc

**Query param (optional):**

| Field | Type | Description |
|------|-----|-----|
| page | int | Trang (default: 0) |
| size | int | Số phần tử (default: 10) |

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Semesters retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "semester_name": "HK1 2025-2026",
        "semester_code": "HK1_2025",
        "academic_years": "2025-2026",
        "semester_number": 1,
        "start_date": "2025-09-01",
        "end_date": "2026-01-15",
        "is_active": true
      }
    ],
    "page": 0,
    "size": 10,
    "total_elements": 1,
    "total_pages": 1,
    "first": true,
    "last": true
  }
}
```

**Test cases:**

- ✅ token hợp lệ → code 0 + thông tin kỳ học
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ student id không tồn tại trong db → code -2, HTTP 404
---
### 13.3. POST /api/v1/admin/semesters/create

Tạo học kỳ mới.

- **Auth**: Bắt buộc
- **Content-Type**: application/json

**Request body:**

```json
{
  "academicYears": "2025-2026",
  "semesterName": "HK1 2025-2026",
  "semesterCode": "HK1_2025",
  "semesterNumber": 1,
  "startDate": "2025-09-01",
  "endDate": "2026-01-15"
}
```

**Validation:**

| Field | Type | Required | Description |
|------|-----|-----|-----|
| academicYears | string | ✅ | Năm học |
| semesterName | string | ✅ | Tên học kỳ |
| semesterCode | string | ✅ | Mã học kỳ |
| semesterNumber | int | ✅ | 1-3 |
| startDate | date | ✅ | Ngày bắt đầu |
| endDate | date | ✅ | Ngày kết thúc |

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Semester created successfully",
  "data": 1
}
```

**Test cases:**

- ✅ token hợp lệ → code 0 + thông tin kỳ học
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ student id không tồn tại trong db → code -2, HTTP 404
---
### 13.4. POST /api/v1/admin/semesters/update/`{id}`

Cập nhật học kỳ.

- **Auth**: Bắt buộc

**Request body:**

```json
{
  "academicYears": "2025-2026",
  "semesterName": "HK1 Updated",
  "semesterCode": "HK1_NEW",
  "semesterNumber": 1,
  "startDate": "2025-09-01",
  "endDate": "2026-01-20"
}
```

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Semester updated successfully",
  "data": null
}
```

**Test cases:**

- ✅ token hợp lệ → code 0 + thông tin kỳ học
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ student id không tồn tại trong db → code -2, HTTP 404
---
### 13.5. POST /api/v1/admin/semesters/delete/`{id}`

Xóa học kỳ.

- **Auth**: Bắt buộc

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Semester deleted successfully",
  "data": null
}
```

**Test cases:**

- ✅ token hợp lệ → code 0 + thông tin kỳ học
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401
- ❌ student id không tồn tại trong db → code -2, HTTP 404
---


## 14. Course Class - Lớp học phần
### 14.1. GET /api/v1/admin/course-classes/all
Lấy thông tin tất cả lớp học phần.
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng

**Query params:**
| Param | Type   | Required | Description                       |
| ----- | ------ | -------- | --------------------------------- |
| page  | number | ❌        | trang (default = 0)               |
| size  | number | ❌        | số lượng mỗi trang (default = 10) |

**Response thành công (code 0):**:
```json
{
    "code": 0,
    "message": "Get all course classes successfully",
    "data": {
        "content": [
            {
                "id": 1,
                "classCode": "INT1001-01",
                "className": "Nhập môn lập trình - Lớp 1",
                "capacity": 50,
                "lecturerCode": "GV001",
                "subjectCode": "INT1001",
                "semesterCode": "HK1-2025",
                "isActive": true
            }
        ],
        "page": 0,
        "size": 10,
        "total_elements": 1,
        "total_pages": 1,
        "first": true,
        "last": true
    }
}
```      
---  
### 14.2. GET /api/v1/admin/course-classes/`{id}`
Lấy chi tiết lớp học phần.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng

**Path params:**
| Field | Type | Required | Description |
| ----- | ---- | -------- | ----------- |
| id | long | ✅ | ID lớp học phần |

**Response thành công (code 0):**
```json
{
    "code": 0,
    "message": "Get detail course class successfully",
    "data": {
        "id": 1,
        "classCode": "INT1001-01",
        "className": "Nhập môn lập trình - Lớp 1",
        "capacity": 50,
        "isActive": true,
        "lecturerCode": "GV001",
        "lecturerName": "Nguyen Van A",
        "subjectCode": "INT1001",
        "subjectName": "Nhập môn lập trình",
        "semester": {
            "semesterCode": "HK1-2025",
            "semesterName": "HK1 2025-2026",
            "academicYears": "2025-2026",
            "semesterNumber": 1,
            "startDate": "2025-09-01",
            "endDate": "2026-01-15"
        }
    }
}
```  

**Response lỗi:**

```json
{
  "code": -2,
  "data": null,
  "message": "Course class not found"
}
```  
---
### 14.3. POST /api/v1/admin/course-classes/create

Tạo lớp học phần mới.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: application/json

**Request body:**
```json
{
  "classCode": "INT1001-01",
  "className": "Nhập môn lập trình - Lớp 1",
  "capacity": 50,
  "subjectId": 1,
  "semesterId": 1,
  "lecturerId": 1
}
```

| Field | Type | Required | Description |
| ----- | ---- | -------- | ----------- |
| classCode | string | ✅ | Mã lớp học phần |
| className | string | ✅ | Tên lớp |
| capacity | int | ✅ | Sĩ số |
| subjectId | long | ✅ | ID môn học |
| semesterId | long | ✅ | ID học kỳ |
| lecturerId | long | ❌ | ID giảng viên |

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Create course class successfully",
  "data": 1
}
```

Response lỗi:

```json
{
  "code": -25,
  "data": null,
  "message": "Class code already exists"
}
```
---  
### 14.4. POST /api/v1/admin/course-classes/update/`{id}`

Cập nhật lớp học phần.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: application/json

**Request body:**

```json
{
  "classCode": "INT1001-02",
  "className": "Nhập môn lập trình - Lớp 2",
  "capacity": 60,
  "subjectId": 2,
  "semesterId": 2,
  "lecturerId": 2
}
```

⚠️ Tất cả field đều optional (chỉ update field được gửi)

| Field | Type | Required | Description |
| ----- | ---- | -------- | ----------- |
| classCode | string | ❌ | Mã lớp |
| className | string | ❌ | Tên lớp |
| capacity | int | ❌ | Sĩ số |
| subjectId | long | ❌ | ID môn học |
| semesterId | long | ❌ | ID học kỳ |
| lecturerId | long | ❌ | ID giảng viên |


**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Update course class successfully",
  "data": null
}
```

**Response lỗi:**

```json
{
  "code": -2,
  "data": null,
  "message": "Course class not found"
}
```  
--- 
### 14.5. POST /api/v1/admin/course-classes/delete/`{id}`

Xoá lớp học phần (soft delete - set isActive = false).

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)

Response thành công (code 0):

```json
{
  "code": 0,
  "message": "Delete course class successfully",
  "data": null
}
```

**Response lỗi:**

```json
{
  "code": -2,
  "data": null,
  "message": "Course class not found"
}
```  
---  
## 15. Department - Bộ môn
### 15.1. GET /api/v1/admin/department/all
Lấy danh sách khoa (phân trang).
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)

**Query param (optional):**

| Field | Type | Required | Description |
| ----- | ---- | -------- | ----------- |
| page | int | ❌ | Trang (default = 0) |
| size | int | ❌ | Số phần tử mỗi trang (default = 10) |

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Get all departments successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "departmentCode": "CNTT",
        "departmentName": "Công nghệ thông tin",
        "facultyCode": "IT",
        "isActive": true
      }
    ],
    "page": 0,
    "size": 10,
    "total_elements": 1,
    "total_pages": 1,
    "first": true,
    "last": true
  }
}
```  
### 15.2. POST /api/v1/admin/department/create

Tạo bộ môn mới.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: application/json

**Request body:**

```json
{
  "departmentCode": "CNTT",
  "departmentName": "Công nghệ thông tin",
  "facultyId": 1
}
```

| Field | Type | Required | Description |
| ----- | ---- | -------- | ----------- |
| departmentCode | string | ✅ | Mã bộ môn |
| departmentName | string | ✅ | Tên bộ môn |
| facultyId | long | ✅ | ID khoa |

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Create department successfully",
  "data": 1
}
```

**Response lỗi:**

```json
{
  "code": -1,
  "data": null,
  "message": "Validation failed"
}
```
---
### 15.3. POST /api/v1/admin/department/update/`{id}`

Cập nhật bộ môn.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: application/json

**Path param:**

| Field | Type | Description |
| ----- | ---- | ----------- |
| id | long | ID khoa |

**Request body (tất cả optional):**

```json
{
  "departmentCode": "CNTT2",
  "departmentName": "Công nghệ phần mềm",
  "facultyId": 2
}
```

| Field | Type | Description |
| ----- | ---- | ----------- |
| departmentCode | string | Mã bộ môn |
| departmentName | string | Tên bộ môn |
| facultyId | long | ID khoa |

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Update department successfully",
  "data": null
}
```

**Response lỗi:**

```json
{
  "code": -2,
  "data": null,
  "message": "Department not found"
}
```
--- 
### 15.4. POST /api/v1/admin/department/delete/`{id}`

Xóa bộ môn (soft delete).

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)

**Path param:**

| Field | Type | Description |
| ----- | ---- | ----------- |
| id | long | ID bộ môn |

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Delete department successfully",
  "data": null
}
```

**Response lỗi:**

```json
{
  "code": -2,
  "data": null,
  "message": "Department not found"
}
```
---
## 16. Faculty - Khoa
### 16.1. GET /api/v1/admin/faculty/all

Lấy danh sách khoa (phân trang).

- **Auth**: Bắt buộc (Admin)

**Query param (optional):**

| Field | Type | Required | Description |
|------|-----|-----|-----|
| page | int | ❌ | Trang (default = 0) |
| size | int | ❌ | Số phần tử mỗi trang (default = 10) |

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Get all faculties successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "facultyCode": "IT",
        "facultyName": "Công nghệ thông tin",
        "isActive": true
      }
    ],
    "page": 0,
    "size": 10,
    "total_elements": 1,
    "total_pages": 1,
    "first": true,
    "last": true
  }
}
```
---
### 16.2. POST /api/v1/admin/faculty/create

Tạo khoa.

- **Auth**: Bắt buộc
- **Content-Type**: application/json

**Request body:**

```json
{
  "facultyCode": "IT",
  "facultyName": "Công nghệ thông tin"
}
| Field | Type | Required | Description |
|------|-----|-----|-----|
| facultyCode | string | ✅ | Mã khoa |
| facultyName | string | ✅ | Tên khoa |

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Create faculty successfully",
  "data": 1
}
```

**Response lỗi:**

```json
{
  "code": -1,
  "data": null,
  "message": "Validation failed"
}
```
--- 
### 16.3. POST /api/v1/admin/faculty/update/`{id}`

Cập nhật khoa.

- **Auth**: Bắt buộc
- **Content-Type**: application/json

**Path param:**

| Field | Type | Description |
|------|-----|-----|
| id | long | ID khoa |

**Request body (optional):**

```json
{
  "facultyCode": "IT2",
  "facultyName": "Khoa CNTT nâng cao"
}
```

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Update faculty successfully",
  "data": null
}
```
---
### 16.4. POST /api/v1/admin/faculty/delete/`{id}`

Xóa khoa (soft delete).

- **Auth**: Bắt buộc

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Delete faculty successfully",
  "data": null
}
```
---  
## 17. Lecturer - Quản lý giảng viên
### 17.1. GET /api/v1/admin/lecturers/all
Lấy danh sách giảng viên (phân trang).

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng
- **Query param (optional)**:

| Field | Type | Required | Description |
|------|-----|-----|-----|
| page | int | ❌ | Số trang (default: 0) |
| size | int | ❌ | Số lượng mỗi trang (default: 10) |

**Response thành công (code 0):**
```json
{
  "code": 0,
  "message": "Get all lecturers successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "lecturerCode": "GV001",
        "fullName": "Nguyen Van A",
        "email": "a@university.edu.vn",
        "phoneNumber": "0901234567",
        "departmentCode": "CNTT",
        "status": "ACTIVE"
      }
    ],
    "page": 0,
    "size": 10,
    "total_elements": 1,
    "total_pages": 1,
    "first": true,
    "last": true
  }
}
```
---
### 17.2. GET /api/v1/admin/lecturers/`{id}`

Lấy chi tiết giảng viên.

- **Auth**: Bắt buộc

**Path param:**

| Field | Type | Required | Description |
|------|-----|-----|-----|
| id | long | ✅ | ID giảng viên |

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Get detail lecturer successfully",
  "data": {
    "id": 1,
    "lecturerCode": "GV001",
    "fullName": "Nguyen Van A",
    "email": "a@university.edu.vn",
    "phoneNumber": "0901234567",
    "departmentCode": "CNTT",
    "status": "ACTIVE"
  }
}
```
---
### 17.3. POST /api/v1/admin/lecturers/create

Tạo mới giảng viên.

- **Auth**: Bắt buộc
- **Content-Type**: application/json

**Request body:**

```json
{
  "lecturerCode": "GV001",
  "fullName": "Nguyen Van A",
  "email": "a@university.edu.vn",
  "phoneNumber": "0901234567",
  "departmentId": 1
}
```

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Create lecturer successfully",
  "data": 1
}
```
---
### 17.4. POST /api/v1/admin/lecturers/update/`{id}`

Cập nhật giảng viên.

- **Auth**: Bắt buộc

**Request body:**
```json
{
  "lecturerCode": "GV002",
  "fullName": "Nguyen Van B",
  "email": "b@university.edu.vn",
  "phoneNumber": "0912345678",
  "departmentId": 2
}
```

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Update lecturer successfully",
  "data": null
}
```
---
### 17.5. POST /api/v1/admin/lecturers/delete/`{id}`

Xóa giảng viên.

- **Auth**: Bắt buộc

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Delete lecturer successfully",
  "data": null
}
```

**Test cases:**

- ✅ Token hợp lệ → hoạt động bình thường
- ❌ Token thiếu / invalid → code -3 (401)
- ❌ ID không tồn tại → code -2 (404)
- ❌ Validate fail → code -1 hoặc -5 (400)
- ❌ Trùng lecturerCode/email → code -25 (409)
---  
## 18. Academic Advisor - Cố vấn học tập
### 18.1. GET /api/v1/admin/academic-advisors/all
Lấy danh sách cố vấn học tập (phân trang).

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng

**Query param (optional):**

| Field | Type | Required | Description |
|------|-----|-----|-----|
| page | int | ❌ | Số trang (default: 0) |
| size | int | ❌ | Số lượng mỗi trang (default: 10) |

**Response thành công (code 0):**
```json
{
  "code": 0,
  "message": "Get all academic advisors successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "lecturerCode": "GV001",
        "lecturerName": "Nguyen Van A",
        "lecturerEmail": "a@university.edu.vn",
        "lecturerPhoneNumber": "0901234567",
        "studentClassCode": "KHMT2021"
      }
    ],
    "page": 0,
    "size": 10,
    "total_elements": 1,
    "total_pages": 1,
    "first": true,
    "last": true
  }
}
```
---
### 18.2. GET /api/v1/admin/academic-advisors/`{id}`

Lấy chi tiết cố vấn học tập.

- **Auth**: Bắt buộc

**Path param:**

| Field | Type | Required | Description |
|------|-----|-----|-----|
| id | long | ✅ | ID |

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Get detail academic advisor successfully",
  "data": {
    "id": 1,
    "lecturerCode": "GV001",
    "lecturerName": "Nguyen Van A",
    "lecturerEmail": "a@university.edu.vn",
    "lecturerPhoneNumber": "0901234567",
    "studentClassCode": "KHMT2021"
  }
}
```
---
### 18.3. POST /api/v1/admin/academic-advisors/create

Tạo cố vấn học tập.

- **Auth**: Bắt buộc
- **Content-Type**: application/json

**Request body:**

```json
{
  "lecturerId": 1,
  "studentClassId": 2
}

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Create academic advisor successfully",
  "data": 1
}
```
---  
### 18.4. POST /api/v1/admin/academic-advisors/delete/`{id}`

Xóa cố vấn học tập.

- **Auth**: Bắt buộc

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Delete academic advisor successfully",
  "data": null
}
```
**Test cases:**
- ✅ Token hợp lệ → hoạt động bình thường
- ❌ Token thiếu / invalid → code -3 (401)
- ❌ ID không tồn tại → code -2 (404)
- ❌ lecturerId / studentClassId không tồn tại → code -2 (404)
- ❌ Trùng cố vấn (1 lớp đã có advisor) → code -25 (409)
---
## 19. Major - Quản lý ngành học
### 19.1. GET /api/v1/admin/majors/all
Lấy danh sách ngành học (phân trang).

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng

**Query param (optional):**

| Field | Type | Required | Description |
|------|-----|-----|-----|
| page | int | ❌ | Số trang (default: 0) |
| size | int | ❌ | Số lượng mỗi trang (default: 10) |

**Response thành công (code 0):**
```json
{
  "code": 0,
  "message": "Get all majors successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "majorName": "Khoa học máy tính",
        "majorCode": "KHMT",
        "facultyCode": "CNTT",
        "isActive": true,
        "createdAt": "2026-03-01T10:00:00",
        "updatedAt": "2026-03-01T10:00:00"
      }
    ],
    "page": 0,
    "size": 10,
    "total_elements": 1,
    "total_pages": 1,
    "first": true,
    "last": true
  }
}
```
---
### 19.2. POST /api/v1/admin/majors/create

Tạo ngành học.

- **Auth**: Bắt buộc
- **Content-Type**: application/json

**Request body:**

```json
{
  "majorCode": "KHMT",
  "majorName": "Khoa học máy tính",
  "facultyId": 1
}
```

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Create major successfully",
  "data": 1
}
```
---
### 19.3. POST /api/v1/admin/majors/update/`{id}`

Cập nhật ngành học.

- **Auth**: Bắt buộc
- **Content-Type**: application/json

**Request body:**

```json
{
  "majorCode": "KTPM",
  "majorName": "Kỹ thuật phần mềm",
  "facultyId": 2
}
```

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Update major successfully",
  "data": null
}
```
---
### 19.4. POST /api/v1/admin/majors/delete/`{id}`

Xóa ngành học.

- **Auth**: Bắt buộc

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Delete major successfully",
  "data": null
}
```

**Test cases:**

- ✅ Token hợp lệ → hoạt động bình thường
- ❌ Token thiếu / invalid → code -3 (401)
- ❌ ID không tồn tại → code -2 (404)
- ❌ facultyId không tồn tại → code -2 (404)
- ❌ Trùng majorCode → code -25 (409)
- ❌ Validate fail → code -1 hoặc -5 (400)
---
## 20. Student Class - Quản lý lớp sinh viên
### 20.1. GET /api/student-class/all

Lấy danh sách lớp sinh viên (có phân trang)

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng

**Query params:**

| Field | Type | Required | Description |
|------|-----|-----|-----|
| page | int | ❌ | Số trang (default: 0) |
| size | int | ❌ | Số lượng mỗi trang (default: 10) |

**Response:**
```json
{
  "code": 0,
  "message": "Get all student classes successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "class_code": "KHMT2021",
        "major_name": "Khoa học máy tính",
        "start_year": 2021,
        "student_count": 45
      }
    ],
    "page": 0,
    "size": 10,
    "total_elements": 100,
    "total_pages": 10
  }
}
```
---
### 20.2. POST /api/student-class/create

Tạo lớp sinh viên

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: application/json

**Request body:**

```json
{
  "class_code": "KHMT2025",
  "major_id": 1,
  "start_year": 2025
}
```

**Response:**

```json
{
  "code": 0,
  "message": "Create student class successfully",
  "data": 10
}
```
---
### 20.3. POST /api/student-class/update/`{id}`

Cập nhật lớp sinh viên

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: application/json

**Request body (partial):**

```json
{
  "class_code": "KHMT2025",
  "major_id": 2,
  "start_year": 2026
}
```

**Response:**

```json
{
  "code": 0,
  "message": "Update student class successfully",
  "data": null
}
```
---
### 20.4. POST /api/student-class/delete/`{id}`

Xóa lớp sinh viên

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: application/json

**Response:**

```json
{
  "code": 0,
  "message": "Delete student class successfully",
  "data": null
}
```
---
## 21. Subject - Quản lý môn học
### 21.1. GET /api/v1/admin/subjects/all
Lấy danh sách tất cả môn học (có phân trang).

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Query param (optional)**:

| Field | Type | Required | Description |
|------|-----|-----|-----|
| page | int | ❌ | Số trang (default: 0) |
| size | int | ❌ | Số phần tử mỗi trang (default: 10) |

**Response thành công (code 0):**
```json
{
  "code": 0,
  "message": "Get all subjects successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "subjectCode": "INT1001",
        "subjectName": "Nhập môn lập trình",
        "credits": 3,
        "coefficient": 1,
        "lectureHours": 30,
        "practiceHours": 15,
        "facultyId": 1,
        "departmentId": 2
      }
    ],
    "page": 0,
    "size": 10,
    "total_elements": 100,
    "total_pages": 10
  }
}
```
---
### 21.2. GET /api/v1/admin/subjects/`{id}`

Lấy chi tiết môn học theo ID.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Get subject by id successfully",
  "data": {
    "id": 1,
    "subjectCode": "INT1001",
    "subjectName": "Nhập môn lập trình",
    "credits": 3,
    "coefficient": 1,
    "lectureHours": 30,
    "practiceHours": 15,
    "facultyId": 1,
    "departmentId": 2,
    "prerequisiteGroups": [],
    "enrollmentConditions": []
  }
}
```
---
### 21.3. POST /api/v1/admin/subjects/create

Tạo môn học mới.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: application/json

**Request body:**

```json
{
  "facultyId": 1,
  "departmentId": 2,
  "subjectCode": "INT1001",
  "subjectName": "Nhập môn lập trình",
  "credits": 3,
  "coefficient": 1,
  "lectureHours": 30,
  "practiceHours": 15
}
```

**Response thành công:**

```json
{
  "code": 0,
  "message": "Create subject successfully",
  "data": 1
}
```
---
### 21.4. POST /api/v1/admin/subjects/update/`{id}`

Cập nhật môn học.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: application/json

**Request body (các field optional):**

```json
{
  "subjectName": "Lập trình cơ bản",
  "credits": 4,
  "prerequisiteGroups": [
    {
      "id": 1,
      "minSubjectsRequired": 1,
      "description": "Nhóm tiên quyết",
      "prerequisiteSubjectIds": [2,3]
    }
  ],
  "enrollmentConditions": [
    {
      "id": 1,
      "conditionType": "GPA",
      "conditionValue": 2.5,
      "conditionOperator": ">=",
      "description": "Yêu cầu GPA"
    }
  ]
}
```

**Response thành công:**

```json
{
  "code": 0,
  "message": "Update subject successfully",
  "data": null
}
```
---
### 21.5. POST /api/v1/admin/subjects/delete/`{id}`

Xóa môn học.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)

**Response thành công:**

```json
{
  "code": 0,
  "message": "Delete subject successfully",
  "data": null
}
```

**Test cases:**

- ✅ Token hợp lệ → success
- ❌ Không có token → code -3 (401)
- ❌ Subject không tồn tại → code -2 (404)
- ❌ Validation lỗi → code -5 (400)
- ❌ Subject code trùng → code -25 (409)
---
## 22. Tuition - Học phí
### 22.1. GET /api/v1/tuition
Lấy danh sách hóa đơn học phí của sinh viên.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng

**Response thành công (code 0):**
```json
{
  "code": 0,
  "message": "Get tuition invoices successfully",
  "data": [
      {
          "invoiceId": 1,
          "semesterName": "Hoc ky 1 2025",
          "totalAmount": 4500000.00,
          "finalAmount": 4500000.00,
          "status": "PAID",
          "dueDate": "2026-01-10"
      }
  ]
}
```
---
### 22.2. GET /api/v1/tuition/`{invoiceId}`

Lấy chi tiết hóa đơn học phí.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Path param:**

| Field | Type | Required | Description |
|------|-----|-----|-----|
| invoiceId | long | ✅ | ID hóa đơn |
  
**Response thành công (code 0):**
```json
{
  "code": 0,
  "message": "Get tuition invoice detail successfully",
  "data": {
    "invoice_id": 1,
    "semesterName": "Hoc ky 1 2025",
    "student_name": "Pham Minh Duc",
    "student_code": "SV2021001",
    "items": [
      {
          "id": 1,
          "subjectName": "Nhap mon lap trinh",
          "subjectCode": "INT1001",
          "credits": 3,
          "pricePerCredit": 500000.00,
          "coefficient": 1.00,
          "amount": 1500000.00,
          "retake": false
      },
    ],
    "total_amount": 5000000,
    "final_amount": 4500000,
    "status": "UNPAID",
    "due_date": "2026-01-15"
  }
}
```
---
### 22.3. GET /api/v1/admin/tuition/invoices
Lấy danh sách hóa đơn học phí theo kỳ.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Query param:**

| Field | Type | Required | Description |
|------|-----|-----|-----|
| semesterId | long | ✅ | ID kỳ học |
| page | int | ❌ | Trang (default: 0) |
| size | int | ❌ | Số phần tử mỗi trang |

**Response thành công (code 0):**
```json
{
  "code": 0,
  "message": "Get tuition invoices successfully",
  "data": {
    "content": [
        {
            "invoiceId": 1,
            "studentName": "Nguyen Van A",
            "studentCode": "SV001",
            "semesterCode": "HK1-2025",
            "totalAmount": 4500000.00,
            "finalAmount": 4500000.00,
            "status": "CANCELLED",
            "dueDate": "2026-01-10"
        }
    ],
    "page": 0,
    "size": 10,
    "total_elements": 1,
    "total_pages": 1
  }
}
```
---
### 22.4. GET /api/v1/admin/tuition/invoices/`{invoiceId}`

Lấy chi tiết hóa đơn học phí.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Path param:**

| Field | Type | Required | Description |
|------|-----|-----|-----|
| invoiceId | long | ✅ | ID hóa đơn |

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Get tuition invoice detail successfully",
  "data": {
    "invoiceId": 1,
    "studentName": "Nguyen Van A",
    "studentCode": "SV001",
    "semesterCode": "HK1-2025",
    "totalAmount": 4500000.00,
    "finalAmount": 4500000.00,
    "status": "CANCELLED",
    "dueDate": "2026-01-10",
    "items": [
        {
            "id": 1,
            "subjectName": "Nhap mon lap trinh",
            "credits": 3,
            "pricePerCredit": 500000.00,
            "coefficient": 1.00,
            "amount": 1500000.00,
            "retake": false
        }
    ]
  }
}
```
---
### 22.5. POST /api/v1/admin/tuition/generate

Tạo hóa đơn học phí theo kỳ.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: application/json

**Request body:**

```json
{
  "semesterId": 1
}

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Generate invoices successfully",
  "data": 100
}
```
---
### 22.6. POST /api/v1/admin/tuition/regenerate/`{invoiceId}`

Tạo lại hóa đơn học phí.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Path param:**

| Field | Type | Required | Description |
|------|-----|-----|-----|
| invoiceId | long | ✅ | ID hóa đơn |

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Regenerate invoice successfully",
  "data": 999
}
```
---
### 22.7. POST /api/v1/admin/tuition/delete/`{invoiceId}`

Xóa hóa đơn học phí.

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Path param:**

| Field | Type | Required | Description |
|------|-----|-----|-----|
| invoiceId | long | ✅ | ID hóa đơn |

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Delete invoice successfully",
  "data": null
}
```
---
## 23. Notification Template – Quản lý template thông báo
### 23.1. POST /api/v1/admin/notification-templates/create
Tạo template thông báo mới

**Request body:**
```json
{
  "code": "WELCOME",
  "name": "Thông báo chào mừng",
  "content": "Chào mừng bạn đến với hệ thống"
}
```
|Field | Type | Required | Description
|---|---|---|---
|code | string | ✅ | Mã template
|name | string | ✅ | Tên template
|content | string | ✅ | Nội dung template

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Create notification template successfully",
  "data": 1
}
```
---
### 23.2. POST /api/v1/admin/notification-templates/update/`{id}`

Cập nhật template thông báo

**Path param:**

| Field | Type | Required | Description |
|------|-----|-----|-----|
| id | long | ✅ | ID template |

**Request body:**

```json
{
  "code": "WELCOME",
  "name": "Thông báo chào mừng mới",
  "content": "Nội dung mới"
}
```

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Update notification template successfully",
  "data": null
}
```
---
### 23.3. POST /api/v1/admin/notification-templates/delete/`{id}`

Xóa template thông báo

**Path param:**

| Field | Type | Required | Description |
|------|-----|-----|-----|
| id | long | ✅ | ID template |

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Delete notification template successfully",
  "data": null
}
```
---
### 23.4. GET /api/v1/admin/notification-templates/all

Lấy danh sách template (có phân trang)

**Query param:**

| Field | Type | Required | Description |
|------|-----|-----|-----|
| page | int | ❌ | Trang (default = 0) |
| size | int | ❌ | Số phần tử mỗi trang (default = 10) |

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Get all notification templates successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "code": "WELCOME",
        "name": "Thông báo chào mừng"
      }
    ],
    "page": 0,
    "size": 10,
    "total_elements": 1,
    "total_pages": 1,
    "first": true,
    "last": true
  }
}
```
**Test cases**
- ✅ Tạo template hợp lệ → code 0 + id
- ❌ Thiếu field (code/name/content) → code -5
- ❌ ID không tồn tại (update/delete) → code -2
- ❌ Không có token → code -3
---
## 24. Payment - Thanh toán

Base path: `/api/v1/payments`  
- **Auth**: Bắt buộc (trừ callback)  
- **Content-Type**: application/json  

---

### 24.1. POST /api/v1/payments/create-order
Tạo đơn thanh toán học phí

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)

**Request body:**
```json
{
  "invoiceId": 1
}
```
| Field | Type | Required | Description
|---|---|---|---|
| invoiceId | long | ✅ | ID hóa đơn học phí

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Tạo đơn thanh toán thành công",
  "data": {
    "orderUrl": "https://sandbox.zalopay.vn/...",
    "appTransId": "240402_123456",
    "amount": 500000
  }
} 
```
**Response – User chưa đăng nhập (code -3):**

```json
{
  "code": -3,
  "data": null,
  "message": "Authentication required"
}
```
---
### 24.2. POST /api/v1/payments/refund
Hoàn tiền giao dịch

- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: application/json

**Query param:**

| Field | Type | Required | Description
|---|---|---|---|
| transCode | string | ✅ | Mã giao dịch cần hoàn tiền

Ví dụ request:

POST /api/v1/payments/refund?transCode=240402_123456

**Response thành công (code 0):**

```json
{
  "code": 0,
  "message": "Hoàn tiền thành công",
  "data": {
    "returnCode": 1,
    "returnMessage": "Refund success"
  }
}
```
**Response – Lỗi (code -10):**

```json
{
  "code": -10,
  "message": "Refund failed",
  "data": null
}
```
**Test cases**
- ✅ Tạo payment hợp lệ → trả về URL ZaloPay
- ❌ invoiceId null → code -5
- ❌ chưa login → code -3
- ✅ callback hợp lệ → update trạng thái thanh toán
- ❌ callback sai MAC → reject
- ✅ refund thành công → code 0
- ❌ transCode không tồn tại → code -2
---