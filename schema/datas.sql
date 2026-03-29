BEGIN;
SET CONSTRAINTS ALL DEFERRED;

TRUNCATE TABLE 
  user_roles, 
  student_contacts, 
  emergency_contacts, 
  identity_cards, 
  health_insurances, 
  academic_infos, 
  student_classes, 
  majors, 
  student_majors, 
  academic_advisors, 
  faculties, 
  departments, 
  subjects, 
  study_programs, 
  study_program_subjects, 
  semesters, 
  student_course_classes, 
  student_semester_summaries, 
  exam_schedules, 
  student_exam_registrations, 
  application_types, 
  student_applications, 
  application_attachments, 
  class_schedules, 
  course_classes, 
  students, 
  roles, 
  oauth_users, 
  notifications, 
  lecturers, 
  student_subject_results, 
  grade_scale, 
  news, 
  notification_template, 
  notification_read, 
  tuition_invoices, 
  tuition_invoice_items, 
  tuition_fee_configs, 
  payment, 
  tuition_transactions, 
  subject_prerequisite_group_items, 
  subject_prerequisite_groups, 
  subject_enrollment_conditions, 
  student_course_class_logs
  
RESTART IDENTITY CASCADE;

-- oauth_users
INSERT INTO oauth_users (user_uuid, display_name, email)
VALUES
('1deb00a9-835c-4ab7-a50f-57c12a56c7bd', 'Nguyen Van An', 'nhokthanh3211@gmail.com'),
('a65d03d4-6a2a-426f-963d-8dca24399b83', 'Tran Thi Bich', 'bich.tran@university.edu.vn'),
('uuid-lecturer-003', 'Le Van Cuong', 'cuong.le@university.edu.vn'),
('uuid-4','Pham Van D','d@gmail.com'),
('uuid-5','Hoang Thi E','e@gmail.com'),
('uuid-6','Do Van F','f@gmail.com'),
('uuid-7','Nguyen Thi G','g@gmail.com'),
('uuid-8','Tran Van H','h@gmail.com'),
('uuid-9','Le Thi I','i@gmail.com'),
('uuid-10','Pham Van K','k@gmail.com');

-- roles
INSERT INTO roles (code, name) VALUES
('ADMIN', 'Administrator'),
('STUDENT', 'Student'),
('LECTURER', 'Lecturer');

-- user_roles
INSERT INTO user_roles (oauth_user_id, role_id) VALUES
(1,2),(2,2),(4,2),(5,2),(6,2),(7,2),(8,2),
(3,3),(9,3),(10,3);

-- faculties
INSERT INTO faculties (faculty_code, faculty_name) VALUES
('CNTT','Cong nghe thong tin'),
('QTKD','Quan tri kinh doanh');

-- departments
INSERT INTO departments (faculty_id, department_code, department_name) VALUES
(1,'SE','Software Engineering'),
(1,'AI','Artificial Intelligence'),
(2,'MK','Marketing');

-- majors
INSERT INTO majors (major_code, major_name, faculty_id) VALUES
('SE01','Ky thuat phan mem',1),
('AI01','Tri tue nhan tao',1),
('MK01','Marketing',2);

-- student_classes
INSERT INTO student_classes (class_code, major_id, start_year) VALUES
('SE2023A',1,2023),
('SE2024A',1,2024),
('AI2024A',2,2024),
('MK2023A',3,2023);

-- lecturers
INSERT INTO lecturers (oauth_user_id, department_id, full_name, lecturer_code, phone_number, email) VALUES
(3,1,'Dr. Le Van C','GV001', '0123456789','gv1@uni.edu'),
(9,2,'Dr. Le Thi I','GV002','0123456789','gv2@uni.edu'),
(10,3,'Dr. Pham Van K','GV003','0123456789','gv3@uni.edu');

-- academic_advisors
INSERT INTO academic_advisors (lecturer_id, student_class_id) VALUES
(1,1),
(1,2),
(2,3),
(3,4);

