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
  notification_templates, 
  notification_read, 
  tuition_invoices, 
  tuition_invoice_items, 
  tuition_fee_configs, 
  payment, 
  tuition_transactions, 
  subject_prerequisite_group_items, 
  subject_prerequisite_groups, 
  subject_enrollment_conditions, 
  student_course_class_logs,
  notification_targets
  
RESTART IDENTITY CASCADE;

-- oauth_users
INSERT INTO oauth_users (user_uuid, display_name, email)
VALUES
('1deb00a9-835c-4ab7-a50f-57c12a56c7bd', 'Nguyễn Văn An', 'nhokthanh3211@gmail.com'),
('a65d03d4-6a2a-426f-963d-8dca24399b83', 'Trần Thị Bích', 'bich.tran@university.edu.vn'),
('4a43cbd4-f3c6-4f5e-8db3-5b3aa1ef7369', 'Phùng Thanh Độ', 'a45044@thanglong.edu.vn'),
('uuid-4','Phạm Quang Huy','a45123@thanglong.edu.vn'),
('uuid-5','Hoàng Thu Hà','a45124@thanglong.edu.vn'),
('uuid-6','Đỗ Minh Tuấn','a45125@thanglong.edu.vn'),
('uuid-7','Nguyễn Thị Lan Anh','a45126@thanglong.edu.vn'),
('uuid-8','Trần Quốc Bảo','a45127@thanglong.edu.vn'),
('uuid-9','Lê Hoàng Nam','a45128@thanglong.edu.vn'),
('uuid-10','Phạm Gia Khánh','a45129@thanglong.edu.vn'),
('uuid-11','Nguyễn Hoàng Long','a45130@thanglong.edu.vn'),
('uuid-12','Trần Minh Tuấn','a45131@thanglong.edu.vn'),
('uuid-13','Lê Thị Thu Trang','a45132@thanglong.edu.vn'),
('uuid-14','Phạm Đức Anh','a45133@thanglong.edu.vn'),
('uuid-15','Hoàng Ngọc Linh','a45134@thanglong.edu.vn'),
('uuid-16','Đỗ Quang Trung','a45135@thanglong.edu.vn'),
('uuid-17','Nguyễn Hải Đăng','a45136@thanglong.edu.vn'),
('uuid-18','Trần Thu Phương','a45137@thanglong.edu.vn'),
('uuid-19','Lê Quốc Khánh','a45138@thanglong.edu.vn'),
('uuid-20','Phạm Thùy Dương','a45139@thanglong.edu.vn'),
('uuid-21','Hoàng Minh Khang','a45140@thanglong.edu.vn'),
('uuid-22','Đỗ Thị Ngọc Anh','a45141@thanglong.edu.vn'),
('uuid-23','Nguyễn Văn Hưng','a45142@thanglong.edu.vn'),
('uuid-24','Trần Đức Thành','a45143@thanglong.edu.vn'),
('uuid-25','Lê Thanh Tùng','a45144@thanglong.edu.vn'),
('uuid-26','Phạm Nhật Quang','a45145@thanglong.edu.vn'),
('uuid-27','Hoàng Thị Mai','a45146@thanglong.edu.vn'),
('uuid-28','Đỗ Văn Nam','a45147@thanglong.edu.vn'),
('uuid-29','Nguyễn Thị Hồng Nhung','a45148@thanglong.edu.vn'),
('uuid-30','Trần Gia Bảo','a45149@thanglong.edu.vn');

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
('CNTT','Công nghệ thông tin'),
('KTQL','Kinh tế - Quản lý'),
('KHSK','Khoa học sức khỏe'),
('KNN','Ngoại ngữ'),
('XHNV','Khoa học xã hội và nhân văn'),
('KDL','Du lịch'),
('KTT','Truyền thông đa phương tiện'),
('KAN','Âm nhạc ứng dụng'),
('PDT','Phòng đào tạo');


-- departments
INSERT INTO departments (faculty_id, department_code, department_name) VALUES
(1,'TT','Toán Tin'),
(1,'AI','Trí tuệ nhân tạo'),
(2,'KT','Kế toán'),
(2,'KDMK','Quản trị Kinh doanh & Marketing'),
(4,'HQ','Ngôn ngữ Hàn Quốc'),
(4,'TQ','Ngôn ngữ Trung Quốc'),
(2,'LGC','Logistics và Quản lý Chuỗi cung ứng'),
(4,'TA','Ngôn ngữ Anh'),
(3,'GDT','Giáo dục Thể chất'),
(2,'LE','Luật Kinh tế'),
(4, 'GF', 'Ngôn ngữ Pháp');

-- majors
INSERT INTO majors (major_code, major_name, faculty_id) VALUES
('TA','Trí tuệ nhân tạo',1),
('TI','Khoa học máy tính',1),
('TE','Mạng máy tính và truyền thông dữ liệu',1),
('TT','Hệ thống thông tin',1),
('IT','Công nghệ thông tin',1),
('EC','Thương mại điện tử',2),
('IE','Kinh tế quốc tế',2),
('EL','Luật kinh tế',2),
('MK','Marketing',2),
('AC','Kế toán',2),
('FN','Tài chính - Ngân hàng',2),
('LG','Logistics và Quản lí chuỗi cung ứng',2),
('BA','Quản trị kinh doanh',2),
('NU','Điều dưỡng',3),
('EN','Ngôn ngữ Anh',4),
('KR','Ngôn ngữ Hàn Quốc',4),
('CN','Ngôn ngữ Trung Quốc',4),
('JP','Ngôn ngữ Nhật',4),
('VN','Việt Nam học',5),
('TM','Quản trị và Du lịch - Lữ hành',6),
('HM','Quản trị khách sạn',6),
('MM','Truyền thông đa phương tiện',7),
('GD','Thiết kế đồ họa',7),
('VO','Thanh nhạc',8);



