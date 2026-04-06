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
INSERT INTO emergency_contacts (student_id, full_name, phone_number, relationship, address) VALUES
(1,'Bo A','0911111111','Cha', 'Ha Noi'),
(2,'Me B','0922222222','Me', 'Ha Noi'),
(3,'Bo D','0933333333','Cha', 'Ha Noi'),
(4,'Me E','0944444444','Me', 'Ha Noi'),
(5,'Bo F','0955555555','Cha', 'Ha Noi'),
(6,'Me G','0966666666','Me', 'Ha Noi'),
(7,'Bo H','0977777777','Cha', 'Ha Noi');

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
(subject_id, semester_id, exam_date, start_time, end_time, exam_room, exam_format, exam_location, exam_type)
VALUES
(1,1,'2026-01-10','08:00','10:00','A101','OFFLINE', 'Cơ sở 1', 'Final'),
(2,1,'2026-01-12','13:00','15:00','A102','OFFLINE', 'Cơ sở 1', 'Final'),
(4,1,'2026-01-15','09:00','11:00','B201','OFFLINE', 'Cơ sở 1', 'Midterm'),

-- future
(3,2,'2026-06-10','08:00','10:00','A201','Bài tập lớn', 'Cơ sở 1', 'Final'),
(5,2,'2026-06-12','13:00','15:00','B202','OFFLINE','Cơ sở 1', 'Final');

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
(title, content, created_by, target_type, target_id, reference_id, reference_type, deadline)
VALUES

-- ALL
('Thong bao he thong','He thong se bao tri vao 23:00 toi nay','SYSTEM','GLOBAL',0,NULL,NULL,NULL),
('Cap nhat cong thong tin','Da cap nhat giao dien moi','SYSTEM','GLOBAL',0,NULL,NULL,NULL),
('Thong bao nghi le','Sinh vien nghi le quoc khanh','SYSTEM','GLOBAL',0,NULL,NULL,NULL),
('Lich thi HK1','Lich thi da duoc cap nhat tren portal','SYSTEM','GLOBAL',0,NULL,NULL,'2024-01-05'),
('Ket qua hoc bong','Danh sach hoc bong HK1 da duoc cong bo','SYSTEM','GLOBAL',0,NULL,NULL,NULL),

-- STUDENT_CLASS
('Canh bao hoc vu','Ket qua hoc tap duoi muc yeu cau','SYSTEM','STUDENT_CLASS',2,NULL,NULL,'2024-03-01'),
('Canh bao hoc vu lan 2','Sinh vien can gap co van hoc tap','SYSTEM','STUDENT_CLASS',2,NULL,NULL,'2024-03-10'),
('Thong bao rieng','Sinh vien duoc chon tham gia workshop','SYSTEM','STUDENT_CLASS',3,NULL,NULL,NULL),
('Thong bao rieng','Sinh vien duoc cap tai khoan lab','SYSTEM','STUDENT_CLASS',4,NULL,NULL,NULL),
('Thong bao rieng','Sinh vien cap nhat thong tin ca nhan','SYSTEM','STUDENT_CLASS',5,NULL,NULL,NULL),

-- COURSE_CLASS
('Thong bao mon hoc','Lop lap trinh web thay doi phong hoc','LECTURER','COURSE_CLASS',10,NULL,NULL,NULL),
('Thong bao mon hoc','Buoi hoc toi se hoc online','LECTURER','COURSE_CLASS',10,NULL,NULL,NULL),
('Thong bao mon hoc','Deadline project duoc gia han','LECTURER','COURSE_CLASS',10,NULL,NULL,'2024-04-10'),