-- students
INSERT INTO students (oauth_user_id, student_class_id, full_name, student_code, gender, date_of_birth, status) VALUES
(1,1,'Nguyen Van A','SV001','NAM','2003-05-10','ACTIVE'),
(2,2,'Tran Thi B','SV002','NU','2004-08-20','ACTIVE'),
(4,1,'Pham Van D','SV003','NAM','2003-02-11','ACTIVE'),
(5,2,'Hoang Thi E','SV004','NU','2004-01-01','ACTIVE'),
(6,3,'Do Van F','SV005','NAM','2004-09-09','ACTIVE'),
(7,3,'Nguyen Thi G','SV006','NU','2004-03-03','ACTIVE'),
(8,4,'Tran Van H','SV007','NAM','2003-12-12','ACTIVE');

-- student_contacts
INSERT INTO student_contacts (student_id, phone_number, address, email_personal) VALUES
(1,'0900000001','Ha Noi','a1@gmail.com'),
(2,'0900000002','Hai Phong','b1@gmail.com'),
(3,'0900000003','Ha Noi','d1@gmail.com'),
(4,'0900000004','Nam Dinh','e1@gmail.com'),
(5,'0900000005','Da Nang','f1@gmail.com'),
(6,'0900000006','Hue','g1@gmail.com'),
(7,'0900000007','HCM','h1@gmail.com');

-- emergency_contacts
INSERT INTO emergency_contacts (student_id, full_name, phone_number, address) VALUES
(1,'Bo A','0911111111','Cha'),
(2,'Me B','0922222222','Me'),
(3,'Bo D','0933333333','Cha'),
(4,'Me E','0944444444','Me'),
(5,'Bo F','0955555555','Cha'),
(6,'Me G','0966666666','Me'),
(7,'Bo H','0977777777','Cha');

-- identity_cards
INSERT INTO identity_cards (student_id, card_number, card_type, issued_date, issued_place) VALUES
(1,'100000000001', 'CCCD','2021-01-01','Ha Noi'),
(2,'100000000002','CCCD','2022-02-02','Hai Phong'),
(3,'100000000003','CCCD','2021-03-03','Ha Noi'),
(4,'100000000004','CCCD','2022-04-04','Nam Dinh'),
(5,'100000000005','CCCD','2023-05-05','Da Nang'),
(6,'100000000006','CCCD','2023-06-06','Hue'),
(7,'100000000007','CCCD','2021-07-07','HCM');

-- health_insurances
INSERT INTO health_insurances (student_id, insurance_number, provider, valid_from, valid_to, registered_hospital) VALUES
(1,'BHYT001','Bao Viet','2024-01-01','2026-12-31','BV Bach Mai'),
(2,'BHYT002','Bao Minh','2025-01-01','2027-12-31','BV Viet Duc'),
(3,'BHYT003','Bao Viet','2024-06-01','2026-06-01','BV 108'),
(4,'BHYT004','Bao Minh','2025-03-01','2027-03-01','BV Thanh Nhan'),
(5,'BHYT005','Bao Viet','2025-05-01','2028-05-01','BV Da Nang'),
(6,'BHYT006','Bao Minh','2024-09-01','2026-09-01','BV Hue'),
(7,'BHYT007','Bao Viet','2024-12-01','2027-12-01','BV Cho Ray');

-- semesters
INSERT INTO semesters (semester_name, semester_code, academic_years, semester_number, start_date, end_date)
VALUES
('Hoc ky 1 2025','HK1-2025','2025-2026',1,'2025-09-01','2026-01-15'),
('Hoc ky 2 2025','HK2-2025','2025-2026',2,'2026-02-01','2026-06-15'),
('Hoc ky 1 2026','HK1-2026','2026-2027',1,'2026-09-01','2027-01-15');

-- study_programs
INSERT INTO study_programs (major_id, study_program_code, study_program_name, total_credits, start_year) VALUES
(1,'CTDT_SE','Chuong trinh SE',130,2023),
(2,'CTDT_AI','Chuong trinh AI',135,2024),
(3,'CTDT_MK','Chuong trinh Marketing',120,2023);


-- student_majors

INSERT INTO student_majors (student_id, major_id, study_program_id, start_year, end_year) VALUES
(1,1,1,2023,2027),
(2,1,1,2024,2028),
(3,1,1,2023,2027),
(4,1,1,2024,2028),
(5,2,2,2024,2028),
(6,2,2,2024,2028),
(7,3,3,2023,2027);