-- student_classes
INSERT INTO student_classes (class_code, major_id, start_year) VALUES
-- Trí tuệ nhân tạo
('TA35CL01',1,2022),
('TA35CL02',1,2022),

-- Khoa học máy tính
('TI35CL01',2,2022),
('TI35CL02',2,2022),
('TI35CL03',2,2022),

-- Mạng máy tính và truyền thông dữ liệu
('TE35CL01',3,2022),
('TE35CL02',3,2022),

-- Hệ thống thông tin
('TT35CL01',4,2022),
('TT35CL02',4,2022),

-- Công nghệ thông tin
('IT35CL01',5,2022),
('IT35CL02',5,2022),
('IT35CL03',5,2022),
('IT35CL04',5,2022),

('EC35CL01',6,2022),
('MK35CL01',9,2022),
('BA35CL01',13,2022),

('EN35CL01',15,2022),
('JP35CL01',18,2022);


-- lecturers
INSERT INTO lecturers (oauth_user_id, department_id, full_name, lecturer_code, phone_number, email) VALUES
(4,1,'Nguyễn Đức Minh','CTI064', '0969642001','minhnd@thanglong.edu.vn'),
(5,2,'Hoàng Thu Hà','KDM001','0123456789','ha.hoang@thanglong.edu.vn'),
(6,3,'Đỗ Minh Tuấn','KDM002','0123456789','tuan.do@thanglong.edu.vn'),
(7,1,'Nguyễn Thị Lan Anh','CTI065','0901000001','lananh.nguyen@thanglong.edu.vn'),
(8,1,'Trần Quốc Bảo','CTI066','0901000002','bao.tran@thanglong.edu.vn'),
(9,1,'Lê Hoàng Nam','CTI067','0901000003','nam.le@thanglong.edu.vn'),
(10,1,'Phạm Gia Khánh','CTI068','0901000004','khanh.pham@thanglong.edu.vn');

-- academic_advisors
INSERT INTO academic_advisors (lecturer_id, student_class_id) VALUES
(1,1),
(1,2),
(2,3),
(2,4),
(3,5),
(3,6),
(4,7),
(4,8),
(5,9),
(5,10),
(6,11),
(6,12),
(7,13);

-- students
INSERT INTO students (oauth_user_id, student_class_id, full_name, student_code, gender, date_of_birth, status) VALUES
(1,1,'Lê Việt Hoàng','A45033','NAM','2003-05-10','ACTIVE'),
(2,10,'Nguyễn Ngọc Anh','A45035','NU','2004-08-20','ACTIVE'),
(3,10,'Phùng Thanh Độ','A45044','NAM','2003-02-11','ACTIVE'),
(11,1,'Nguyễn Hoàng Long','A45039','NAM','2004-02-15','ACTIVE'),
(12,1,'Trần Minh Tuấn','A45040','NAM','2004-06-21','ACTIVE'),
(13,10,'Lê Thị Thu Trang','A45041','NU','2004-09-12','ACTIVE'),
(14,2,'Phạm Đức Anh','A45042','NAM','2003-11-05','ACTIVE'),
(15,1,'Hoàng Ngọc Linh','A45043','NU','2004-07-30','ACTIVE'),
(16,1,'Đỗ Quang Trung','A45045','NAM','2003-03-18','ACTIVE'),
(17,4,'Nguyễn Hải Đăng','A45046','NAM','2004-01-25','ACTIVE'),
(18,4,'Trần Thu Phương','A45047','NU','2004-05-14','ACTIVE'),
(19,10,'Lê Quốc Khánh','A45048','NAM','2003-08-09','ACTIVE'),
(20,5,'Phạm Thùy Dương','A45049','NU','2004-10-01','ACTIVE'),
(21,6,'Hoàng Minh Khang','A45050','NAM','2003-12-22','ACTIVE'),
(22,6,'Đỗ Thị Ngọc Anh','A45051','NU','2004-04-11','ACTIVE'),
(23,7,'Nguyễn Văn Hưng','A45052','NAM','2003-09-17','ACTIVE'),
(24,7,'Trần Đức Thành','A45053','NAM','2004-02-28','ACTIVE'),
(25,8,'Lê Thanh Tùng','A45054','NAM','2003-06-06','ACTIVE'),
(26,8,'Phạm Nhật Quang','A45055','NAM','2004-08-19','ACTIVE'),
(27,9,'Hoàng Thị Mai','A45056','NU','2004-03-27','ACTIVE'),
(28,9,'Đỗ Văn Nam','A45057','NAM','2003-07-13','ACTIVE'),
(29,10,'Nguyễn Thị Hồng Nhung','A45058','NU','2004-11-02','ACTIVE'),
(30,10,'Trần Gia Bảo','A45059','NAM','2003-10-10','ACTIVE');


-- student_contacts
INSERT INTO student_contacts (student_id, phone_number, address, email_personal) VALUES
(1,'0967000008','Hà Nội','i1@gmail.com'),
(2,'0900000009','Hải Phòng','j1@gmail.com'),
(3,'0900000010','Hà Nội','k1@gmail.com'),
(4,'0900000011','Nam Định','l1@gmail.com'),
(5,'0900000012','Thanh Hóa','m1@gmail.com'),
(6,'0900000013','Huế','n1@gmail.com'),
(7,'0900000014','Hồ Chí Minh','o1@gmail.com'),
(8,'0900000015','Hà Nội','p1@gmail.com'),
(9,'0900000016','Hải Phòng','q1@gmail.com'),
(10,'0900000017','Hà Nội','r1@gmail.com'),
(11,'0900000018','Nam Định','s1@gmail.com'),
(12,'0900000019','Đà Nẵng','t1@gmail.com'),
(13,'0900000020','Huế','u1@gmail.com'),
(14,'0900000021','Hồ Chí Minh','v1@gmail.com'),
(15,'0900000022','Hà Nội','w1@gmail.com'),
(16,'0900000023','Hải Phòng','x1@gmail.com'),
(17,'0900000024','Hà Nội','y1@gmail.com'),
(18,'0900000025','Nam Định','z1@gmail.com'),
(19,'0900000026','Đà Nẵng','aa1@gmail.com'),
(20,'0900000027','Huế','bb1@gmail.com'),
(21,'0900000028','Hồ Chí Minh','cc1@gmail.com'),
(22,'0900000029','Hà Nội','dd1@gmail.com');

