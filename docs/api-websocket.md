# WebSocket Realtime
## Tổng quan

Hệ thống sử dụng Spring WebSocket + STOMP để cung cấp realtime cho 2 nhóm nghiệp vụ chính:

- 💬 Notification realtime (global / class / faculty / course / private)
- 💰 Payment realtime (theo transaction hoặc user)

Client sử dụng STOMP protocol để:

- subscribe nhận dữ liệu
- server send dữ liệu qua destination

### WebSocket Endpoint
ws://tl-connect-app-latest.onrender.com/ws

Authentication cần gửi token jwt 

### Prefix cấu hình (Server)

| Loại | Prefix |
| --- | --- |
| Public topic | /topic |
| Private queue | /queue |
| User destination | /user |
| Client send (optional) | /app |


### Notification WebSocket

**Notification Destination**

* Global notification
/topic/notification/global
* Class notification
/topic/notification/class/{classId}
* Faculty notification
/topic/notification/faculty/{facultyId}
* Course notification
/topic/notification/course/{courseId}
* Private notification (user-based)
/user/queue/notification

**Notification Payload**
{
  "id": 1,
  "title": "Thong bao he thong",
  "content": "He thong se bao tri",
  "createdBy": "Admin",
  "targetType": "ALL",
  "isImportant": true,
  "referenceType": "SYSTEM",
  "deadLine": "2026-06-10",
  "createdAt": "2026-06-08T10:00:00",
  "isRead": false
}

### Payment WebSocket

**Payment Destination**

* Private payment update
/user/queue/payment

**Payment Payload**
{
  "userId": 1,
  "tuitionId": 10,
  "transactionCode": "TXN_20260608_001",
  "status": "SUCCESS"
}