-- academic_infos
INSERT INTO academic_infos (student_major_id, cohort, position) VALUES
(1,'K23','Sinh vien'),
(2,'K24','Sinh vien'),
(3,'K23','Lop pho'),
(4,'K24','Sinh vien'),
(5,'K24','Sinh vien'),
(6,'K24','Sinh vien'),
(7,'K23','Sinh vien');

-- subjects
INSERT INTO subjects (faculty_id, department_id, subject_code, subject_name, credits, lecture_hours, practice_hours)
VALUES
(1,1,'SE101','Nhap mon lap trinh',3,30,30),
(1,1,'SE102','Cau truc du lieu',3,30,30),
(1,1,'SE201','Lap trinh Java',3,30,30),
(1,2,'AI101','Nhap mon AI',3,30,30),
(1,2,'AI201','Machine Learning',4,45,30),
(2,3,'MK101','Marketing can ban',3,30,15),
(2,3,'MK201','Digital Marketing',3,30,30);

-- study_program_subjects
INSERT INTO study_program_subjects (study_program_id, subject_id, semester_id, is_required)
VALUES
-- SE
(1,1,1,true),
(1,2,1,true),
(1,3,2,true),

-- AI
(2,4,1,true),
(2,5,2,true),

-- Marketing
(3,6,1,true),
(3,7,2,true);

-- course_classes
INSERT INTO course_classes (lecturer_id, subject_id, semester_id, class_code, class_name, capacity)
VALUES
(1,1,1,'SE101-01','Lap trinh 01',50),
(1,2,1,'SE102-01','CTDL 01',50),
(2,4,1,'AI101-01','AI co ban',40),
(2,5,2,'AI201-01','Machine Learning',40),
(3,6,1,'MK101-01','Marketing 01',60),
(3,7,2,'MK201-01','Digital Marketing',60);

-- student_course_classes
INSERT INTO student_course_classes 
(student_id, course_class_id, subject_id, semester_id, status) VALUES
-- HK1 2025
(1,1,1,1,'ENROLLED'),
(1,2,2,1,'ENROLLED'),
(2,1,1,1,'ENROLLED'),
(3,2,2,1,'ENROLLED'),
(4,1,1,1,'ENROLLED'),
(5,3,4,1,'ENROLLED'),
(6,3,4,1,'ENROLLED'),
(7,5,6,1,'ENROLLED'),

-- HK2 2025
(1,3,3,2,'PENDING'),
(2,3,3,2,'PENDING'),
(5,4,5,2,'PENDING'),
(7,6,7,2,'PENDING');

-- class_schedules
INSERT INTO class_schedules
(course_class_id, day_of_week, start_period, end_period, start_time, end_time, room)
VALUES
-- SE101
(1,2,1,3,'07:30','10:00','A101'),
(1,4,1,3,'07:30','10:00','A101'),

-- SE102
(2,3,4,6,'13:00','16:00','A102'),
(2,5,4,6,'13:00','16:00','A102'),

-- AI101
(3,2,7,9,'18:00','20:30','B201'),

-- AI201 
(4,3,7,9,'18:00','20:30','B202'),

-- MK101
(5,6,1,3,'07:30','10:00','C301'),

-- MK201 
(6,7,4,6,'13:00','16:00','C302');

-- exam_schedules
INSERT INTO exam_schedules
(subject_id, semester_id, exam_date, start_time, end_time, exam_room, exam_format)
VALUES
(1,1,'2026-01-10','08:00','10:00','A101','OFFLINE'),
(2,1,'2026-01-12','13:00','15:00','A102','OFFLINE'),
(4,1,'2026-01-15','09:00','11:00','B201','OFFLINE'),

-- future
(3,2,'2026-06-10','08:00','10:00','A201','OFFLINE'),
(5,2,'2026-06-12','13:00','15:00','B202','OFFLINE');

-- student_exam_registrations
INSERT INTO student_exam_registrations
(student_id, exam_schedule_id, exam_attempt, attendance_status)
VALUES
(1,1,1,'UPCOMING'),
(1,2,1,'UPCOMING'),
(2,1,1,'UPCOMING'),
(3,2,1,'UPCOMING'),
(5,3,1,'UPCOMING');