-- emergency_contacts
INSERT INTO emergency_contacts (student_id, full_name, phone_number, relationship, address) VALUES
(1,'Nguyễn Văn Hùng','0911111111','Cha','Hà Nội'),
(2,'Trần Thị Mai','0922222222','Mẹ','Hà Nội'),
(3,'Phạm Văn Dũng','0933333333','Cha','Hà Nội'),
(4,'Lê Thị Hoa','0944444444','Mẹ','Hà Nội'),
(5,'Hoàng Văn Sơn','0955555555','Cha','Đà Nẵng'),
(6,'Đỗ Thị Lan','0966666666','Mẹ','Huế'),
(7,'Nguyễn Văn Bình','0977777777','Cha','TP.HCM'),
(8,'Trần Văn Phúc','0980000001','Cha','Hải Phòng'),
(9,'Lê Thị Hạnh','0980000002','Mẹ','Nam Định'),
(10,'Phạm Văn Khánh','0980000003','Cha','Hà Nội'),
(11,'Hoàng Thị Thu','0980000004','Mẹ','Đà Nẵng'),
(12,'Đỗ Văn Long','0980000005','Cha','Huế'),
(13,'Nguyễn Thị Tuyết','0980000006','Mẹ','Hà Nội'),
(14,'Trần Văn Nam','0980000007','Cha','TP.HCM'),
(15,'Lê Thị Ngọc','0980000008','Mẹ','Hải Phòng'),
(16,'Phạm Văn Hòa','0980000009','Cha','Nam Định'),
(17,'Hoàng Thị Dung','0980000010','Mẹ','Hà Nội'),
(18,'Đỗ Văn Tài','0980000011','Cha','Đà Nẵng'),
(19,'Nguyễn Thị Hồng','0980000012','Mẹ','Huế'),
(20,'Trần Văn Quang','0980000013','Cha','TP.HCM'),
(21,'Lê Thị Thanh','0980000014','Mẹ','Hà Nội'),
(22,'Phạm Văn Cường','0980000015','Cha','Hải Phòng');

-- identity_cards
INSERT INTO identity_cards (student_id, card_number, card_type, issued_date, issued_place) VALUES
(1,'100000000008','CCCD','2021-08-08','Hải Phòng'),
(2,'100000000009','CCCD','2022-09-09','Nam Định'),
(3,'100000000010','CCCD','2021-10-10','Hà Nội'),
(4,'100000000011','CCCD','2022-11-11','Đà Nẵng'),
(5,'100000000012','CCCD','2023-12-12','Huế'),
(6,'100000000013','CCCD','2021-01-13','Hà Nội'),
(7,'100000000014','CCCD','2022-02-14','TP.HCM'),
(8,'100000000015','CCCD','2023-03-15','Hải Phòng'),
(9,'100000000016','CCCD','2021-04-16','Nam Định'),
(10,'100000000017','CCCD','2022-05-17','Hà Nội'),
(11,'100000000018','CCCD','2023-06-18','Đà Nẵng'),
(12,'100000000019','CCCD','2021-07-19','Huế'),
(13,'100000000020','CCCD','2022-08-20','TP.HCM'),
(14,'100000000021','CCCD','2023-09-21','Hà Nội'),
(15,'100000000022','CCCD','2021-10-22','Hải Phòng'),
(16,'100000000023','CCCD','2022-11-23','Nam Định'),
(17,'100000000024','CCCD','2023-12-24','Hà Nội'),
(18,'100000000025','CCCD','2021-01-25','Đà Nẵng'),
(19,'100000000026','CCCD','2022-02-26','Huế'),
(20,'100000000027','CCCD','2023-03-27','TP.HCM'),
(21,'100000000028','CCCD','2021-04-28','Hà Nội'),
(22,'100000000029','CCCD','2022-05-29','Hải Phòng');