-- FACULTY
('Thong bao khoa CNTT','Sinh vien tham gia hoi thao AI','FACULTY','FACULTY',1,NULL,NULL,NULL),
('Thong bao khoa CNTT','Cuoc thi lap trinh sap dien ra','FACULTY','FACULTY',1,NULL,NULL,NULL),
('Thong bao khoa CNTT','Mo dang ky CLB AI','FACULTY','FACULTY',1,NULL,NULL,NULL),
('Thong bao khoa CNTT','Workshop Cloud Computing','FACULTY','FACULTY',1,NULL,NULL,NULL),
('Thong bao khoa CNTT','Sinh vien dang ky thuc tap he','FACULTY','FACULTY',1,NULL,NULL,NULL);

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
INSERT INTO news (title, excerpt, image_url, image_key, source, publish_date, news_url)
VALUES
('Sinh viên Kế toán định hướng nghề nghiệp trong thời đại số tại workshop “Accounting Next Gen: Kế toán trong thời đại chuyển đổi số”','Chiều ngày 19.12.2025, workshop “Accounting next gen: Kế toán trong thời đại chuyển đổi số” do Trường Đại học Thăng Long phối hợp cùng ACCA (Hiệp hội Kế toán Công chứng Anh quốc) và Công ty Cổ phần Misa (doanh nghiệp công nghệ thông tin hàng đầu Việt Nam, cung cấp giải pháp chuyển đổi số trong lĩnh vực tài chính kế toán và quản trị doanh nghiệp) tổ chức đã diễn ra thành công rực rỡ, thu hút sự tham gia của đông đảo sinh viên từ năm 2 đến năm 4 ngành Kế toán.','https://res.cloudinary.com/dm5ev1isi/image/upload/v1775458101/uploads/1775458100898_605685389_1322159439950890_5475185904169852220_n.jpg.png', 'uploads/1775458100898_605685389_1322159439950890_5475185904169852220_n.jpg','Khoa kinh tế','2025-12-20','https://thanglong.edu.vn/sinh-vien-ke-toan-dinh-huong-nghe-nghiep-trong-thoi-dai-so-tai-workshop-accounting-next-gen-ke-toan-trong-thoi-dai-chuyen-doi-so-21619.html'),
('Sinh viên, học viên khoa Khoa học sức khỏe mở mang kiến thức chuyên ngành với tọa đàm “Đổi mới sáng tạo trong nghiên cứu khoa học và đào tạo nhân lực y tế”', 'Ngày 30.12.2025, tại Trường Đại học Thăng Long, tọa đàm với chủ đề “Đổi mới sáng tạo trong nghiên cứu khoa học và đào tạo nhân lực Y tế” đã diễn ra trong không khí sôi nổi, thu hút đông đảo giảng viên, nhà nghiên cứu, học viên, sinh viên khoa Khoa học sức khỏe tham dự.','https://res.cloudinary.com/dm5ev1isi/image/upload/v1775458052/uploads/1775458050825_608115782_1322766886556812_5292800162340264472_n.jpg.jpg', 'uploads/1775458050825_608115782_1322766886556812_5292800162340264472_n.jpg', 'Khoa điều dưỡngTruong','2025-12-31','https://thanglong.edu.vn/sinh-vien-hoc-vien-khoa-khoa-hoc-suc-khoe-mo-mang-kien-thuc-chuyen-nganh-voi-toa-dam-doi-moi-sang-tao-trong-nghien-cuu-khoa-hoc-va-dao-tao-nhan-luc-y-te-21633.html'),
('Sinh viên Khoa Công nghệ thông tin tham gia trải nghiệm thực tế tại Công ty Cổ phần VTI','Ngày 08.12, sinh viên Khoa Công nghệ Thông tin, Trường Đại học Thăng Long vừa có chuyến tham quan, trải nghiệm thực tế đầy ý nghĩa tại Công ty Cổ phần VTI - tập đoàn công nghệ hàng đầu tại Việt Nam.','https://res.cloudinary.com/dm5ev1isi/image/upload/v1775458467/uploads/1775458464887_596791541_1306010964899071_2118744865079236732_n.jpg.png','uploads/1775458464887_596791541_1306010964899071_2118744865079236732_n.jpg','Khoa công nghệ thông tin','2025-12-09','https://thanglong.edu.vn/sinh-vien-khoa-cong-nghe-thong-tin-tham-gia-trai-nghiem-thuc-te-tai-cong-ty-co-phan-vti-21587.html'),
('THÔNG BÁO (GẤP) Gia hạn thời gian nhận hồ sơ xét tốt nghiệp hệ chính quy đợt 1 năm 2026','',null, null,'Phòng Đào tạo', '2025-01-31', 'https://thanglong.edu.vn/thong-bao-gap-gia-han-thoi-gian-nhan-ho-so-xet-tot-nghiep-he-chinh-quy-dot-1-nam-2026-21653.html'),
($$Bộ môn Luật kinh tế tổ chức thành công hội thảo khoa học “Các vấn đề pháp lý mới trong kỷ nguyên số: Vai trò của các cơ sở đào tạo, nghiên cứu luật”$$,$$Vào ngày 31.12.2025, Hội thảo Khoa học cấp trường “Các vấn đề pháp lý mới trong kỷ nguyên số: Vai trò của các cơ sở đào tạo, nghiên cứu luật” được đồng tổ chức bởi Bộ môn Luật Kinh tế, Trường Đại học Thăng Long và Viện Chính sách công và Pháp luật (IPL) đã diễn ra thành công. Hội thảo thu hút sự tham gia của đông đảo các nhà khoa học, giảng viên, chuyên viên pháp lý và sinh viên Bộ môn Luật kinh tế.$$,'https://res.cloudinary.com/dm5ev1isi/image/upload/v1775457941/uploads/1775457939544_607712071_1325747526258748_1488510943557057778_n.jpg.jpg', 'uploads/1775457939544_607712071_1325747526258748_1488510943557057778_n.jpg','Bộ môn Luật kinh tế','2026-01-01','https://thanglong.edu.vn/bo-mon-luat-kinh-te-to-chuc-thanh-cong-hoi-thao-khoa-hoc-cac-van-de-phap-ly-moi-trong-ky-nguyen-so-vai-tro-cua-cac-co-so-dao-tao-nghien-cuu-luat-21632.html');

