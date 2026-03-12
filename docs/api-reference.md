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
| -10 | 500 | Lỗi server nội bộ |
| -13 | 502 | Lỗi external API |
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
    "studentCode": "SV2021001",
    "fullName": "Pham Minh Duc",
    "dateOfBirth": "2003-05-10",
    "gender": "NAM",
    "classCode": "KHMT2021",
    "academicAdvisor": "Nguyen Van An",
    "startYear": "2022-09-01",
    "endYear": "2028-06-30",
    "major": {
        "majorCode": "KHMT",
        "majorName": "Khoa học máy tính",
        "faculty": "Công nghệ thông tin"
    },
    "identityCard": {
        "cardNumber": "079203001111",
        "cardType": "CCCD",
        "issuedDate": "2021-01-10",
        "issuedPlace": "Cục CS QLHC về TTXH - HCM"
    },
    "contact": {
        "phoneNumber": "0911111111",
        "address": "12 Nguyen Trai, HCM",
        "email": "duc.personal@gmail.com"
    },
    "academicInfo": {
        "cohort": "K2021",
        "position": "Lớp trưởng",
        "educationMode": "CHINH_QUY"
    },
    "emergencyContact": {
        "name": "Pham Van Bo",
        "phoneNumber": "0981111111",
        "address": "12 Nguyen Trai, HCM"
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
      "classCode": "KHMT2021",
      "academicAdvisor": {
          "lecturerCode": "GV001",
          "fullName": "Nguyen Van An",
          "phoneNumber": "0901234567",
          "email": "an.nguyen@university.edu.vn"
      },
      "students": [
          {
              "studentCode": "SV2021001",
              "fullName": "Pham Minh Duc",
              "gender": "NAM"
          },
          {
              "studentCode": "SV2021002",
              "fullName": "Hoang Thi Em",
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
            "studentCode": "SV2021001",
            "studyProgramCode": "CTDT-KHMT-2021",
            "studyProgramName": "Chương trình đào tạo KHMT 2021",
            "isPrimary": true,
            "startYear": 2021
        },
        {
            "studentCode": "SV2021001",
            "studyProgramCode": "CTDT-HTTT-2021",
            "studyProgramName": "Chương trình đào tạo HTTT 2021",
            "isPrimary": false,
            "startYear": 2021
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
        "studyProgramName": "Chương trình đào tạo KHMT 2021",
        "yearStart": 2021,
        "totalCredits": 130,
        "major": {
            "majorName": "Khoa học máy tính",
            "majorCode": "KHMT",
            "faculty": "Công nghệ thông tin"
        },
        "semesters": [
            {
                "semesterName": "HK1 2026-2027",
                "semesterStartDate": "2021-01-01",
                "semesterEndDate": "2022-04-15",
                "subjects": [
                    {
                        "subjectCode": "INT1001",
                        "subjectName": "Nhập môn lập trình",
                        "credits": 3,
                        "isRequired": true,
                        "electiveGroup": null,
                        "lectureHours": 30,
                        "practiceHours": 15,
                        "subjectPrerequisite": null,
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
        "courseClasses": [
            {
                "classCode": "INT1002-01",
                "dayOfWeek": 2,
                "subjectName": "Cấu trúc dữ liệu & giải thuật",
                "subjectCode": "INT1002",
                "startPeriod": 6,
                "endPeriod": 8,
                "startTime": "11:30:00",
                "endTime": "14:00:00",
                "room": "B201",
                "lecturer": {
                    "lecturerCode": "GV001",
                    "fullName": "Nguyen Van An",
                    "email": "an.nguyen@university.edu.vn",
                    "phoneNumber": "0123456789"
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
      "semester": "HK2 2021-2022",
      "week": 1,
      "startDate": "2022-02-01",
      "endDate": "2022-09-01",
      "dailySchedules": [
          {
              "courseClasses": [
                  {
                      "classCode": "INT1002-01",
                      "dayOfWeek": 2,
                      "subjectName": "Cấu trúc dữ liệu & giải thuật",
                      "subjectCode": "INT1002",
                      "startPeriod": 6,
                      "endPeriod": 8,
                      "startTime": "11:30:00",
                      "endTime": "14:00:00",
                      "room": "B201",
                      "lecturer": {
                            "lecturerCode": "GV001",
                            "fullName": "Nguyen Van An",
                            "email": "an.nguyen@university.edu.vn",
                            "phoneNumber": "0123456789"
                        }
                  }
              ]
          },
          {
              "courseClasses": [
                  {
                      "classCode": "INT1003-01",
                      "dayOfWeek": 5,
                      "subjectName": "Lập trình hướng đối tượng",
                      "subjectCode": "INT1003",
                      "startPeriod": 1,
                      "endPeriod": 3,
                      "startTime": "07:00:00",
                      "endTime": "09:30:00",
                      "room": "B202",
                      "lecturer": {
                        "lecturerCode": "GV001",
                        "fullName": "Nguyen Van An",
                        "email": "an.nguyen@university.edu.vn",
                        "phoneNumber": "0123456789"
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
      "semester": "HK1 2022-2023",
      "courseClasses": [
          {
              "classCode": "INT2001-01",
              "dayOfWeek": 3,
              "subjectName": "Cơ sở dữ liệu",
              "subjectCode": "INT2001",
              "startPeriod": 4,
              "endPeriod": 6,
              "startTime": "09:45:00",
              "endTime": "12:15:00",
              "room": "C301",
              "lecturer": {
                "lecturerCode": "GV001",
                "fullName": "Nguyen Van An",
                "email": "an.nguyen@university.edu.vn",
                "phoneNumber": "0123456789"
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
  "message": "HK1 2021-2022",
  "data": {
      "semesterName": "HK1 2021-2022",
      "examSchedules": [
          {
              "subjectCode": "INT1001",
              "subjectName": "Nhập môn lập trình",
              "classCode": "INT1001-01",
              "examDate": "2022-01-10",
              "startTime": "07:30:00",
              "endTime": "09:30:00",
              "examRoom": "P101",
              "examLocation": "Co so 1",
              "examFormat": "TRAC_NGHIEM",
              "examType": "GIUA_KY",
              "examAttempt": 1,
              "attendanceStatus": "ATTENDED",
              "examStatus": "DONE"
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
    "studyProgram": "CTDT-KHMT-2021",
    "semesterResults": [
        {
            "semester": "HK1 2026-2027",
            "subjectResults": [
                {
                    "subjectCode": "INT1001",
                    "subjectName": "Nhập môn lập trình",
                    "credits": 3,
                    "attendanceScore": 10.00,
                    "midtermScore": 6.00,
                    "finalScore": 8.00,
                    "score10": 8.50,
                    "score4": 3.50,
                    "letterGrade": "A",
                    "isPass": true
                }
            ],
            "semesterSummary": {
                "creditsRegistered": null,
                "creditsPassed": 3,
                "semesterGpa": 3.50,
                "conductScore": 85,
                "cumulativeGpa": 3.50
            }
        },
        {
            "semester": "HK2 2021-2022",
            "subjectResults": [
                {
                    "subjectCode": "INT1002",
                    "subjectName": "Cấu trúc dữ liệu & giải thuật",
                    "credits": 3,
                    "attendanceScore": 10.00,
                    "midtermScore": 9.00,
                    "finalScore": 5.00,
                    "score10": 7.00,
                    "score4": 3.00,
                    "letterGrade": "B",
                    "isPass": true
                },
                {
                    "subjectCode": "INT1003",
                    "subjectName": "Lập trình hướng đối tượng",
                    "credits": 3,
                    "attendanceScore": 10.00,
                    "midtermScore": 9.00,
                    "finalScore": 8.60,
                    "score10": 9.00,
                    "score4": 4.00,
                    "letterGrade": "A+",
                    "isPass": true
                }
            ],
            "semesterSummary": {
                "creditsRegistered": null,
                "creditsPassed": 6,
                "semesterGpa": 3.20,
                "conductScore": 88,
                "cumulativeGpa": 3.30
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
          "id": 4,
          "title": "Ket qua xet hoc bong HK1",
          "sender": "Phong Cong Tac Sinh Vien",
          "targetType": "ALL",
          "deadLine": null,
          "createdAt": "2026-02-26T11:28:45.567903"
      },
      {
          "id": 2,
          "title": "Thong bao nghi le 30/4",
          "sender": "Ban Giam Hieu",
          "targetType": "ALL",
          "deadLine": null,
          "createdAt": "2026-02-26T11:28:45.567903"
      },
      {
          "id": 1,
          "title": "Thong bao lich thi HK1 2021-2022",
          "sender": "Phong Dao Tao",
          "targetType": "ALL",
          "deadLine": "2022-01-05T17:00:00",
          "createdAt": "2026-02-26T11:28:45.567903"
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
      "title": "Thong bao nghi le 30/4",
      "content": "Truong thong bao nghi le 30/4 - 1/5. Sinh vien nghi hoc tu ngay 29/4 den 2/5.",
      "sender": "Ban Giam Hieu",
      "targetType": "ALL",
      "deadLine": null,
      "createdAt": "2026-02-26T11:28:45.567903"
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
            "semesterName": "HK1 2025-2026",
            "startDate": "2025-09-01",
            "endDate": "2026-01-15"
        },
        {
            "semesterName": "HK2 2025-2026",
            "startDate": "2026-02-01",
            "endDate": "2026-06-30"
        },
        {
            "semesterName": "HK1 2026-2027",
            "startDate": "2026-09-01",
            "endDate": "2027-01-15"
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