-- health_insurances
INSERT INTO health_insurances (student_id, insurance_number, provider, valid_from, valid_to, registered_hospital) VALUES
(1,'BHYT008','Bao Minh','2024-02-01','2026-02-01','BV Hải Phòng'),
(2,'BHYT009','Bao Viet','2025-04-01','2027-04-01','BV Nam Định'),
(3,'BHYT010','Bao Minh','2024-07-01','2026-07-01','BV Bạch Mai'),
(4,'BHYT011','Bao Viet','2025-08-01','2027-08-01','BV Đà Nẵng'),
(5,'BHYT012','Bao Minh','2024-10-01','2026-10-01','BV Huế'),
(6,'BHYT013','Bao Viet','2025-11-01','2027-11-01','BV 108'),
(7,'BHYT014','Bao Minh','2024-03-01','2026-03-01','BV Chợ Rẫy'),
(8,'BHYT015','Bao Viet','2025-06-01','2027-06-01','BV Việt Đức'),
(9,'BHYT016','Bao Minh','2024-09-15','2026-09-15','BV Thanh Nhàn'),
(10,'BHYT017','Bao Viet','2025-12-01','2027-12-01','BV Bạch Mai'),
(11,'BHYT018','Bao Minh','2024-05-01','2026-05-01','BV Đà Nẵng'),
(12,'BHYT019','Bao Viet','2025-07-01','2027-07-01','BV Huế'),
(13,'BHYT020','Bao Minh','2024-08-01','2026-08-01','BV Chợ Rẫy'),
(14,'BHYT021','Bao Viet','2025-09-01','2027-09-01','BV 108'),
(15,'BHYT022','Bao Minh','2024-11-01','2026-11-01','BV Hải Phòng'),
(16,'BHYT023','Bao Viet','2025-01-15','2027-01-15','BV Nam Định'),
(17,'BHYT024','Bao Minh','2024-04-01','2026-04-01','BV Việt Đức'),
(18,'BHYT025','Bao Viet','2025-02-01','2027-02-01','BV Đà Nẵng'),
(19,'BHYT026','Bao Minh','2024-06-15','2026-06-15','BV Huế'),
(20,'BHYT027','Bao Viet','2025-03-15','2027-03-15','BV Chợ Rẫy'),
(21,'BHYT028','Bao Minh','2024-12-15','2026-12-15','BV Bạch Mai'),
(22,'BHYT029','Bao Viet','2025-10-01','2027-10-01','BV 108');

-- semesters
INSERT INTO semesters (semester_name, semester_code, academic_years, semester_number, start_date, end_date)
VALUES
('Học kỳ 1 2024-2025','HK1-2024-2025','2024-2025',1,'2024-09-03','2024-12-15'),
('Học kỳ 2 2024-2025','HK2-2024-2025','2024-2025',2,'2025-01-02','2025-04-27'),
('Học kỳ tăng cường 2024-2025','HKTC-2024-2025','2024-2025',3,'2025-05-05','2025-08-24'),
('Học kỳ 1 2025-2026','HK1-2025-2026','2025-2026',1,'2025-09-08','2025-12-28'),
('Học kỳ 2 2025-2026','HK2-2025-2026','2025-2026',2,'2026-01-05','2026-04-26'),
('Học kỳ tăng cường 2025-2026','HKTC-2025-2026','2025-2026',3,'2026-05-04','2026-08-23');

-- study_programs
INSERT INTO study_programs (major_id, study_program_code, study_program_name, total_credits, start_year) VALUES
(1,'DHCQK35TA','Trí tuệ nhân tạo - Khóa 35',130,2022),
(2,'DHCQK35TI','Khoa học máy tính - Khóa 35',130,2022),
(3,'DHCQK35TE','Mạng máy tính và truyền thông dữ liệu - Khóa 35',130,2022),
(4,'DHCQK35TT','Hệ thống thông tin - Khóa 35',130,2022),
(5,'DHCQK35IT','Công nghệ thông tin - Khóa 35',130,2022),

(6,'DHCQK35EC','Thương mại điện tử - Khóa 35',125,2022),
(7,'DHCQK35IE','Kinh tế quốc tế - Khóa 35',125,2022),
(8,'DHCQK35EL','Luật kinh tế - Khóa 35',125,2022),
(9,'DHCQK35MK','Marketing - Khóa 35',125,2022),
(10,'DHCQK35AC','Kế toán - Khóa 35',125,2022),
(11,'DHCQK35FN','Tài chính - Ngân hàng - Khóa 35',125,2022),
(12,'DHCQK35LG','Logistics và Quản lí chuỗi cung ứng - Khóa 35',125,2022),
(13,'DHCQK35BA','Quản trị kinh doanh - Khóa 35',125,2022),

(14,'DHCQK35NU','Điều dưỡng - Khóa 35',140,2022),

(15,'DHCQK35EN','Ngôn ngữ Anh - Khóa 35',120,2022),
(16,'DHCQK35KR','Ngôn ngữ Hàn Quốc - Khóa 35',120,2022),
(17,'DHCQK35CN','Ngôn ngữ Trung Quốc - Khóa 35',120,2022),
(18,'DHCQK35JP','Ngôn ngữ Nhật - Khóa 35',120,2022),

(19,'DHCQK35VN','Việt Nam học - Khóa 35',120,2022),

(20,'DHCQK35TM','Quản trị và Du lịch - Lữ hành - Khóa 35',120,2022),
(21,'DHCQK35HM','Quản trị khách sạn - Khóa 35',120,2022),

(22,'DHCQK35MM','Truyền thông đa phương tiện - Khóa 35',120,2022),
(23,'DHCQK35GD','Thiết kế đồ họa - Khóa 35',120,2022),

(24,'DHCQK35VO','Thanh nhạc - Khóa 35',110,2022);



-- student_majors
INSERT INTO student_majors (student_id, major_id, study_program_id, start_year, end_year) VALUES
(1,2,2,2022,2026),
(2,5,5,2022,2026),
(3,5,5,2022,2026),
(4,1,1,2022,2026),
(5,1,1,2022,2026),
(6,5,5,2022,2026),
(7,2,2,2022,2026),
(8,1,1,2022,2026),
(9,1,1,2022,2026),
(10,4,4,2022,2026),
(11,4,4,2022,2026),
(12,5,5,2022,2026),
(13,5,5,2022,2026),
(14,5,5,2022,2026),
(15,1,1,2022,2026),
(16,1,1,2022,2026),
(17,2,2,2022,2026),
(18,2,2,2022,2026),
(19,3,3,2022,2026),
(20,3,3,2022,2026),
(21,4,4,2022,2026),
(22,4,4,2022,2026),
(23,5,5,2022,2026);