-- tuition_fee_configs
INSERT INTO tuition_fee_configs
(base_price_per_credit, effective_from, effective_to)
VALUES
(500000, '2025-01-01', '2025-02-01'),
(520000, '2025-01-01', '2025-02-01'),
(480000, '2025-01-01', '2025-01-15'),
(700000, '2025-01-01', '2025-02-01'),
(750000, '2025-01-01', '2025-02-01'),
(450000, '2025-01-01', '2025-02-01');

-- tuition_invoices
INSERT INTO tuition_invoices
(student_id, semester_id, due_date, total_amount, final_amount, status, created_at, updated_at)
VALUES
-- đã thanh toán
(1, 1, '2026-01-10', 4500000, 4500000, 'PAID', '2025-12-01', '2025-12-05'),

-- chưa thanh toán (quá hạn)
(2, 1, '2026-01-05', 4200000, 4200000, 'OVERDUE', '2025-12-01', '2026-01-06'),

-- chưa thanh toán (còn hạn)
(3, 1, '2026-01-15', 4300000, 4300000, 'UNPAID', '2025-12-01', '2025-12-01'),

-- học kỳ sau
(4, 2, '2026-05-01', 5000000, 5000000, 'UNPAID', '2026-03-01', '2026-03-01'),

-- đã thanh toán học kỳ sau
(5, 2, '2026-05-01', 5200000, 5200000, 'PAID', '2026-03-01', '2026-03-10'),

-- bị hủy
(6, 1, '2026-01-10', 4000000, 4000000, 'CANCELLED', '2025-12-01', '2025-12-20');

-- tuition_invoice_items
INSERT INTO tuition_invoice_items
(invoice_id, course_class_id, price_per_credit, credits, coefficient, amount, created_at, updated_at)
VALUES
-- invoice 1 (đã paid)
(1, 1, 500000, 3, 1.0, 1500000, '2025-12-01', '2025-12-01'),
(1, 2, 520000, 3, 1.0, 1560000, '2025-12-01', '2025-12-01'),
(1, 3, 480000, 3, 1.0, 1440000, '2025-12-01', '2025-12-01'),