-- student_subject_results
INSERT INTO student_subject_results
(student_id, subject_id, semester_id, credits,
 attendance_score, midterm_score, final_score,
 score_10, score_4, letter_grade, is_pass)
VALUES
-- pass tốt
(1,1,1,3,9,8,9,8.8,3.7,'A',true),

-- trung bình
(2,1,1,3,7,7,6,6.7,2.5,'C',true),

-- fail
(3,2,1,3,5,4,3,3.8,0,'F',false),

-- khá
(4,2,1,3,8,7,8,7.8,3.2,'B',true),

-- AI
(5,4,1,3,8,8,7,7.5,3.0,'B',true),

-- marketing
(7,6,1,3,6,6,6,6.0,2.0,'C',true);

-- student_semester_summaries
INSERT INTO student_semester_summaries
(student_id, study_program_id, semester_id, credits_registered, credits_passed,
 semester_gpa, letter_gpa, conduct_score, activity_score, letter_activity_score,
 group_contribution, letter_group_contribution)
VALUES
(1,1,1,6,6,3.2,'B',80,8.0,'B',7.5,'B'),
(2,1,1,3,3,3.5,'A',85,8.5,'A',8.0,'A'),
(5,2,1,3,3,3.0,'B',75,7.0,'B',7.0,'B'),
(7,3,1,3,3,2.8,'C',70,6.5,'C',6.0,'C');

-- application_types
INSERT INTO application_types (code, name)
VALUES
('LEAVE','Xin nghi hoc'),
('GRADE_REVIEW','Phuc khao diem'),
('CERT','Xin giay xac nhan');

-- student_applications
INSERT INTO student_applications
(student_id, application_type_id, content, status)
VALUES
(1,1,'Xin nghi vi om','PENDING'),
(2,2,'Phuc khao mon Java','APPROVED'),
(3,3,'Xin giay xac nhan sinh vien','PENDING'),
(5,1,'Xin nghi viec gia dinh','REJECTED');

-- application_attachments
INSERT INTO application_attachments
(application_id, file_key, original_filename, file_size)
VALUES
(1,'file1.pdf','don_xin_nghi.pdf',123456),
(2,'file2.pdf','phuc_khao.pdf',223456),
(3,'file3.pdf','giay_xac_nhan.pdf',323456);

-- notification_template
INSERT INTO notification_template (code, name, content) VALUES
('GENERAL', 'Thong bao chung', '{{content}}'),
('EXAM', 'Thong bao lich thi', 'Lich thi: {{content}}'),
('FEE', 'Nhac nop hoc phi', 'Sinh vien can nop hoc phi truoc {{deadline}}'),
('SCHOLARSHIP', 'Thong bao hoc bong', '{{content}}'),
('ACADEMIC_WARNING', 'Canh bao hoc vu', '{{content}}'),
('DEFENSE', 'Thong bao bao ve do an', '{{content}}');

-- notifications
INSERT INTO notifications
(template_id, title, content, created_by, target_type, target_id, reference_id, reference_type, deadline)
VALUES

-- ALL
(1,'Thong bao he thong','He thong se bao tri vao 23:00 toi nay','Admin','ALL',0,NULL,NULL,NULL),
(1,'Cap nhat cong thong tin','Da cap nhat giao dien moi','Admin','ALL',0,NULL,NULL,NULL),
(1,'Thong bao nghi le','Sinh vien nghi le quoc khanh','Ban Giam Hieu','ALL',0,NULL,NULL,NULL),
(2,'Lich thi HK1','Lich thi da duoc cap nhat tren portal','Phong Dao Tao','ALL',0,NULL,NULL,'2024-01-05'),
(4,'Ket qua hoc bong','Danh sach hoc bong HK1 da duoc cong bo','Phong CTSV','ALL',0,NULL,NULL,NULL),