-- academic_infos
INSERT INTO academic_infos (student_major_id, cohort, position) VALUES
(1,'K35','Lớp trưởng'),
(2,'K35','Sinh viên'),
(3,'K35','Lớp phó'),
(4,'K35','Sinh viên'),
(5,'K35','Sinh viên'),
(6,'K35','Sinh viên'),
(7,'K35','Sinh viên'),
(8,'K35','Sinh viên'),
(9,'K35','Sinh viên'),
(10,'K35','Sinh viên'),
(11,'K35','Sinh viên'),
(12,'K35','Sinh viên'),
(13,'K35','Sinh viên'),
(14,'K35','Sinh viên'),
(15,'K35','Sinh viên'),
(16,'K35','Sinh viên'),
(17,'K35','Sinh viên'),
(18,'K35','Sinh viên'),
(19,'K35','Sinh viên'),
(20,'K35','Sinh viên'),
(21,'K35','Sinh viên'),
(22,'K35','Sinh viên'),
(23,'K35','Sinh viên');

-- subjects
INSERT INTO subjects (faculty_id, department_id, subject_code, subject_name, credits, coefficient, lecture_hours, practice_hours)
VALUES
(9,null,'AD205','Kỹ năng soạn thảo văn bản (MS Office)',3,1.0,45,null),
(6,null,'AD206','Ẩm thực Việt Nam',3,1.0,30,30),
(9,null,'AD207','Kỹ năng soạn thảo văn bản (MS Open)',3,1.0,45,null),
(9,null,'AD212','Phương pháp hùng biện và các thủ thuật tranh biện',3,1.0,45,null),
(9,null,'AD213','Hát - Nhạc',3,1.0,45,null),
(9,null,'AD214','Nâng cao chất lượng giọng hát',3,1.0,45,null),
(5,null,'AD215','Kỹ năng sống',3,1.0,45,null),
(4 ,11,'GF101','Tiếng Pháp 1',2,1.0,54,null),
(4 ,11,'GF102','Tiếng Pháp 2',2,1.0,54,null),
(4 ,9,'PG102','GDTC: Thể dục cổ truyền cơ bản',1,1.0,25,null),
(1 ,null,'CS100','Tin đại cương',2,1.5,18,24),
(9 ,null,'CS102','Tin học văn phòng',2,1.5,30,null),
(2 ,null,'EC102','Nhập môn kinh tế học',2,1.2,30,null),
(4 ,8,'GE111','Tiếng Anh sơ cấp 1',2,1.2,54,null),
(1 ,1,'MA101','Logic, suy luận toán học và kỹ thuật đếm',3,1.2,27,36),
(9 ,null,'ML113','Triết học Mác - Lênin',3,1.2,45,null),
(9 ,null,'NA151','Khoa học môi trường',2,1.2,30,null),
(5 ,null,'SH131','Pháp luật đại cương',2,1.2,30,null),
(5 ,null,'VL101','Tiếng Việt thực hành',2,1.2,30,null),
(1 ,null,'CS121','Lập trình cơ sở 1',3,1.5,27,36),
(1 ,null,'CS212','Kiến trúc máy tính',3,1.5,45,null),
(1 ,null,'CS213','Cấu trúc dữ liệu và giải thuật',3,1.5,27,36),
(4 ,8,'GE112','Tiếng Anh sơ cấp 2',2,1.2,54,null),
(1 ,1,'MA120','Đại số tuyến tính',3,1.2,27,36),
(9 ,null,'ML114','Kinh tế chính trị Mác - Lênin',2,1.2,30,null),
(4 ,8,'GE121','Tiếng Anh sơ trung cấp 1',2,1.2,54,null),
(1 ,1,'MA110','Giải tích 1',3,1.2,27,36),
(1 ,1,'MA111','Giải tích 2',3,1.2,27,36),
(1 ,null,'MI201','Toán rời rạc',3,1.2,45,null),
(9,null,'ML115','Chủ nghĩa xã hội khoa học',2, 1.2,30,null),
(1,null,'CS111','Kỹ thuật số',2, 1.5,18,24),
(1 ,1,'CF213','Cấu trúc dữ liệu và giải thuật',4,1.6,45,36),
(4 ,8,'GE222','Tiếng Anh sơ trung cấp 2',2,1.2,54,null),
(1 ,null,'IS222','Cơ sở dữ liệu',3,1.2,45,null),
(1 ,1,'MA239','Xác suất thống kê',4,1.6,45,27),
(9 ,null,'ML202','Tư tưởng Hồ Chí Minh',2,1.2,30,null),
(1 ,null,'CS315','Nguyên lý hệ điều hành',3,1.2,45,null),
(1 ,null,'IS322','Hệ quản trị cơ sở dữ liệu',3,1.2,45,null),
(1,null,'IS314','Hệ thống thông tin',3, 1.2,45,null),
(1,null,'CS122','Lập trình hướng đối tượng',3,1.5,27,36);

-- study_program_subjects
INSERT INTO study_program_subjects (study_program_id, subject_id, semester_id, is_required)
VALUES
-- IT
(5,10,1,true),
(5,11,1,true),
(5,12,1,true),
(5,13,1,true),
(5,14,1,true),
(5,15,1,true),
(5,16,2,true),
(5,17,2,true),
(5,18,2,true),
(5,19,2,true),
(5,20,2,true),
(5,21,2,true),
(5,22,2,true),
(5,23,2,true),
(5,24,2,true),
(5,25,4,true),
(5,26,4,true),
(5,27,4,true),
(5,28,4,true),
(5,29,4,true),
(5,30,4,true),
(5,31,4,true),
(5,32,4,true),
(5,33,4,true),
(5,34,5,true),
(5,35,5,true),
(5,36,5,true),
(5,37,5,true),
(5,38,5,true),
(5,39,5,true),
(5,40,5,true),
(5,1,5,false),
(5,2,5,false),
(5,3,5,false),
(5,4,5,false),
(5,5,5,false),
(5,6,5,false),
(5,7,5,false),
(5,8,5,false),
(5,9,5,false),