-- invoice 2 (overdue)
(2, 1, 500000, 3, 1.0, 1500000, '2025-12-01', '2025-12-01'),
(2, 2, 520000, 3, 1.0, 1560000, '2025-12-01', '2025-12-01'),

-- invoice 3 (unpaid)
(3, 4, 700000, 3, 1.0, 2100000, '2025-12-01', '2025-12-01'),
(3, 5, 750000, 3, 1.0, 2250000, '2025-12-01', '2025-12-01'),

-- invoice 4 (future semester)
(4, 6, 450000, 3, 1.0, 1350000, '2026-03-01', '2026-03-01'),

-- invoice 5 (paid future)
(5, 4, 700000, 3, 1.0, 2100000, '2026-03-01', '2026-03-01'),
(5, 5, 750000, 3, 1.0, 2250000, '2026-03-01', '2026-03-01'),

-- invoice 6 (cancelled)
(6, 1, 500000, 3, 1.0, 1500000, '2025-12-01', '2025-12-01');

-- payment
INSERT INTO payment
(invoice_id, amount, provider, transaction_code, status, created_at, updated_at)
VALUES
-- invoice 1: thanh toán thành công
(1, 4500000, 'ZALOPAY', 'TXN_001_SUCCESS', 'SUCCESS', '2025-12-05', '2025-12-05'),

-- invoice 2: thanh toán thất bại
(2, 4200000, 'ZALOPAY', 'TXN_002_FAIL', 'FAILED', '2025-12-07', '2025-12-07'),

-- retry nhưng vẫn pending
(2, 4200000, 'ZALOPAY', 'TXN_002_PENDING', 'PENDING', '2025-12-08', '2025-12-08'),

-- invoice 3: user vừa tạo thanh toán (chưa xong)
(3, 4300000, 'BANK', 'TXN_003_PENDING', 'PENDING', '2025-12-09', '2025-12-09'),

-- invoice 5: thanh toán thành công (future semester)
(5, 5200000, 'BANK', 'TXN_005_SUCCESS', 'SUCCESS', '2026-03-10', '2026-03-10'),

-- invoice 6: đã từng thanh toán nhưng bị hủy/refund sau
(6, 4000000, 'ZALOPAY', 'TXN_006_SUCCESS', 'SUCCESS', '2025-12-10', '2025-12-10');

-- tuition_transactions
INSERT INTO tuition_transactions
(student_id, invoice_id, amount, type, reference_id, reference_type, description, created_at)
VALUES
-- ===== INVOICE 1 =====
-- phát sinh học phí
(1, 1, 4500000, 'TUITION', 1, 'INVOICE', 'Tao hoa don hoc phi HK1', '2025-12-01'),

-- thanh toán
(1, 1, 4500000, 'PAYMENT', 1, 'PAYMENT', 'Thanh toan qua ZaloPay', '2025-12-05'),

-- ===== INVOICE 2 =====
(2, 2, 4200000, 'TUITION', 2, 'INVOICE', 'Hoc phi HK1', '2025-12-01'),

-- failed payment
(2, 2, 4200000, 'PAYMENT', 2, 'PAYMENT', 'Thanh toan that bai', '2025-12-07'),

-- ===== INVOICE 3 =====
(3, 3, 4300000, 'TUITION', 3, 'INVOICE', 'Hoc phi HK1', '2025-12-01'),

-- pending payment (chưa ghi nhận success)
(3, 3, 4300000, 'PAYMENT', 4, 'PAYMENT', 'Dang xu ly thanh toan', '2025-12-09'),

-- ===== INVOICE 5 =====
(5, 5, 5200000, 'TUITION', 5, 'INVOICE', 'Hoc phi HK2', '2026-03-01'),

(5, 5, 5200000, 'PAYMENT', 5, 'PAYMENT', 'Thanh toan ngan hang', '2026-03-10'),

