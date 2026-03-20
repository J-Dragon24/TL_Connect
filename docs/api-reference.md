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
| idToken | string | ✅ | Microsoft OAuth2 ID từ Microsoft Azure AD |

**Response – Đăng nhập thành công (code 0):**:

```json
{
  "code": 0,
  "data": {
    "microsoftId": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "abc@gmail.com",
    "name": "John Doe",
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
### 5.1. POST /api/v1/student/me
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
### 5.2. POST /api/v1/student/class
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
            },
            {
                "student_code": "SV2021002",
                "full_name": "Hoang Thi Em",
                "gender": "NU"
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
### 5.3. POST /api/v1/admin/student/create
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
    "issued_place": "Cục Cảnh sát QLHC về TTXH"
  },
  "contact": {
    "phone_number": "0912345678",
    "address": "123 Nguyễn Trãi, Hà Nội",
    "email": "an.nguyen@example.com"
  },
  "academic_info": {
    "cohort": "2022-2026",
    "position": "Lớp trưởng"
  },
  "emergency_contact": {
    "name": "Nguyễn Văn Bình",
    "phone_number": "0987654321",
    "address": "123 Nguyễn Trãi, Hà Nội",
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

### 5.4. POST /api/v1/admin/student/import
Import danh sách sinh viên từ file xlsx/csv.
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: multipart/form-data

**Form data field:**

| Field | Type | Required | Description |
|------|-----|-----|-----|
| file | File | ✅ | File chứa data sinh viên |

**Response thành công (code 0):**

```json
{
    "code": 0,
    "message": "File imported successfully",
    "data": {
        "total": 2,
        "success": 2,
        "failed": 0,
        "errors": []
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
### 5.5. GET /api/v1/admin/student/all
Lấy danh sách tất cả sinh viên.
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng

**Response thành công (code 0):**

```json
{
    "code": 0,
    "message": "Get all students successfully",
    "data": {
        "content": [
            {
                "student_code": "SV2022002",
                "full_name": "Dang Van Minh",
                "date_of_birth": "2004-03-07",
                "gender": "NAM",
                "class_code": "KTPM2022",
                "academic_advisor": null,
                "major": {
                    "major_code": "KTPM",
                    "major_name": "Kỹ thuật phần mềm",
                    "faculty": "Công nghệ thông tin"
                },
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
### 6.2. GET /api/v1/study-programs/{studyProgramCode}
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
                        "subject_code": "INT1001",
                        "subject_name": "Nhập môn lập trình",
                        "credits": 3,
                        "is_required": true,
                        "elective_group": null,
                        "lecture_hours": 30,
                        "practice_hours": 15,
                        "subject_prerequisite": null,
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
| HocKy | string | ✅ | Tên học kỳ |  

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
## 8. Exam - Lịch thi
### 8.1. GET /api/v1/student/exams
Lấy thông tin lịch thi của sinh viên.  
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng
- **Query param**: 
| Field | Type | Required | Description |
|------|-----|-----|-----|
| HocKy | string | ✅ | Tên học kỳ |  
  
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
  
## 10. Notification - Thông báo
### 10.1. GET /api/v1/student/notification
Lấy tất cả thông tin thông báo của sinh viên.  
- **Auth**: Bắt buộc (Authorization: Bearer &lt;JWT&gt;)
- **Content-Type**: Không áp dụng

**Response thành công (code 0):**:
```json
{
    "code": 0,
    "message": "Get all notification successfully",
    "data": [
        {
            "id": 1,
            "title": "Thong bao he thong",
            "content": "He thong se bao tri vao 23:00 toi nay",
            "created_by": "Admin",
            "target_type": "ALL",
            "dead_line": null,
            "created_at": "2026-03-18T08:48:44.464959",
            "is_read": true
        },
        {
            "id": 2,
            "title": "Cap nhat cong thong tin",
            "content": "Da cap nhat giao dien moi",
            "created_by": "Admin",
            "target_type": "ALL",
            "dead_line": null,
            "created_at": "2026-03-18T08:48:44.464959",
            "is_read": true
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

- ✅ token hợp lệ → code 0 + thông tin thông báo
- ❌ token rỗng / thiếu / invalid / hết hạn → code -3, HTTP 401

### 10.2. GET /api/v1/student/notification/id
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
        "created_by": "Admin",
        "target_type": "ALL",
        "dead_line": null,
        "created_at": "2026-03-18T08:48:44.464959"
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
| file | File | ✅ | file đơn (pdf) |  
| application-type | number | ✅ | loại đơn |  
| content | string | ❌ | nội dung đơn |  
  
**Response thành công (code 0):**:
```json
{
  "code": 0,
  "message": "Application created successfully",
  "data": [
      "abc.pdf"
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
      "abc.pdf"
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
  "data": [
      {
          "id": 1,
          "title": "Tin tức 1",
          "content": "Nội dung tin tức 1",
          "createdAt": "2026-02-26T11:28:45.567903"
      },
      {
          "id": 2,
          "title": "Tin tức 2",
          "content": "Nội dung tin tức 2",
          "createdAt": "2026-02-26T11:28:45.567903"
      }
  ]
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
            "academic_years": "2025-2026",
            "semester_number": 1,
            "start_date": "2025-09-01",
            "end_date": "2026-01-15",
            "created_at": "2026-03-18T08:48:44.464959",
            "updated_at": "2026-03-18T08:48:44.464959"
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