-- TI
(2,10,1,true),
(2,11,1,true),
(2,12,1,true),
(2,13,1,true),
(2,14,1,true),
(2,15,1,true),
(2,16,2,true),
(2,17,2,true),
(2,18,2,true),
(2,19,2,true),
(2,20,2,true),
(2,21,2,true),
(2,22,2,true),
(2,23,2,true),
(2,24,2,true),
(2,25,4,true),
(2,26,4,true),
(2,27,4,true),
(2,28,4,true),
(2,29,4,true),
(2,30,4,true),
(2,31,4,true),
(2,32,4,true),
(2,33,4,true),
(2,34,5,true),
(2,35,5,true),
(2,36,5,true),
(2,37,5,true),
(2,38,5,true),
(2,39,5,true),
(2,40,5,true),
(2,1,5,false),
(2,2,5,false),
(2,3,5,false),
(2,4,5,false),
(2,5,5,false),
(2,6,5,false),
(2,7,5,false),
(2,8,5,false),
(2,9,5,false);

-- course_classes
INSERT INTO course_classes (lecturer_id, subject_id, semester_id, class_code, class_name, capacity)
VALUES
(1,32,5,'252CF21301','Cấu trúc dữ liệu và giải thuật 01',40),
(1,1,5,'252AD21501','Kỹ năng sống 01',40),
(2,2,5,'252GF10101','Tiếng Pháp 1 - 01',35),
(2,3,6,'252GF10201','Tiếng Pháp 2 - 01',35),
(3,4,5,'252PG10201','GDTC cổ truyền 01',50),

(1,5,5,'252CS10001','Tin đại cương 01',45),
(4,6,5,'252CS10201','Tin học văn phòng 01',45),
(5,7,5,'252EC10201','Nhập môn kinh tế học 01',50),
(6,8,5,'252GE11101','Tiếng Anh sơ cấp 1 - 01',40),

(1,9,5,'252MA10101','Logic và suy luận toán học 01',45),
(7,10,5,'252ML11301','Triết học Mác - Lênin 01',50),
(7,11,1,'252NA15101','Khoa học môi trường 01',40),
(4,12,1,'252SH13101','Pháp luật đại cương 01',45),
(4,13,1,'252VL10101','Tiếng Việt thực hành 01',45),

(1,14,1,'252CS12101','Lập trình cơ sở 1 - 01',45),
(1,15,1,'252CS21201','Kiến trúc máy tính 01',40),
(1,16,1,'252CS21301','Cấu trúc dữ liệu và giải thuật 01',40),

(6,17,2,'252GE11201','Tiếng Anh sơ cấp 2 - 01',40),
(1,18,2,'252MA12001','Đại số tuyến tính 01',45),
(7,19,2,'252ML11401','Kinh tế chính trị M-L 01',50),

(6,20,2,'252GE12101','Tiếng Anh sơ trung cấp 1 - 01',40),
(1,21,2,'252MA11001','Giải tích 1 - 01',45),
(1,22,2,'252MA11101','Giải tích 2 - 01',45),

(1,23,2,'252MI20101','Toán rời rạc 01',45),
(7,24,2,'252ML11501','CNXH khoa học 01',50),
(1,25,2,'252CS11101','Kỹ thuật số 01',40),

(1,26,4,'252CF21301','CTDL & GT nâng cao 01',40),
(6,33,4,'252GE22201','Tiếng Anh sơ trung cấp 2 - 01',40),

(1,28,4,'252IS22201','Cơ sở dữ liệu 01',45),
(1,29,4,'252MA23901','Xác suất thống kê 01',45),
(7,30,4,'252ML20201','Tư tưởng HCM 01',50),

(1,31,4,'252CS31501','Hệ điều hành 01',40),
(1,32,5,'252IS32201','Hệ QTCSDL 01',40),
(1,33,5,'252IS31401','Hệ thống thông tin 01',40),
(1,34,5,'252CS12201','Lập trình hướng đối tượng 01',45);

-- student_course_classes
INSERT INTO student_course_classes 
(student_id, course_class_id, subject_id, semester_id, status) VALUES
(1,1,1,5,'ENROLLED'),
(1,5,5,5,'ENROLLED'),
(1,14,14,5,'ENROLLED'),

(2,1,1,1,'ENROLLED'),
(2,6,6,1,'ENROLLED'),
(2,14,14,1,'ENROLLED'),

(3,5,5,1,'ENROLLED'),
(3,14,14,1,'ENROLLED'),
(3,16,16,1,'ENROLLED'),

(4,5,5,2,'ENROLLED'),
(4,9,9,2,'ENROLLED'),
(4,14,14,2,'ENROLLED'),

(5,5,5,2,'ENROLLED'),
(5,14,14,2,'ENROLLED'),
(5,15,15,2,'ENROLLED'),

(6,6,6,2,'ENROLLED'),
(6,7,7,2,'ENROLLED'),
(6,14,14,3,'ENROLLED'),

(7,5,5,3,'ENROLLED'),
(7,14,14,3,'ENROLLED'),
(7,18,18,3,'ENROLLED'),

(8,5,5,4,'ENROLLED'),
(8,9,9,4,'ENROLLED'),
(8,14,14,4,'ENROLLED'),

(9,5,5,4,'ENROLLED'),
(9,14,14,4,'ENROLLED'),
(9,21,21,4,'ENROLLED'),

(10,5,5,4,'ENROLLED'),
(10,9,9,4,'ENROLLED'),
(10,14,14,4,'ENROLLED'),