-- ===== INVOICE 6 (cancel + refund) =====
(6, 6, 4000000, 'TUITION', 6, 'INVOICE', 'Hoc phi HK1', '2025-12-01'),

-- đã thanh toán
(6, 6, 4000000, 'PAYMENT', 6, 'PAYMENT', 'Thanh toan thanh cong', '2025-12-10'),

-- refund
(6, 6, -4000000, 'REFUND', 6, 'PAYMENT', 'Hoan tien do huy hoa don', '2025-12-20');

INSERT INTO subject_prerequisite_groups
(subject_id, min_subjects_required, description, created_at, updated_at)
VALUES

(5, 2, 'Can it nhat 2 mon nen tang AI/CTDL', '2025-12-01', '2025-12-01'),


(4, 1, 'Can 1 mon co so lap trinh', '2025-12-01', '2025-12-01'),


(6, 1, 'Chon 1 mon marketing co ban', '2025-12-01', '2025-12-01');

INSERT INTO subject_prerequisite_group_items
(group_id, prerequisite_subject_id, created_at)
VALUES

(1, 1, '2025-12-01'), -- Lap trinh co ban
(1, 2, '2025-12-01'), -- CTDL
(1, 4, '2025-12-01'), -- AI101


(2, 1, '2025-12-01'),


(3, 6, '2025-12-01'),
(3, 3, '2025-12-01');

INSERT INTO subject_enrollment_conditions
(subject_id, condition_type, condition_value, condition_operator, description, created_at, updated_at)
VALUES

(5, 'GPA', 2.50, '>=', 'Yeu cau GPA toi thieu 2.5', '2025-12-01', '2025-12-01'),


(5, 'TOTAL_CREDITS', 30, '>=', 'Phai tich luy it nhat 30 tin chi', '2025-12-01', '2025-12-01'),

(4, 'GPA', 2.00, '>', 'Yeu cau GPA > 2.0', '2025-12-01', '2025-12-01'),

(6, 'TOTAL_CREDITS', 20, '>=', 'Yeu cau >= 20 tin chi', '2025-12-01', '2025-12-01'),

(6, 'CURRENT_SEMESTER', 2, '=', 'Chi duoc dang ky o HK2', '2025-12-01', '2025-12-01');

INSERT INTO student_course_class_logs
(student_id, course_class_id, action, from_status, to_status, created_at)
VALUES
-- ===== STUDENT 1: flow chuẩn =====
-- đăng ký thành công
(1, 1, 'ENROLL', 'PENDING', 'ENROLLED', '2025-12-01'),

-- hủy lớp
(1, 1, 'DROP', 'ENROLLED', 'DROPPED', '2025-12-10'),

-- đăng ký lại
(1, 1, 'ENROLL', 'DROPPED', 'ENROLLED', '2025-12-15'),

-- ===== STUDENT 2: fail do GPA =====
(2, 4, 'REJECTED', 'PENDING', 'REJECTED', '2025-12-01'),

-- ===== STUDENT 3: fail prerequisite =====
(3, 5, 'REJECTED', 'PENDING', 'REJECTED', '2025-12-01'),

-- ===== STUDENT 4: pending → enrolled =====
(4, 2, 'ENROLL', 'PENDING', 'ENROLLED', '2025-12-02'),

-- ===== STUDENT 5: enroll future semester =====
(5, 4, 'ENROLL', 'PENDING', 'ENROLLED', '2026-03-05'),

-- ===== STUDENT 6: reject rồi thử lại =====
(6, 3, 'REJECTED', 'PENDING', 'REJECTED', '2025-12-01'),
(6, 3, 'ENROLL', 'REJECTED', 'ENROLLED', '2025-12-20'),

-- ===== STUDENT 7: enroll rồi drop =====
(7, 5, 'ENROLL', 'PENDING', 'ENROLLED', '2025-12-03'),
(7, 5, 'DROP', 'ENROLLED', 'DROPPED', '2025-12-12');

 COMMIT;