-- CLASS
(3,'Nhac nop hoc phi','Sinh vien lop KHMT2021 nop hoc phi HK2','Phong Tai Chinh','CLASS',1,NULL,NULL,'2024-02-28'),
(3,'Nhac nop hoc phi lan 2','Sinh vien chua nop hoc phi vui long hoan thanh','Phong Tai Chinh','CLASS',1,NULL,NULL,'2024-03-05'),
(6,'Lich bao ve do an','Sinh vien xem lich bao ve mon Web','GV Tran Thi Bich','CLASS',4,NULL,NULL,'2024-06-20'),
(1,'Thong bao hoc tap','Sinh vien nop bai tap tuan 5','GV Nguyen Van A','CLASS',2,NULL,NULL,NULL),
(1,'Thong bao hoc tap','Sinh vien nop bai tap tuan 6','GV Nguyen Van A','CLASS',2,NULL,NULL,NULL),

-- STUDENT_CLASS
(5,'Canh bao hoc vu','Ket qua hoc tap duoi muc yeu cau','Phong Dao Tao','STUDENT_CLASS',2,NULL,NULL,'2024-03-01'),
(5,'Canh bao hoc vu lan 2','Sinh vien can gap co van hoc tap','Phong Dao Tao','STUDENT_CLASS',2,NULL,NULL,'2024-03-10'),
(1,'Thong bao rieng','Sinh vien duoc chon tham gia workshop','Phong CTSV','STUDENT_CLASS',3,NULL,NULL,NULL),
(1,'Thong bao rieng','Sinh vien duoc cap tai khoan lab','Phong CNTT','STUDENT_CLASS',4,NULL,NULL,NULL),
(1,'Thong bao rieng','Sinh vien cap nhat thong tin ca nhan','Phong Dao Tao','STUDENT_CLASS',5,NULL,NULL,NULL),

-- COURSE_CLASS
(1,'Thong bao mon hoc','Lop lap trinh web thay doi phong hoc','GV Tran','COURSE_CLASS',10,NULL,NULL,NULL),
(1,'Thong bao mon hoc','Buoi hoc toi se hoc online','GV Tran','COURSE_CLASS',10,NULL,NULL,NULL),
(1,'Thong bao mon hoc','Deadline project duoc gia han','GV Tran','COURSE_CLASS',10,NULL,NULL,'2024-04-10'),
(1,'Thong bao mon hoc','Upload slide bai giang moi','GV Tran','COURSE_CLASS',10,NULL,NULL,NULL),
(1,'Thong bao mon hoc','Sinh vien chuan bi demo giua ky','GV Tran','COURSE_CLASS',10,NULL,NULL,NULL),

-- FACULTY
(1,'Thong bao khoa CNTT','Sinh vien tham gia hoi thao AI','Khoa CNTT','FACULTY',1,NULL,NULL,NULL),
(1,'Thong bao khoa CNTT','Cuoc thi lap trinh sap dien ra','Khoa CNTT','FACULTY',1,NULL,NULL,NULL),
(1,'Thong bao khoa CNTT','Mo dang ky CLB AI','Khoa CNTT','FACULTY',1,NULL,NULL,NULL),
(1,'Thong bao khoa CNTT','Workshop Cloud Computing','Khoa CNTT','FACULTY',1,NULL,NULL,NULL),
(1,'Thong bao khoa CNTT','Sinh vien dang ky thuc tap he','Khoa CNTT','FACULTY',1,NULL,NULL,NULL);

-- notification_read
INSERT INTO notification_read (notification_id, oauth_user_id, read_at) VALUES
(1,1,now()),
(2,1,now()),
(3,1,now()),
(4,1,now()),

(1,2,now()),
(2,2,now()),

(5,3,now()),
(6,3,now()),
(7,3,now()),

(10,4,now()),
(11,4,now()),

(15,5,now());

INSERT INTO grade_scale (min_score, max_score, letter_grade) VALUES
(9.50, 10.00, 'A+'),
(9.00, 9.50,  'A'),
(8.50, 9.00,  'A-'),
(8.00, 8.50,  'B+'),
(7.50, 8.00,  'B'),
(7.00, 7.50,  'B-'),
(6.50, 7.00,  'C'),
(6.00, 7.50,  'D'),
(5.00, 6.00,  'E'),
(0.00, 5.00,  'F');