(11,5,5,4,'ENROLLED'),
(11,14,14,4,'ENROLLED'),
(11,23,23,4,'ENROLLED'),

(12,5,5,5,'ENROLLED'),
(12,14,14,5,'ENROLLED'),
(12,25,25,5,'ENROLLED'),

(13,7,7,5,'ENROLLED'),
(13,10,10,5,'ENROLLED'),
(13,12,12,5,'ENROLLED'),

(14,5,5,5,'ENROLLED'),
(14,14,14,5,'ENROLLED'),
(14,28,28,5,'ENROLLED'),

(15,5,5,5,'ENROLLED'),
(15,14,14,5,'ENROLLED'),
(15,31,31,5,'ENROLLED'),

(16,5,5,5,'ENROLLED'),
(16,14,14,5,'ENROLLED'),
(16,32,32,5,'ENROLLED');

-- class_schedules
INSERT INTO class_schedules
(course_class_id, day_of_week, start_period, end_period, start_time, end_time, room)
VALUES
-- class 2
(2,3,4,6,'10:10','12:40','A102'),
(2,5,4,6,'10:10','12:40','A102'),

-- class 3
(3,2,7,9,'13:00','15:30','B201'),
(3,6,7,9,'13:00','15:30','B201'),

-- class 4
(4,3,1,3,'07:30','10:00','C101'),
(4,5,1,3,'07:30','10:00','C101'),

-- class 5
(5,2,4,6,'10:10','12:40','A201'),
(5,4,4,6,'10:10','12:40','A201'),

-- class 6
(6,3,7,9,'13:00','15:30','B101'),
(6,6,7,9,'13:00','15:30','B101'),

-- class 7
(7,2,1,3,'07:30','10:00','D101'),
(7,5,1,3,'07:30','10:00','D101'),

-- class 8
(8,3,4,6,'10:10','12:40','C202'),
(8,6,4,6,'10:10','12:40','C202'),

-- class 9
(9,2,7,9,'13:00','15:30','A301'),
(9,4,7,9,'13:00','15:30','A301'),

-- class 10
(10,3,1,3,'07:30','10:00','B202'),
(10,5,1,3,'07:30','10:00','B202');

-- exam_schedules
INSERT INTO exam_schedules
(subject_id, semester_id, exam_date, start_time, end_time, exam_room, exam_format, exam_location, exam_type)
VALUES
(6,1,'2026-01-17','08:00','10:00','C101','OFFLINE','Cơ sở 1','Final'),
(7,1,'2026-01-18','13:00','15:00','C102','OFFLINE','Cơ sở 1','Final'),
(8,1,'2026-01-20','09:00','11:00','D201','OFFLINE','Cơ sở 1','Midterm'),
(9,1,'2026-01-22','08:00','10:00','A103','OFFLINE','Cơ sở 1','Final'),
(10,1,'2026-01-24','13:00','15:00','B203','OFFLINE','Cơ sở 1','Final'),

(11,2,'2026-06-14','08:00','10:00','A301','OFFLINE','Cơ sở 1','Final'),
(12,2,'2026-06-15','13:00','15:00','A302','OFFLINE','Cơ sở 1','Final'),
(13,2,'2026-06-16','09:00','11:00','B301','OFFLINE','Cơ sở 1','Midterm'),
(14,2,'2026-06-18','08:00','10:00','C301','OFFLINE','Cơ sở 1','Final'),
(15,2,'2026-06-20','13:00','15:00','C302','OFFLINE','Cơ sở 1','Final'),

(16,3,'2026-08-05','08:00','10:00','D101','OFFLINE','Cơ sở 1','Final'),
(17,3,'2026-08-06','13:00','15:00','D102','OFFLINE','Cơ sở 1','Final'),
(18,3,'2026-08-07','09:00','11:00','A104','OFFLINE','Cơ sở 1','Midterm'),
(19,3,'2026-08-09','08:00','10:00','B204','OFFLINE','Cơ sở 1','Final'),
(20,3,'2026-08-10','13:00','15:00','C103','OFFLINE','Cơ sở 1','Final');

-- student_exam_registrations
INSERT INTO student_exam_registrations
(student_id, exam_schedule_id, exam_attempt, attendance_status)
VALUES
(1,1,1,'UPCOMING'),
(1,2,1,'UPCOMING'),
(2,1,1,'UPCOMING'),
(2,2,1,'UPCOMING'),
(3,3,1,'UPCOMING');

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
(1,2,1,6,6,3.2,'B',80,8.0,'B',7.5,'B'),
(2,5,1,3,3,3.5,'A',85,8.5,'A',8.0,'A'),
(3,5,1,3,3,3.0,'B',75,7.0,'B',7.0,'B'),
(4,1,1,3,3,2.8,'C',70,6.5,'C',6.0,'C');

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

-- notification_templates
INSERT INTO notification_templates (code, name, content) VALUES
('GENERAL', 'Thong bao chung', '{{content}}'),
('EXAM', 'Thong bao lich thi', 'Lich thi: {{content}}'),
('FEE', 'Nhac nop hoc phi', 'Sinh vien can nop hoc phi truoc {{deadline}}'),
('SCHOLARSHIP', 'Thong bao hoc bong', '{{content}}'),
('ACADEMIC_WARNING', 'Canh bao hoc vu', '{{content}}'),
('DEFENSE', 'Thong bao bao ve do an', '{{content}}');

-- notifications
INSERT INTO notifications
(title, content, created_by, target_type, deadline, is_important, reference_type)
VALUES

-- GLOBAL
('Thong bao he thong','He thong se bao tri vao 23:00 toi nay','SYSTEM','GLOBAL',NULL, false, null),
('Cap nhat cong thong tin','Da cap nhat giao dien moi','SYSTEM','GLOBAL',NULL, false, null),
('Thong bao nghi le','Sinh vien nghi le quoc khanh','SYSTEM','GLOBAL',NULL, false, null),
('Lich thi HK1','Lich thi da duoc cap nhat tren portal','SYSTEM','GLOBAL','2024-01-05', true, 'EXAM_SCHEDULE'),
('Ket qua hoc bong','Danh sach hoc bong HK1 da duoc cong bo','SYSTEM','GLOBAL',NULL, false, null),

-- STUDENT_CLASS
('Canh bao hoc vu','Ket qua hoc tap duoi muc yeu cau','SYSTEM','STUDENT_CLASS','2024-03-01', true, null),
('Canh bao hoc vu lan 2','Sinh vien can gap co van hoc tap','SYSTEM','STUDENT_CLASS','2024-03-10', true, null),
('Thong bao rieng','Sinh vien duoc chon tham gia workshop','SYSTEM','STUDENT_CLASS','2024-03-10', false, null),
('Thong bao rieng','Sinh vien duoc cap tai khoan lab','SYSTEM','STUDENT_CLASS','2024-03-10', false, null),
('Thong bao rieng','Sinh vien cap nhat thong tin ca nhan','SYSTEM','STUDENT_CLASS','2024-03-10', false, null),

-- COURSE_CLASS
('Thong bao mon hoc','Lop lap trinh web thay doi phong hoc','LECTURER','COURSE_CLASS','2024-03-10', false, null),
('Thong bao mon hoc','Buoi hoc toi se hoc online','LECTURER','COURSE_CLASS','2024-03-10', false, null),
('Thong bao mon hoc','Deadline project duoc gia han','LECTURER','COURSE_CLASS','2024-04-10', false, null),

-- FACULTY
('Thong bao khoa CNTT','Sinh vien tham gia hoi thao AI','FACULTY','FACULTY',NULL, false, null),
('Thong bao khoa CNTT','Cuoc thi lap trinh sap dien ra','FACULTY','FACULTY',NULL, false, null),
('Thong bao khoa CNTT','Mo dang ky CLB AI','FACULTY','FACULTY',NULL, false, null),
('Thong bao khoa CNTT','Workshop Cloud Computing','FACULTY','FACULTY',NULL, false, null),
('Thong bao khoa CNTT','Sinh vien dang ky thuc tap he','FACULTY','FACULTY',NULL, false, null);

-- notification_targets
INSERT INTO notification_targets (notification_id, target_id) VALUES

-- STUDENT_CLASS (id 6 -> 10)
(6, 2),
(7, 2),
(8, 3),
(9, 4),
(10, 5),

-- COURSE_CLASS (id 11 -> 13)
(11, 10),
(12, 10),
(13, 10),

-- FACULTY (id 14 -> 18)
(14, 1),
(15, 1),
(16, 1),
(17, 1),
(18, 1);

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
(base_price_per_credit, academic_year, cohort)
VALUES
(500000, '2022-2023', 2022),
(520000, '2023-2024', 2022),
(480000, '2024-2025', 2022),
(700000, '2025-2026', 2022),
(750000, '2026-2027', 2022);

-- tuition_invoices
INSERT INTO tuition_invoices
(student_id, semester_id, due_date, total_amount, final_amount, status, created_at, updated_at)
VALUES
-- đã thanh toán
(1, 1, '2026-01-10', 4500000, 4500000, 'UNPAID', '2025-12-01', '2025-12-05'),

-- chưa thanh toán (quá hạn)
(2, 1, '2026-01-05', 4200000, 4200000, 'OVERDUE', '2025-12-01', '2026-01-06'),

-- chưa thanh toán (còn hạn)
(1, 1, '2026-01-15', 4300000, 4300000, 'UNPAID', '2025-12-01', '2025-12-01'),

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
(3, 4300000, 'VNPAY', 'TXN_003_PENDING', 'PENDING', '2025-12-09', '2025-12-09'),

-- invoice 5: thanh toán thành công (future semester)
(5, 5200000, 'VNPAY', 'TXN_005_SUCCESS', 'SUCCESS', '2026-03-10', '2026-03-10'),

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
(subject_id, min_subjects_required, description)
VALUES

(6, 1, 'Cần học học phần mã AD213'),
(9, 1, 'Cần học học phần mã GF101'),
(20, 1, 'Cần học học phần mã CS100'),
(21, 1, 'Cần học học phần mã CS121'),
(22, 1, 'Cần học học phần mã CS122'),
(23, 1, 'Cần học học phần mã GE111'),
(24, 1, 'Cần học học phần mã MA101');

INSERT INTO subject_prerequisite_group_items
(group_id, prerequisite_subject_id)
VALUES

(1, 5),
(2, 8),
(3, 11),
(4, 20),
(5,40),
(6, 14),
(7, 15);

INSERT INTO subject_enrollment_conditions
(subject_id, condition_type, condition_value, condition_operator, description, created_at, updated_at)
VALUES

(5, 'GPA', 2.50, '>=', 'Yeu cau GPA toi thieu 2.5', '2025-12-01', '2025-12-01'),

(5, 'TOTAL_CREDITS', 30, '>=', 'Phai tich luy it nhat 30 tin chi', '2025-12-01', '2025-12-01'),

(4, 'GPA', 2.00, '>', 'Yeu cau GPA > 2.0', '2025-12-01', '2025-12-01'),

(6, 'TOTAL_CREDITS', 20, '>=', 'Yeu cau >= 20 tin chi', '2025-12-01', '2025-12-01'),

(6, 'GPA', 2, '=', 'Yeu cau GPA toi thieu 2', '2025-12-01', '2025-12-01');

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