-- news
INSERT INTO news (title, excerpt, image_url, source, publish_date, news_url)
VALUES
('Thong bao tuyen sinh 2026','Mo don dang ky','img1.jpg','Bo GD','2026-03-01','link1'),
('Cap nhat chuong trinh hoc','Them mon AI','img2.jpg','Truong','2026-02-15','link2'),
('Lich nghi le','Thong bao nghi le','img3.jpg','Truong','2026-04-20','link3'),

('Su kien cong nghe','Hoi thao AI','img4.jpg','Truong','2026-06-01','link4');

-- tuition_fee_configs
INSERT INTO tuition_fee_configs
(subject_id, price_per_credit, effective_from, effective_to)
VALUES
(1, 500000, '2025-01-01', 2025-02-01),
(2, 520000, '2025-01-01', 2025-02-01),
(3, 480000, '2025-01-01', 2025-01-15),
(4, 700000, '2025-01-01', 2025-02-01),
(5, 750000, '2025-01-01', 2025-02-01),
(6, 450000, '2025-01-01', NULL);

-- tuition_invoices
INSERT INTO tuition_invoices
(student_id, semester_id, total_amount, paid_amount, status, due_date, created_at)
VALUES
(1,1,4500000,4500000,'PAID','2026-01-10','2025-12-01'),

(2,1,4200000,0,'OVERDUE','2026-01-05','2025-12-01'),

(3,1,4300000,2000000,'PARTIAL','2026-01-10','2025-12-01'),

(4,2,5000000,0,'UNPAID','2026-05-01','2026-03-01'),

(5,2,5200000,5200000,'PAID','2026-05-01','2026-03-01');

-- tuition_invoice_items
INSERT INTO tuition_invoice_items
(invoice_id, subject_id, credits, price_per_credit, amount)
VALUES
-- invoice 1
(1,1,3,500000,1500000),
(1,2,3,520000,1560000),
(1,3,3,480000,1440000),

-- invoice 2
(2,1,3,500000,1500000),
(2,2,3,520000,1560000),

-- invoice 3
(3,4,3,700000,2100000),
(3,5,3,750000,2250000),

-- invoice 4
(4,6,3,450000,1350000),

-- invoice 5
(5,4,3,700000,2100000),
(5,5,3,750000,2250000);

-- payment
INSERT INTO payment
(student_id, invoice_id, amount, method, status, created_at)
VALUES
(1,1,4500000,'ZALOPAY','SUCCESS','2025-12-05'),

-- partial
(3,3,2000000,'BANK','SUCCESS','2025-12-06'),

-- failed payment
(2,2,4200000,'ZALOPAY','FAILED','2025-12-07'),

-- retry success
(5,5,5200000,'BANK','SUCCESS','2026-03-10');

-- tuition_transactions
INSERT INTO tuition_transactions
(invoice_id, amount, type, status, created_at)
VALUES
(1,4500000,'PAYMENT','SUCCESS','2025-12-05'),

(3,2000000,'PAYMENT','SUCCESS','2025-12-06'),

(2,4200000,'PAYMENT','FAILED','2025-12-07'),

(5,5200000,'PAYMENT','SUCCESS','2026-03-10');

INSERT INTO subject_prerequisite_groups
(subject_id, group_type)
VALUES
-- AI201 cần nhóm điều kiện
(5,'AND');

INSERT INTO subject_prerequisite_group_items
(group_id, prerequisite_subject_id)
VALUES
(1,4), -- phải học AI101
(1,2); -- và CTDL

INSERT INTO subject_enrollment_conditions
(subject_id, min_gpa, required_credit)
VALUES
(5, 2.5, 30), -- AI201 yêu cầu GPA + số tín chỉ
(4, 2.0, 20);

INSERT INTO student_course_class_logs
(student_id, course_class_id, action, status, message, created_at)
VALUES

(1,1,'ENROLL','SUCCESS','Dang ky thanh cong','2025-12-01'),


(3,4,'ENROLL','FAILED','Chua hoc mon tien quyet','2025-12-01'),


(2,4,'ENROLL','FAILED','Khong du GPA','2025-12-01'),


(1,1,'DROP','SUCCESS','Huy lop','2025-12-10'),


(1,1,'ENROLL','SUCCESS','Dang ky lai','2025-12-15');

 COMMIT;