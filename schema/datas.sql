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
  notification_targets,
  feedback_category
  
RESTART IDENTITY CASCADE;

-- oauth_users
INSERT INTO oauth_users (id, user_uuid, display_name, email)
VALUES
(1,'1deb00a9-835c-4ab7-a50f-57c12a56c7bd', 'Lê Việt Hoàng', 'nhokthanh3211@gmail.com'),
(2,'a65d03d4-6a2a-426f-963d-8dca24399b83', 'Nguyễn Ngọc Anh', 'a45045@thanglong.edu.vn'),
(3,'4a43cbd4-f3c6-4f5e-8db3-5b3aa1ef7369', 'Phùng Thanh Độ', 'a45044@thanglong.edu.vn'),
(4,'uuid-4','Phạm Quang Huy','a45123@thanglong.edu.vn'),
(5,'uuid-5','Hoàng Thu Hà','a45124@thanglong.edu.vn'),
(6,'uuid-6','Đỗ Minh Tuấn','a45125@thanglong.edu.vn'),
(7,'uuid-7','Nguyễn Thị Lan Anh','a45126@thanglong.edu.vn'),
(8,'uuid-8','Trần Quốc Bảo','a45127@thanglong.edu.vn'),
(9,'uuid-9','Lê Hoàng Nam','a45128@thanglong.edu.vn'),
(10,'uuid-10','Phạm Gia Khánh','a45129@thanglong.edu.vn'),
(11,'uuid-11','Nguyễn Hoàng Long','a45130@thanglong.edu.vn'),
(12,'uuid-12','Trần Minh Tuấn','a45131@thanglong.edu.vn'),
(13,'uuid-13','Lê Thị Thu Trang','a45132@thanglong.edu.vn'),
(14,'uuid-14','Phạm Đức Anh','a45133@thanglong.edu.vn'),
(15,'uuid-15','Hoàng Ngọc Linh','a45134@thanglong.edu.vn'),
(16,'uuid-16','Đỗ Quang Trung','a45135@thanglong.edu.vn'),
(17,'uuid-17','Nguyễn Hải Đăng','a45136@thanglong.edu.vn'),
(18,'uuid-18','Trần Thu Phương','a45137@thanglong.edu.vn'),
(19,'uuid-19','Lê Quốc Khánh','a45138@thanglong.edu.vn'),
(20,'uuid-20','Phạm Thùy Dương','a45139@thanglong.edu.vn'),
(21,'uuid-21','Hoàng Minh Khang','a45140@thanglong.edu.vn'),
(22,'uuid-22','Đỗ Thị Ngọc Anh','a45141@thanglong.edu.vn'),
(23,'uuid-23','Nguyễn Đức Minh','minhnd@thanglong.edu.vn'),
(24,'uuid-24','Hoàng Thu Hà','ha.hoang@thanglong.edu.vn'),
(25,'uuid-25','Đỗ Minh Tuấn','tuan.do@thanglong.edu.vn'),
(26,'uuid-26','Nguyễn Thị Lan Anh','lananh.nguyen@thanglong.edu.vn'),
(27,'uuid-27','Trần Quốc Bảo','bao.tran@thanglong.edu.vn'),
(28,'uuid-28','Lê Hoàng Nam','nam.le@thanglong.edu.vn'),
(29,'uuid-29','Phạm Gia Khánh','khanh.pham@thanglong.edu.vn'),
(30,'uuid-30','Trần Gia Bảo','a45149@thanglong.edu.vn');

-- roles
INSERT INTO roles (id, code, name) VALUES
(1,'ADMIN', 'Administrator'),
(2,'STUDENT', 'Student'),
(3,'LECTURER', 'Lecturer');

-- user_roles
INSERT INTO user_roles (oauth_user_id, role_id) VALUES
(1,2),(2,2),(4,2),(5,2),(6,2),(7,2),(8,2),
(3,3),(9,3),(10,3);

-- faculties
INSERT INTO faculties (id, faculty_code, faculty_name) VALUES
(1,'CNTT','Công nghệ thông tin'),
(2,'KTQL','Kinh tế - Quản lý'),
(3,'KHSK','Khoa học sức khỏe'),
(4,'KNN','Ngoại ngữ'),
(5,'XHNV','Khoa học xã hội và nhân văn'),
(6,'KDL','Du lịch'),
(7,'KTT','Truyền thông đa phương tiện'),
(8,'KAN','Âm nhạc ứng dụng'),
(9,'PDT','Phòng đào tạo');


-- departments
INSERT INTO departments (id, faculty_id, department_code, department_name) VALUES
(1,1,'TT','Toán Tin'),
(2,1,'AI','Trí tuệ nhân tạo'),
(3,2,'KT','Kế toán'),
(4,2,'KDMK','Quản trị Kinh doanh & Marketing'),
(5,4,'HQ','Ngôn ngữ Hàn Quốc'),
(6,4,'TQ','Ngôn ngữ Trung Quốc'),
(7,2,'LGC','Logistics và Quản lý Chuỗi cung ứng'),
(8,4,'TA','Ngôn ngữ Anh'),
(9,3,'GDT','Giáo dục Thể chất'),
(10,2,'LE','Luật Kinh tế'),
(11,4, 'GF', 'Ngôn ngữ Pháp');

-- majors
INSERT INTO majors (id, major_code, major_name, faculty_id) VALUES
(1,'TA','Trí tuệ nhân tạo',1),
(2,'TI','Khoa học máy tính',1),
(3,'TE','Mạng máy tính và truyền thông dữ liệu',1),
(4,'TT','Hệ thống thông tin',1),
(5,'IT','Công nghệ thông tin',1),
(6,'EC','Thương mại điện tử',2),
(7,'IE','Kinh tế quốc tế',2),
(8,'EL','Luật kinh tế',2),
(9,'MK','Marketing',2),
(10,'AC','Kế toán',2),
(11,'FN','Tài chính - Ngân hàng',2),
(12,'LG','Logistics và Quản lí chuỗi cung ứng',2),
(13,'BA','Quản trị kinh doanh',2),
(14,'NU','Điều dưỡng',3),
(15,'EN','Ngôn ngữ Anh',4),
(16,'KR','Ngôn ngữ Hàn Quốc',4),
(17,'CN','Ngôn ngữ Trung Quốc',4),
(18,'JP','Ngôn ngữ Nhật',4),
(19,'VN','Việt Nam học',5),
(20,'TM','Quản trị và Du lịch - Lữ hành',6),
(21,'HM','Quản trị khách sạn',6),
(22,'MM','Truyền thông đa phương tiện',7),
(23,'GD','Thiết kế đồ họa',7),
(24,'VO','Thanh nhạc',8);



-- student_classes
INSERT INTO student_classes (id, class_code, major_id, start_year) VALUES
-- Trí tuệ nhân tạo
(1,'TA36CL01',1,2023),
(2,'TA36CL02',1,2023),

-- Khoa học máy tính
(3,'TI36CL01',2,2023),
(4,'TI36CL02',2,2023),
(5,'TI36CL03',2,2023),

-- Mạng máy tính và truyền thông dữ liệu
(6,'TE36CL01',3,2023),
(7,'TE36CL02',3,2023),

-- Hệ thống thông tin
(8,'TT36CL01',4,2023),
(9,'TT36CL02',4,2023),

-- Công nghệ thông tin
(10,'IT36CL01',5,2023),
(11,'IT36CL02',5,2023),
(12,'IT36CL03',5,2023),
(13,'IT36CL04',5,2023),

(14,'EC36CL01',6,2023),
(15,'MK36CL01',9,2023),
(16,'BA36CL01',13,2023),

(17,'EN36CL01',15,2023),
(18,'JP36CL01',18,2023);


-- lecturers
INSERT INTO lecturers (id, oauth_user_id, department_id, full_name, lecturer_code, phone_number, email) VALUES
(1,23,1,'Nguyễn Đức Minh','CTI064', '0969642001','minhnd@thanglong.edu.vn'),
(2,24,2,'Hoàng Thu Hà','KDM001','0123456789','ha.hoang@thanglong.edu.vn'),
(3,25,3,'Đỗ Minh Tuấn','KDM002','0123456789','tuan.do@thanglong.edu.vn'),
(4,26,1,'Nguyễn Thị Lan Anh','CTI065','0901000001','lananh.nguyen@thanglong.edu.vn'),
(5,27,1,'Trần Quốc Bảo','CTI066','0901000002','bao.tran@thanglong.edu.vn'),
(6,28,1,'Lê Hoàng Nam','CTI067','0901000003','nam.le@thanglong.edu.vn'),
(7,29,1,'Phạm Gia Khánh','CTI068','0901000004','khanh.pham@thanglong.edu.vn');

-- academic_advisors
INSERT INTO academic_advisors (id, lecturer_id, student_class_id) VALUES
(1,1,1),
(2,1,2),
(3,2,3),
(4,2,4),
(5,3,5),
(6,3,6),
(7,4,7),
(8,4,8),
(9,5,9),
(10,5,10),
(11,6,11),
(12,6,12),
(13,7,13);

-- students
INSERT INTO students (id, oauth_user_id, student_class_id, full_name, student_code, gender, date_of_birth, status) VALUES
(1,1,3,'Lê Việt Hoàng','A45033','NAM','2003-05-10','ACTIVE'),
(2,2,10,'Nguyễn Ngọc Anh','A45035','NU','2004-08-20','ACTIVE'),
(3,3,10,'Phùng Thanh Độ','A45044','NAM','2003-02-11','ACTIVE'),
(4,11,3,'Nguyễn Hoàng Long','A45039','NAM','2004-02-15','ACTIVE'),
(5,12,3,'Trần Minh Tuấn','A45040','NAM','2004-06-21','ACTIVE'),
(6,13,3,'Lê Thị Thu Trang','A45041','NU','2004-09-12','ACTIVE'),
(7,14,3,'Phạm Đức Anh','A45042','NAM','2003-11-05','ACTIVE'),
(8,15,3,'Hoàng Ngọc Linh','A45043','NU','2004-07-30','ACTIVE'),
(9,16,10,'Đỗ Quang Trung','A45045','NAM','2003-03-18','ACTIVE'),
(10,17,1,'Nguyễn Hải Đăng','A45046','NAM','2004-01-25','ACTIVE'),
(11,18,1,'Trần Thu Phương','A45047','NU','2004-05-14','ACTIVE'),
(12,19,10,'Lê Quốc Khánh','A45048','NAM','2003-08-09','ACTIVE'),
(13,20,3,'Phạm Thùy Dương','A45049','NU','2004-10-01','ACTIVE'),
(14,21,10,'Hoàng Minh Khang','A45050','NAM','2003-12-22','ACTIVE'),
(15,22,10,'Đỗ Thị Ngọc Anh','A45051','NU','2004-04-11','ACTIVE'),
(16,6,10,'Đỗ Minh Tuấn','A45052','NAM','2003-09-17','ACTIVE'),
(17,7,6,'Nguyễn Thị Lan Anh','A45053','NU','2004-02-28','ACTIVE'),
(18,8,8,'Trần Quốc Bảo','A45054','NAM','2003-06-06','ACTIVE'),
(19,9,8,'Lê Hoàng Nam','A45055','NAM','2004-08-19','ACTIVE'),
(20,10,10,'Phạm Gia Khánh','A45056','NAM','2004-03-27','ACTIVE'),
(21,30,10,'Trần Gia Bảo','A45057','NAM','2003-07-13','ACTIVE');



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
(21,'0900000028','Hồ Chí Minh','cc1@gmail.com');

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
(21,'Lê Thị Thanh','0980000014','Mẹ','Hà Nội');

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
(21,'100000000028','CCCD','2021-04-28','Hà Nội');
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
(21,'BHYT028','Bao Minh','2024-12-15','2026-12-15','BV Bạch Mai');
-- semesters
INSERT INTO semesters (id, semester_name, semester_code, academic_years, semester_number, start_date, end_date)
VALUES
(1,'Học kỳ 1 2024-2025','HK1-2024-2025','2024-2025',1,'2024-09-02','2024-12-22'),
(2,'Học kỳ 2 2024-2025','HK2-2024-2025','2024-2025',2,'2024-12-30','2025-04-27'),
(3,'Học kỳ tăng cường 2024-2025','HKTC-2024-2025','2024-2025',3,'2025-05-05','2025-08-24'),
(4,'Học kỳ 1 2025-2026','HK1-2025-2026','2025-2026',1,'2025-09-08','2025-12-28'),
(5,'Học kỳ 2 2025-2026','HK2-2025-2026','2025-2026',2,'2026-01-05','2026-04-26'),
(6,'Học kỳ tăng cường 2025-2026','HKTC-2025-2026','2025-2026',3,'2026-05-04','2026-08-23'),
(7,'Học kỳ 1 2026-2027','HK1-2026-2027','2026-2027',1,'2026-09-07','2026-12-20'),
(8,'Học kỳ 2 2026-2027','HK2-2026-2027','2026-2027',2,'2027-01-04','2027-04-25'),
(9,'Học kỳ tăng cường 2026-2027','HKTC-2026-2027','2026-2027',3,'2027-05-03','2027-08-22');


-- study_programs
INSERT INTO study_programs (id, major_id, study_program_code, study_program_name, total_credits, start_year) VALUES
(1,1,'DHCQK36TA','Trí tuệ nhân tạo - Khóa 36',130,2023),
(2,2,'DHCQK36TI','Khoa học máy tính - Khóa 36',130,2023),
(3,3,'DHCQK36TE','Mạng máy tính và truyền thông dữ liệu - Khóa 36',130,2023),
(4,4,'DHCQK36TT','Hệ thống thông tin - Khóa 36',130,2023),
(5,5,'DHCQK36IT','Công nghệ thông tin - Khóa 36',130,2023),

(6,6,'DHCQK35EC','Thương mại điện tử - Khóa 35',125,2022),
(7,7,'DHCQK35IE','Kinh tế quốc tế - Khóa 35',125,2022),
(8,8,'DHCQK35EL','Luật kinh tế - Khóa 35',125,2022),
(9,9,'DHCQK35MK','Marketing - Khóa 35',125,2022),
(10,10,'DHCQK35AC','Kế toán - Khóa 35',125,2022),
(11,11,'DHCQK35FN','Tài chính - Ngân hàng - Khóa 35',125,2022),
(12,12,'DHCQK35LG','Logistics và Quản lí chuỗi cung ứng - Khóa 35',125,2022),
(13,13,'DHCQK35BA','Quản trị kinh doanh - Khóa 35',125,2022),

(14,14,'DHCQK35NU','Điều dưỡng - Khóa 35',140,2022),

(15,15,'DHCQK35EN','Ngôn ngữ Anh - Khóa 35',120,2022),
(16,16,'DHCQK35KR','Ngôn ngữ Hàn Quốc - Khóa 35',120,2022),
(17,17,'DHCQK35CN','Ngôn ngữ Trung Quốc - Khóa 35',120,2022),
(18,18,'DHCQK35JP','Ngôn ngữ Nhật - Khóa 35',120,2022),

(19,19,'DHCQK35VN','Việt Nam học - Khóa 35',120,2022),

(20,20,'DHCQK35TM','Quản trị và Du lịch - Lữ hành - Khóa 35',120,2022),
(21,21,'DHCQK35HM','Quản trị khách sạn - Khóa 35',120,2022),

(22,22,'DHCQK35MM','Truyền thông đa phương tiện - Khóa 35',120,2022),
(23,23,'DHCQK35GD','Thiết kế đồ họa - Khóa 35',120,2022),

(24,24,'DHCQK35VO','Thanh nhạc - Khóa 35',110,2022);



-- student_majors
INSERT INTO student_majors (student_id, major_id, study_program_id, start_year, end_year) VALUES
(1,2,2,2023,2027),
(2,5,5,2023,2027),
(3,5,5,2023,2027),
(4,1,2,2023,2027),
(5,1,2,2023,2027),
(6,5,2,2023,2027),
(7,2,2,2023,2027),
(8,1,2,2023,2027),
(9,1,5,2023,2027),
(10,4,1,2023,2027),
(11,4,1,2023,2027),
(12,5,2,2023,2027),
(13,5,5,2023,2027),
(14,5,5,2023,2027),
(15,1,5,2023,2027),
(16,1,5,2023,2027),
(17,3,3,2023,2027),
(18,3,3,2023,2027),
(19,4,4,2023,2027),
(20,4,4,2023,2027),
(21,5,5,2023,2027);

-- academic_infos
INSERT INTO academic_infos (student_major_id, cohort, position) VALUES
(1,'K36','Lớp trưởng'),
(2,'K36','Sinh viên'),
(3,'K36','Lớp phó'),
(4,'K36','Sinh viên'),
(5,'K36','Sinh viên'),
(6,'K36','Sinh viên'),
(7,'K36','Sinh viên'),
(8,'K36','Sinh viên'),
(9,'K36','Sinh viên'),
(10,'K36','Sinh viên'),
(11,'K36','Sinh viên'),
(12,'K36','Sinh viên'),
(13,'K36','Sinh viên'),
(14,'K36','Sinh viên'),
(15,'K36','Sinh viên'),
(16,'K36','Sinh viên'),
(17,'K36','Sinh viên'),
(18,'K36','Sinh viên'),
(19,'K36','Sinh viên'),
(20,'K36','Sinh viên'),
(21,'K36','Sinh viên');

-- subjects
INSERT INTO subjects (id, faculty_id, department_id, subject_code, subject_name, credits, coefficient, lecture_hours, practice_hours)
VALUES
(1,9,null,'AD205','Kỹ năng soạn thảo văn bản (MS Office)',3,1.0,45,null),
(2,6,null,'AD206','Ẩm thực Việt Nam',3,1.0,30,30),
(3,9,null,'AD207','Kỹ năng soạn thảo văn bản (MS Open)',3,1.0,45,null),
(4,9,null,'AD212','Phương pháp hùng biện và các thủ thuật tranh biện',3,1.0,45,null),
(5,9,null,'AD213','Hát - Nhạc',3,1.0,45,null),
(6,9,null,'AD214','Nâng cao chất lượng giọng hát',3,1.0,45,null),
(7,5,null,'AD215','Kỹ năng sống',3,1.0,45,null),
(8,4,11,'GF101','Tiếng Pháp 1',2,1.0,54,null),
(9,4,11,'GF102','Tiếng Pháp 2',2,1.0,54,null),
(10,3,9,'PG102','GDTC: Thể dục cổ truyền cơ bản',1,1.0,25,null),
(11,1,null,'CS100','Tin đại cương',2,1.5,18,24),
(12,9,null,'CS102','Tin học văn phòng',2,1.5,30,null),
(13,2,null,'EC102','Nhập môn kinh tế học',2,1.2,30,null),
(14, 4 ,8,'GE111','Tiếng Anh sơ cấp 1',2,1.2,54,null),
(15, 1 ,1,'MA101','Logic, suy luận toán học và kỹ thuật đếm',3,1.2,27,36),
(16, 9 ,null,'ML113','Triết học Mác - Lênin',3,1.2,45,null),
(17, 9 ,null,'NA151','Khoa học môi trường',2,1.2,30,null),
(18, 5 ,null,'SH131','Pháp luật đại cương',2,1.2,30,null),
(19, 5 ,null,'VL101','Tiếng Việt thực hành',2,1.2,30,null),
(20, 1 ,null,'CS121','Lập trình cơ sở 1',3,1.5,27,36),
(21, 1 ,null,'CS212','Kiến trúc máy tính',3,1.5,45,null),
(22, 1 ,null,'SE422','Phát triển dự án',3,1.5,45,null),
(23, 4 ,8,'GE112','Tiếng Anh sơ cấp 2',2,1.2,54,null),
(24, 1 ,1,'MA120','Đại số tuyến tính',3,1.2,27,36),
(25, 9 ,null,'ML114','Kinh tế chính trị Mác - Lênin',2,1.2,30,null),
(26, 4 ,8,'GE121','Tiếng Anh sơ trung cấp 1',2,1.2,54,null),
(27, 1 ,1,'MA110','Giải tích 1',3,1.2,27,36),
(28, 1 ,1,'MA111','Giải tích 2',3,1.2,27,36),
(29, 1 ,null,'MI201','Toán rời rạc',3,1.2,45,null),
(30, 9 ,null,'ML115','Chủ nghĩa xã hội khoa học',2, 1.2,30,null),
(31, 1 ,null,'CS110','Kỹ thuật số',2, 1.5,18,24),
(32, 1 ,1,'CF213','Cấu trúc dữ liệu và giải thuật',4,1.6,45,36),
(33, 4 ,8,'GE222','Tiếng Anh sơ trung cấp 2',2,1.2,54,null),
(34, 1 ,null,'IS222','Cơ sở dữ liệu',3,1.2,45,null),
(35, 1 ,1,'MA239','Xác suất thống kê',4,1.6,45,27),
(36, 9 ,null,'ML202','Tư tưởng Hồ Chí Minh',2,1.2,30,null),
(37, 1 ,null,'CS315','Nguyên lý hệ điều hành',3,1.2,45,null),
(38, 1 ,null,'IS322','Hệ quản trị cơ sở dữ liệu',3,1.2,45,null),
(39,1,null,'IS314','Hệ thống thông tin',3, 1.2,45,null),
(40,1,null,'CS122','Lập trình hướng đối tượng',3,1.5,27,36),
(41,1,null, 'CF231', 'Lý thuyết thông tin và mã hóa', 2, 1.2, 18, 24),
(42,1,null, 'NW212', 'Mạng máy tính', 2, 1.2, 18, 24),
(43,1,null, 'CF301', 'Ngôn ngữ hình thức và Otomat', 3, 1.5, 45, null),
(44,1,null, 'IS332', 'Phân tích thiết kế hướng đối tượng', 3, 1.5, 45, null),
(45,1,null, 'MI312', 'Đồ họa', 2, 1.2, 18, 24),
(46,1,null, 'MI322', 'Trí tuệ nhân tạo và công nghệ tri thức', 3, 1.2, 45, null),
(47,1,null, 'SE302', 'Công nghệ phần mềm', 2, 1.2, 18, 24);

-- study_program_subjects
INSERT INTO study_program_subjects (study_program_id, subject_id, semester_id, is_required, elective_group)
VALUES
-- IT

(5,10,1,true, 'NHOM-GDTC (chọn 4/15 tín chỉ)'),
(5,11,1,true, null),
(5,12,1,true, null),
(5,13,1,true, null),
(5,14,1,true, null),
(5,15,1,true, null),
(5,16,2,true, null),
(5,17,2,true, null),
(5,18,2,true, null),
(5,19,2,true, null),
(5,20,2,true, null),
(5,21,2,true, null),
(5,22,2,true, 'NHOM-LCN (chọn 5/34 tín chỉ)'),
(5,23,2,true, null),
(5,24,2,true, null),
(5,25,4,true, null),
(5,26,4,true, null),
(5,27,4,true, null),
(5,28,4,true, null),
(5,29,4,true, null),
(5,30,4,true, null),
(5,31,4,true, null),
(5,32,4,true, null),
(5,33,4,true, null),
(5,34,5,true, null),
(5,35,5,true, null),
(5,36,5,true, null),
(5,37,5,true, null),
(5,38,5,true, null),
(5,39,5,true, null),
(5,40,5,true, null),
(5,1,5,false, null),
(5,2,5,false, null),
(5,3,5,false, null),
(5,4,5,false, null),
(5,5,5,false, null),
(5,6,5,false, null),
(5,7,5,false, null),
(5,8,5,false, null),
(5,9,5,false, null),

-- TI
(2,10,1,true, 'NHOM-GDTC (chọn 4/15 tín chỉ)'),
(2,11,1,true, null),
(2,12,1,true, null),
(2,13,1,true, null),
(2,14,1,true, null),
(2,15,1,true, null),
(2,16,2,true, null),
(2,17,2,true, null),
(2,18,2,true, null),
(2,19,2,true, null),
(2,20,2,true, null),
(2,21,2,true, null),
(2,22,2,true, 'NHOM-LCN (chọn 5/34 tín chỉ)'),
(2,23,2,true, null),
(2,24,2,true, null),
(2,25,4,true, null),
(2,26,4,true, null),
(2,27,4,true, null),
(2,28,4,true, null),
(2,29,4,true, null),
(2,30,4,true, null),
(2,31,4,true, null),
(2,32,4,true, null),
(2,33,4,true, null),
(2,34,5,true, null),
(2,35,5,true, null),
(2,36,5,true, null),
(2,37,5,true, null),
(2,38,5,true, null),
(2,39,5,true, null),
(2,40,5,true, null),
(2,1,5,false, null),
(2,2,5,false, null),
(2,3,5,false, null),
(2,4,5,false, null),
(2,5,5,false, null),
(2,6,5,false, null),
(2,7,5,false, null),
(2,8,5,false, null),
(2,9,5,false, null);

-- course_classes
INSERT INTO course_classes (id, lecturer_id, subject_id, semester_id, class_code, class_name, capacity)
VALUES
(1,1,32,4,'252CF21301','Cấu trúc dữ liệu và giải thuật 01',40),
(2,1,7,4,'252AD21501','Kỹ năng sống 01',40),
(3,2,8,4,'252GF10101','Tiếng Pháp 1 - 01',35),
(4,2,9,4,'252GF10201','Tiếng Pháp 2 - 01',35),
(5,3,10,4,'252PG10201','GDTC cổ truyền 01',50),

(6,1,11,4,'252CS10001','Tin đại cương 01',45),
(7,4,12,4,'252CS10201','Tin học văn phòng 01',45),
(8,5,13,4,'252EC10201','Nhập môn kinh tế học 01',50),
(9,6,14,4,'252GE11101','Tiếng Anh sơ cấp 1 - 01',40),

(10,1,15,4,'252MA10101','Logic và suy luận toán học 01',45),
(11,7,16,4,'252ML11301','Triết học Mác - Lênin 01',50),
(12,7,17,4,'252NA15101','Khoa học môi trường 01',40),
(13,4,18,5,'252SH13101','Pháp luật đại cương 01',45),
(14,4,19,5,'252VL10101','Tiếng Việt thực hành 01',45),

(15,1,20,5,'252CS12101','Lập trình cơ sở 1 - 01',45),
(16,1,21,5,'252CS21201','Kiến trúc máy tính 01',40),
(17,1,22,5,'252SE42201','Phát triển dự án 01',45),

(18,6,23,5,'252GE11201','Tiếng Anh sơ cấp 2 - 01',40),
(19,1,24,5,'252MA12001','Đại số tuyến tính 01',45),
(20,7,25,5,'252ML11401','Kinh tế chính trị M-L 01',50),

(21,6,26,5,'252GE12101','Tiếng Anh sơ trung cấp 1 - 01',40),
(22,1,27,5,'252MA11001','Giải tích 1 - 01',45),
(23,1,28,5,'252MA11101','Giải tích 2 - 01',45),

(24,1,29,5,'252MI20101','Toán rời rạc 01',45),
(25,7,30,6,'252ML11501','CNXH khoa học 01',50),
(26,1,31,6,'252CS11101','Kỹ thuật số 01',40),

(27,1,32,6,'252CF21301','Cấu trúc dữ liệu và giải thuật 01',40),
(28,6,33,6,'252GE22201','Tiếng Anh sơ trung cấp 2 - 01',40),

(29,1,34,6,'252IS22201','Cơ sở dữ liệu 01',45),
(30,1,35,6,'252MA23901','Xác suất thống kê 01',45),
(31,7,36,6,'252ML20201','Tư tưởng HCM 01',50),

(32,1,37,6,'252CS31501','Hệ điều hành 01',40),
(33,1,38,6,'252IS32201','Hệ QTCSDL 01',40),
(34,1,39,6,'252IS31401','Hệ thống thông tin 01',40),
(35,1,40,6,'252CS12201','Lập trình hướng đối tượng 01',45),

(36,1,32,6,'252CF21301','Cấu trúc dữ liệu và giải thuật 01',40),
(37,1,7,6,'252AD21501','Kỹ năng sống 01',40),
(38,2,8,6,'252GF10101','Tiếng Pháp 1 - 01',35),
(39,2,9,6,'252GF10201','Tiếng Pháp 2 - 01',35),
(40,3,10,6,'252PG10201','GDTC cổ truyền 01',50),

(41,1,11,6,'252CS10001','Tin đại cương 01',45),
(42,4,12,6,'252CS10201','Tin học văn phòng 01',45),
(43,5,13,6,'252EC10201','Nhập môn kinh tế học 01',50),
(44,6,14,6,'252GE11101','Tiếng Anh sơ cấp 1 - 01',40),

(45,1,15,7,'252MA10101','Logic và suy luận toán học 01',45),
(46,1,15,7,'252MA10102','Logic và suy luận toán học 02',45),
(47,7,16,7,'252ML11301','Triết học Mác - Lênin 01',50),
(48,7,16,7,'252ML11302','Triết học Mác - Lênin 02',50),
(49,7,17,7,'252NA15101','Khoa học môi trường 01',40),
(50,7,17,7,'252NA15102','Khoa học môi trường 02',40),
(51,4,18,7,'252SH13101','Pháp luật đại cương 01',45),
(52,4,19,7,'252VL10101','Tiếng Việt thực hành 01',45),

(53,1,20,7,'252CS12101','Lập trình cơ sở 1 - 01',45),
(54,1,20,7,'252CS12102','Lập trình cơ sở 1 - 02',45),
(55,1,21,7,'252CS21201','Kiến trúc máy tính 01',40),
(56,1,21,7,'252CS21202','Kiến trúc máy tính 02',40),
(57,1,22,7,'252SE42201','Phát triển dự án 01',45),
(58,1,22,7,'252SE42202','Phát triển dự án 02',45),

(59,6,23,7,'252GE11201','Tiếng Anh sơ cấp 2 - 01',40),
(60,6,23,7,'252GE11202','Tiếng Anh sơ cấp 2 - 02',40),
(61,1,24,7,'252MA12001','Đại số tuyến tính 01',45),
(62,1,24,7,'252MA12002','Đại số tuyến tính 02',45),
(63,7,25,7,'252ML11401','Kinh tế chính trị M-L 01',50),
(64,7,25,7,'252ML11402','Kinh tế chính trị M-L 02',50),

(65,6,26,7,'252GE12101','Tiếng Anh sơ trung cấp 1 - 01',40),
(66,6,26,7,'252GE12102','Tiếng Anh sơ trung cấp 1 - 02',40),
(67,1,27,7,'252MA11001','Giải tích 1 - 01',45),
(68,1,27,7,'252MA11002','Giải tích 1 - 02',45),
(69,1,28,7,'252MA11101','Giải tích 2 - 01',45),
(70,1,28,7,'252MA11102','Giải tích 2 - 02',45),

(71,1,29,7,'252MI20101','Toán rời rạc 01',45),
(72,1,29,7,'252MI20102','Toán rời rạc 02',45),
(73,7,30,7,'252ML11501','CNXH khoa học 01',50),
(74,7,30,7,'252ML11502','CNXH khoa học 02',50),
(75,1,31,7,'252CS11101','Kỹ thuật số 01',40),
(76,1,31,7,'252CS11102','Kỹ thuật số 02',40),

(77,1,32,7,'252CF21301','Cấu trúc dữ liệu và giải thuật 01',40),
(78,1,32,7,'252CF21302','Cấu trúc dữ liệu và giải thuật 02',40),
(79,6,33,7,'252GE22201','Tiếng Anh sơ trung cấp 2 - 01',40),
(80,6,33,7,'252GE22202','Tiếng Anh sơ trung cấp 2 - 02',40),

(81,1,34,7,'252IS22201','Cơ sở dữ liệu 01',45),
(82,1,34,7,'252IS22202','Cơ sở dữ liệu 02',45),
(83,1,35,7,'252MA23901','Xác suất thống kê 01',45),
(84,1,35,7,'252MA23902','Xác suất thống kê 02',45),
(85,7,36,7,'252ML20201','Tư tưởng HCM 01',50),
(86,7,36,7,'252ML20202','Tư tưởng HCM 02',50),

(87,1,37,7,'252CS31501','Hệ điều hành 01',40),
(88,1,37,7,'252CS31502','Hệ điều hành 02',40),
(89,1,38,7,'252IS32201','Hệ QTCSDL 01',40),
(90,1,38,7,'252IS32202','Hệ QTCSDL 02',40),
(91,1,39,7,'252IS31401','Hệ thống thông tin 01',40),
(92,1,39,7,'252IS31402','Hệ thống thông tin 02',40),
(93,1,40,7,'252CS12201','Lập trình hướng đối tượng 01',45);

-- student_course_classes
INSERT INTO student_course_classes 
(student_id, course_class_id, subject_id, semester_id, status) VALUES

(1,2,7,4,'ENROLLED'),
(1,3,8,4,'ENROLLED'),

(1,4,9,4,'ENROLLED'),
(1,5,10,4,'ENROLLED'),
(1,6,11,4,'ENROLLED'),

(1,7,12,4,'ENROLLED'),
(1,8,13,4,'ENROLLED'),
(1,9,14,4,'ENROLLED'),

(2,2,7,4,'ENROLLED'),
(2,3,8,4,'ENROLLED'),

(2,4,9,4,'ENROLLED'),
(2,5,10,4,'ENROLLED'),
(2,6,11,4,'ENROLLED'),

(2,7,12,4,'ENROLLED'),
(2,8,13,4,'ENROLLED'),
(2,9,14,4,'ENROLLED'),

(3,2,7,4,'ENROLLED'),
(3,3,8,4,'ENROLLED'),

(3,4,9,4,'ENROLLED'),
(3,5,10,4,'ENROLLED'),
(3,6,11,4,'ENROLLED'),

(3,7,12,4,'ENROLLED'),
(3,8,13,4,'ENROLLED'),
(3,9,14,4,'ENROLLED'),

(4,1,32,4,'ENROLLED'),
(4,2,7,4,'ENROLLED'),
(4,3,8,4,'ENROLLED'),

(4,4,9,4,'ENROLLED'),
(4,5,10,4,'ENROLLED'),
(4,6,11,4,'ENROLLED'),

(4,7,12,4,'ENROLLED'),
(4,8,13,4,'ENROLLED'),
(4,9,14,4,'ENROLLED'),

(1,13,18,5,'ENROLLED'),
(1,14,19,5,'ENROLLED'),
(1,15,20,5,'ENROLLED'),

(1,16,21,5,'ENROLLED'),
(1,17,22,5,'ENROLLED'),
(1,18,23,5,'ENROLLED'),

(1,19,24,5,'ENROLLED'),
(1,20,25,5,'ENROLLED'),
(1,21,26,5,'ENROLLED'),

(2,19,24,5,'ENROLLED'),
(2,20,25,5,'ENROLLED'),
(2,21,26,5,'ENROLLED'),

(2,13,18,5,'ENROLLED'),
(2,14,19,5,'ENROLLED'),
(2,15,20,5,'ENROLLED'),

(2,16,21,5,'ENROLLED'),
(2,17,22,5,'ENROLLED'),
(2,18,23,5,'ENROLLED'),

(3,13,18,5,'ENROLLED'),
(3,14,19,5,'ENROLLED'),
(3,15,20,5,'ENROLLED'),

(3,16,21,5,'ENROLLED'),
(3,17,22,5,'ENROLLED'),
(3,18,23,5,'ENROLLED'),

(3,19,24,5,'ENROLLED'),
(3,20,25,5,'ENROLLED'),
(3,21,26,5,'ENROLLED'),

(4,13,18,5,'ENROLLED'),
(4,14,19,5,'ENROLLED'),
(4,15,20,5,'ENROLLED'),

(4,16,21,5,'ENROLLED'),
(4,17,22,5,'ENROLLED'),
(4,18,23,5,'ENROLLED'),

(4,19,24,5,'ENROLLED'),
(4,20,25,5,'ENROLLED'),
(4,21,26,5,'ENROLLED'),

(1,25,30,6,'ENROLLED'),
(1,26,31,6,'ENROLLED'),
(1,27,32,6,'ENROLLED'),

(1,28,33,6,'ENROLLED'),
(1,29,34,6,'ENROLLED'),
(1,30,35,6,'ENROLLED'),

(2,25,30,6,'ENROLLED'),
(2,26,31,6,'ENROLLED'),
(2,27,32,6,'ENROLLED'),

(2,28,33,6,'ENROLLED'),
(2,29,34,6,'ENROLLED'),
(2,30,35,6,'ENROLLED'),

(3,25,30,6,'ENROLLED'),
(3,26,31,6,'ENROLLED'),
(3,27,32,6,'ENROLLED'),

(3,28,33,6,'ENROLLED'),
(3,29,34,6,'ENROLLED'),
(3,30,35,6,'ENROLLED');

-- class_schedules
INSERT INTO class_schedules
(course_class_id, day_of_week, start_period, end_period, start_time, end_time, room)
VALUES
-- class 1 | subject 32 | 2 buổi/tuần
(1,5,4,6,'10:10','12:40','B202'),
(1,6,4,6,'10:10','12:40','B202'),
 
-- class 2 | subject 7 | 1 buổi/tuần
(2,3,4,6,'10:10','12:40','A104'),
 
-- class 3 | subject 8 | 2 buổi/tuần
(3,4,10,12,'15:15','17:30','A102'),
(3,6,1,3,'07:00','09:15','A102'),
 
-- class 4 | subject 9 | 2 buổi/tuần
(4,7,4,6,'10:10','12:40','B205'),
(4,3,10,12,'15:15','17:30','B205'),
 
-- class 5 | subject 10 | 2 buổi/tuần
(5,6,7,9,'13:00','15:30','A103'),
(5,2,1,3,'07:00','09:15','A103'),
 
-- class 6 | subject 11 | 2 buổi/tuần
(6,2,7,9,'13:00','15:30','B203'),
(6,4,1,3,'07:00','09:15','B203'),
 
-- class 7 | subject 12 | 2 buổi/tuần
(7,6,10,12,'15:15','17:30','B203'),
(7,7,10,12,'15:15','17:30','B203'),
 
-- class 8 | subject 13 | 1 buổi/tuần
(8,2,4,6,'10:10','12:40','B205'),
 
-- class 9 | subject 14 | 2 buổi/tuần
(9,2,10,12,'15:15','17:30','B203'),
(9,7,1,3,'07:00','09:15','B203'),
 
-- class 10 | subject 15 | 1 buổi/tuần
(10,4,1,3,'07:00','09:15','A103'),
 
-- class 11 | subject 16 | 1 buổi/tuần
(11,4,4,6,'10:10','12:40','B202'),
 
-- class 12 | subject 17 | 1 buổi/tuần
(12,5,4,6,'10:10','12:40','B204'),
 
-- class 13 | subject 18 | 1 buổi/tuần
(13,7,4,6,'10:10','12:40','A101'),
 
-- class 14 | subject 19 | 1 buổi/tuần
(14,4,10,12,'15:15','17:30','B203'),
 
-- class 15 | subject 20 | 2 buổi/tuần
(15,2,4,6,'10:10','12:40','A103'),
(15,6,10,12,'15:15','17:30','A103'),
 
-- class 16 | subject 21 | 2 buổi/tuần
(16,2,1,3,'07:00','09:15','A102'),
(16,3,7,9,'13:00','15:30','A102'),
 
-- class 17 | subject 22 | 2 buổi/tuần
(17,3,10,12,'15:15','17:30','A103'),
(17,6,7,9,'13:00','15:30','A103'),
 
-- class 18 | subject 23 | 2 buổi/tuần
(18,2,7,9,'13:00','15:30','B203'),
(18,3,1,3,'07:00','09:15','B203'),
 
-- class 19 | subject 24 | 2 buổi/tuần
(19,4,1,3,'07:00','09:15','B204'),
(19,6,1,3,'07:00','09:15','B204'),
 
-- class 20 | subject 25 | 1 buổi/tuần
(20,5,7,9,'13:00','15:30','B201'),
 
-- class 21 | subject 26 | 2 buổi/tuần
(21,5,10,12,'15:15','17:30','B202'),
(21,4,4,6,'10:10','12:40','B202'),
 
-- class 22 | subject 27 | 2 buổi/tuần
(22,2,7,9,'13:00','15:30','A105'),
(22,3,4,6,'10:10','12:40','A105'),
 
-- class 23 | subject 28 | 2 buổi/tuần
(23,4,10,12,'15:15','17:30','B202'),
(23,2,10,12,'15:15','17:30','B202'),
 
-- class 24 | subject 29 | 2 buổi/tuần
(24,4,1,3,'07:00','09:15','B202'),
(24,6,7,9,'13:00','15:30','B202'),
 
-- class 25 | subject 30 | 1 buổi/tuần
(25,5,4,6,'10:10','12:40','B201'),
 
-- class 26 | subject 31 | 2 buổi/tuần
(26,5,7,9,'13:00','15:30','A105'),
(26,4,10,12,'15:15','17:30','A105'),
 
-- class 27 | subject 32 | 2 buổi/tuần
(27,6,10,12,'15:15','17:30','B205'),
(27,3,10,12,'15:15','17:30','B205'),
 
-- class 28 | subject 33 | 2 buổi/tuần
(28,3,7,9,'13:00','15:30','B201'),
(28,2,7,9,'13:00','15:30','B201'),
 
-- class 29 | subject 34 | 2 buổi/tuần
(29,3,4,6,'10:10','12:40','A105'),
(29,7,7,9,'13:00','15:30','A105'),
 
-- class 30 | subject 35 | 2 buổi/tuần
(30,7,4,6,'10:10','12:40','B202'),
(30,2,1,3,'07:00','09:15','B202'),
 
-- class 31 | subject 36 | 1 buổi/tuần
(31,6,4,6,'10:10','12:40','B204'),
 
-- class 32 | subject 37 | 2 buổi/tuần
(32,6,7,9,'13:00','15:30','A101'),
(32,7,4,6,'10:10','12:40','A101'),
 
-- class 33 | subject 38 | 2 buổi/tuần
(33,5,4,6,'10:10','12:40','B204'),
(33,4,10,12,'15:15','17:30','B204'),
 
-- class 34 | subject 39 | 2 buổi/tuần
(34,6,7,9,'13:00','15:30','B203'),
(34,3,1,3,'07:00','09:15','B203'),
 
-- class 35 | subject 40 | 2 buổi/tuần
(35,4,1,3,'07:00','09:15','A102'),
(35,6,1,3,'07:00','09:15','A102'),
 
-- class 36 | subject 32 | 2 buổi/tuần
(36,5,7,9,'13:00','15:30','A101'),
(36,4,7,9,'13:00','15:30','A101'),
 
-- class 37 | subject 7 | 1 buổi/tuần
(37,2,10,12,'15:15','17:30','A104'),
 
-- class 38 | subject 8 | 2 buổi/tuần
(38,3,7,9,'13:00','15:30','B202'),
(38,5,7,9,'13:00','15:30','B202'),
 
-- class 39 | subject 9 | 2 buổi/tuần
(39,3,10,12,'15:15','17:30','A103'),
(39,7,10,12,'15:15','17:30','A103'),
 
-- class 40 | subject 10 | 2 buổi/tuần
(40,6,4,6,'10:10','12:40','B202'),
(40,4,7,9,'13:00','15:30','B202'),
 
-- class 41 | subject 11 | 2 buổi/tuần
(41,4,10,12,'15:15','17:30','A104'),
(41,6,10,12,'15:15','17:30','A104'),
 
-- class 42 | subject 12 | 2 buổi/tuần
(42,5,10,12,'15:15','17:30','A103'),
(42,7,4,6,'10:10','12:40','A103'),
 
-- class 43 | subject 13 | 1 buổi/tuần
(43,6,4,6,'10:10','12:40','A102'),
 
-- class 44 | subject 14 | 2 buổi/tuần
(44,6,1,3,'07:00','09:15','B203'),
(44,2,7,9,'13:00','15:30','B203'),
 
-- class 45 | subject 15 | 1 buổi/tuần
(45,2,7,9,'13:00','15:30','B203'),
 
-- class 46 | subject 15 | 1 buổi/tuần
(46,6,7,9,'13:00','15:30','A101'),
 
-- class 47 | subject 16 | 1 buổi/tuần
(47,6,7,9,'13:00','15:30','A101'),
 
-- class 48 | subject 16 | 1 buổi/tuần
(48,4,4,6,'10:10','12:40','B202'),
 
-- class 49 | subject 17 | 1 buổi/tuần
(49,6,1,3,'07:00','09:15','A103'),
 
-- class 50 | subject 17 | 1 buổi/tuần
(50,2,1,3,'07:00','09:15','B202'),
 
-- class 51 | subject 18 | 1 buổi/tuần
(51,6,4,6,'10:10','12:40','B201'),
 
-- class 52 | subject 19 | 1 buổi/tuần
(52,4,4,6,'10:10','12:40','B204'),
 
-- class 53 | subject 20 | 2 buổi/tuần
(53,2,10,12,'15:15','17:30','B203'),
(53,3,4,6,'10:10','12:40','B203'),
 
-- class 54 | subject 20 | 2 buổi/tuần
(54,5,4,6,'10:10','12:40','A102'),
(54,4,7,9,'13:00','15:30','A102'),
 
-- class 55 | subject 21 | 2 buổi/tuần
(55,2,4,6,'10:10','12:40','B201'),
(55,6,4,6,'10:10','12:40','B201'),
 
-- class 56 | subject 21 | 2 buổi/tuần
(56,7,1,3,'07:00','09:15','B201'),
(56,3,4,6,'10:10','12:40','B201'),
 
-- class 57 | subject 22 | 2 buổi/tuần
(57,2,1,3,'07:00','09:15','B204'),
(57,6,10,12,'15:15','17:30','B204'),
 
-- class 58 | subject 22 | 2 buổi/tuần
(58,4,4,6,'10:10','12:40','A103'),
(58,6,4,6,'10:10','12:40','A103'),
 
-- class 59 | subject 23 | 2 buổi/tuần
(59,2,7,9,'13:00','15:30','B201'),
(59,7,10,12,'15:15','17:30','B201'),
 
-- class 60 | subject 23 | 2 buổi/tuần
(60,4,4,6,'10:10','12:40','A104'),
(60,7,1,3,'07:00','09:15','A104'),
 
-- class 61 | subject 24 | 2 buổi/tuần
(61,5,1,3,'07:00','09:15','A103'),
(61,3,10,12,'15:15','17:30','A103'),
 
-- class 62 | subject 24 | 2 buổi/tuần
(62,3,10,12,'15:15','17:30','A102'),
(62,7,1,3,'07:00','09:15','A102'),
 
-- class 63 | subject 25 | 1 buổi/tuần
(63,3,10,12,'15:15','17:30','A103'),
 
-- class 64 | subject 25 | 1 buổi/tuần
(64,4,7,9,'13:00','15:30','B201'),
 
-- class 65 | subject 26 | 2 buổi/tuần
(65,2,10,12,'15:15','17:30','B205'),
(65,3,4,6,'10:10','12:40','B205'),
 
-- class 66 | subject 26 | 2 buổi/tuần
(66,4,7,9,'13:00','15:30','A101'),
(66,5,10,12,'15:15','17:30','A101'),
 
-- class 67 | subject 27 | 2 buổi/tuần
(67,2,7,9,'13:00','15:30','A102'),
(67,3,7,9,'13:00','15:30','A102'),
 
-- class 68 | subject 27 | 2 buổi/tuần
(68,7,7,9,'13:00','15:30','B201'),
(68,2,10,12,'15:15','17:30','B201'),
 
-- class 69 | subject 28 | 2 buổi/tuần
(69,6,4,6,'10:10','12:40','A105'),
(69,7,1,3,'07:00','09:15','A105'),
 
-- class 70 | subject 28 | 2 buổi/tuần
(70,7,10,12,'15:15','17:30','A105'),
(70,5,4,6,'10:10','12:40','A105'),
 
-- class 71 | subject 29 | 2 buổi/tuần
(71,6,10,12,'15:15','17:30','A103'),
(71,2,4,6,'10:10','12:40','A103'),
 
-- class 72 | subject 29 | 2 buổi/tuần
(72,7,7,9,'13:00','15:30','A104'),
(72,3,1,3,'07:00','09:15','A104'),
 
-- class 73 | subject 30 | 1 buổi/tuần
(73,2,1,3,'07:00','09:15','B202'),
 
-- class 74 | subject 30 | 1 buổi/tuần
(74,3,4,6,'10:10','12:40','A104'),
 
-- class 75 | subject 31 | 2 buổi/tuần
(75,2,7,9,'13:00','15:30','A105'),
(75,3,10,12,'15:15','17:30','A105'),
 
-- class 76 | subject 31 | 2 buổi/tuần
(76,2,1,3,'07:00','09:15','A103'),
(76,6,10,12,'15:15','17:30','A103'),
 
-- class 77 | subject 32 | 2 buổi/tuần
(77,2,4,6,'10:10','12:40','B203'),
(77,6,4,6,'10:10','12:40','B203'),
 
-- class 78 | subject 32 | 2 buổi/tuần
(78,5,4,6,'10:10','12:40','B201'),
(78,4,4,6,'10:10','12:40','B201'),
 
-- class 79 | subject 33 | 2 buổi/tuần
(79,7,10,12,'15:15','17:30','A104'),
(79,3,1,3,'07:00','09:15','A104'),
 
-- class 80 | subject 33 | 2 buổi/tuần
(80,6,4,6,'10:10','12:40','B202'),
(80,3,4,6,'10:10','12:40','B202'),
 
-- class 81 | subject 34 | 2 buổi/tuần
(81,7,4,6,'10:10','12:40','B203'),
(81,3,7,9,'13:00','15:30','B203'),
 
-- class 82 | subject 34 | 2 buổi/tuần
(82,7,1,3,'07:00','09:15','A103'),
(82,3,4,6,'10:10','12:40','A103'),
 
-- class 83 | subject 35 | 2 buổi/tuần
(83,3,10,12,'15:15','17:30','B204'),
(83,5,1,3,'07:00','09:15','B204'),
 
-- class 84 | subject 35 | 2 buổi/tuần
(84,4,7,9,'13:00','15:30','A104'),
(84,6,7,9,'13:00','15:30','A104'),
 
-- class 85 | subject 36 | 1 buổi/tuần
(85,3,1,3,'07:00','09:15','B203'),
 
-- class 86 | subject 36 | 1 buổi/tuần
(86,2,10,12,'15:15','17:30','A101'),
 
-- class 87 | subject 37 | 2 buổi/tuần
(87,4,4,6,'10:10','12:40','A101'),
(87,2,7,9,'13:00','15:30','A101'),
 
-- class 88 | subject 37 | 2 buổi/tuần
(88,4,7,9,'13:00','15:30','A103'),
(88,2,7,9,'13:00','15:30','A103'),
 
-- class 89 | subject 38 | 2 buổi/tuần
(89,7,7,9,'13:00','15:30','A104'),
(89,2,4,6,'10:10','12:40','A104'),
 
-- class 90 | subject 38 | 2 buổi/tuần
(90,5,4,6,'10:10','12:40','B201'),
(90,7,7,9,'13:00','15:30','B201'),
 
-- class 91 | subject 39 | 2 buổi/tuần
(91,4,4,6,'10:10','12:40','B204'),
(91,5,1,3,'07:00','09:15','B204'),
 
-- class 92 | subject 39 | 2 buổi/tuần
(92,4,4,6,'10:10','12:40','A101'),
(92,3,1,3,'07:00','09:15','A101'),
 
-- class 93 | subject 40 | 2 buổi/tuần
(93,2,7,9,'13:00','15:30','B202'),
(93,4,10,12,'15:15','17:30','B202');

-- exam_schedules
INSERT INTO exam_schedules
(id, subject_id, semester_id, exam_date, start_time, end_time, exam_room, exam_format, exam_location, exam_type)
VALUES
(1,32,4,'2025-12-29','08:00','10:00','A101','OFFLINE','Cơ sở 1','FINAL'),
(2,7,4,'2025-12-29','13:00','15:00','A102','OFFLINE','Cơ sở 1','FINAL'),
(3,8,4,'2025-12-30','09:00','11:00','B201','OFFLINE','Cơ sở 1','FINAL'),
(4,9,4,'2025-12-30','13:00','15:00','A103','OFFLINE','Cơ sở 1','FINAL'),
(5,10,4,'2026-01-02','13:00','15:00','B203','OFFLINE','Cơ sở 1','FINAL'),
(6,11,4,'2026-01-02','16:00','18:00','A701','OFFLINE','Cơ sở 1','FINAL'),
(7,12,4,'2026-01-03','08:00','10:30','B601','OFFLINE','Cơ sở 1','FINAL'),
(8,13,4,'2026-01-03','13:00','15:00','A702','OFFLINE','Cơ sở 1','FINAL'),
(9,14,4,'2026-01-04','13:00','15:00','B203','OFFLINE','Cơ sở 1','FINAL'),

(10,18,5,'2026-04-26','08:00','10:00','A302','OFFLINE','Cơ sở 1','FINAL'),
(11,19,5,'2026-04-27','13:00','15:00','A303','OFFLINE','Cơ sở 1','FINAL'),
(12,20,5,'2026-03-27','09:00','11:00','B501','OFFLINE','Cơ sở 1','MIDTERM'),
(13,21,5,'2026-04-28','08:00','10:00','A401','OFFLINE','Cơ sở 1','FINAL'),
(14,22,5,'2026-04-29','13:00','15:00','A502','OFFLINE','Cơ sở 1','FINAL'),
(15,23,5,'2026-04-30','13:00','15:00','B503','OFFLINE','Cơ sở 1','FINAL'),
(16,24,5,'2026-05-01','08:00','10:00','A501','OFFLINE','Cơ sở 1','FINAL'),
(17,25,5,'2026-05-02','13:00','15:00','A702','OFFLINE','Cơ sở 1','FINAL'),
(18,26,5,'2026-05-03','13:00','15:00','B702','OFFLINE','Cơ sở 1','FINAL'),

(19,30,6,'2026-08-24','08:00','10:00','A101','OFFLINE','Cơ sở 1','FINAL'),
(20,31,6,'2026-08-25','13:00','15:00','A102','OFFLINE','Cơ sở 1','FINAL'),
(21,32,6,'2026-08-26','09:00','11:00','B504','OFFLINE','Cơ sở 1','FINAL'),
(22,33,6,'2026-08-27','08:00','10:00','B504','OFFLINE','Cơ sở 1','FINAL'),
(23,34,6,'2026-08-28','13:00','15:00','A708','OFFLINE','Cơ sở 1','FINAL'),
(24,35,6,'2026-08-29','08:00','10:00','A706','OFFLINE','Cơ sở 1','FINAL');

-- student_exam_registrations
INSERT INTO student_exam_registrations
(student_id, exam_schedule_id, exam_attempt, attendance_status)
VALUES

(1,2,1,'ATTENDED'),
(1,3,1,'ATTENDED'),
(1,4,1,'ATTENDED'),
(1,5,1,'ATTENDED'),
(1,6,1,'ATTENDED'),
(1,7,1,'ATTENDED'),
(1,8,1,'ATTENDED'),
(1,9,1,'ATTENDED'),
(1,10,1,'ATTENDED'),
(1,11,1,'ATTENDED'),
(1,12,1,'ATTENDED'),
(1,13,1,'ATTENDED'),
(1,14,1,'ATTENDED'),
(1,15,1,'ATTENDED'),
(1,16,1,'ATTENDED'),
(1,17,1,'ATTENDED'),
(1,18,1,'ATTENDED'),
(1,19,1,'UPCOMING'),
(1,20,1,'UPCOMING'),
(1,21,1,'UPCOMING'),
(1,22,1,'UPCOMING'),
(1,23,1,'UPCOMING'),
(1,24,1,'UPCOMING'),

(2,2,1,'ATTENDED'),
(2,3,1,'ATTENDED'),
(2,4,1,'ATTENDED'),
(2,5,1,'ATTENDED'),
(2,6,1,'ABSENT'),
(2,7,1,'ATTENDED'),
(2,8,1,'ATTENDED'),
(2,9,1,'ATTENDED'),
(2,10,1,'ATTENDED'),
(2,11,1,'ATTENDED'),
(2,12,1,'ATTENDED'),
(2,13,1,'ATTENDED'),
(2,14,1,'ATTENDED'),
(2,15,1,'ATTENDED'),
(2,16,1,'ATTENDED'),
(2,17,1,'ATTENDED'),
(2,18,1,'ATTENDED'),
(2,19,1,'UPCOMING'),
(2,20,1,'UPCOMING'),
(2,21,1,'UPCOMING'),
(2,22,1,'UPCOMING'),
(2,23,1,'UPCOMING'),
(2,24,1,'UPCOMING'),

(3,2,1,'ABSENT'),
(3,3,1,'ATTENDED'),
(3,4,1,'ATTENDED'),
(3,5,1,'ATTENDED'),
(3,6,1,'ATTENDED'),
(3,7,1,'ATTENDED'),
(3,8,1,'ATTENDED'),
(3,9,1,'ATTENDED'),
(3,10,1,'ATTENDED'),
(3,11,1,'ABSENT'),
(3,12,1,'ATTENDED'),
(3,13,1,'ATTENDED'),
(3,14,1,'ATTENDED'),
(3,15,1,'ABSENT'),
(3,16,1,'ATTENDED'),
(3,17,1,'ATTENDED'),
(3,18,1,'ATTENDED'),
(3,19,1,'UPCOMING'),
(3,20,1,'UPCOMING'),
(3,21,1,'UPCOMING'),
(3,22,1,'UPCOMING'),
(3,23,1,'UPCOMING'),
(3,24,1,'UPCOMING');

-- student_subject_results
INSERT INTO student_subject_results
(student_id, subject_id, semester_id, credits,
 attendance_score, midterm_score, final_score,
 score_10, score_4, letter_grade, is_pass)
VALUES

(1, 7, 4, 3, 9.4, 6.6, 5.8, 6.4, 2.0, 'C+', true),
(1,8, 4, 2, 9.5, 5.9, 6.7, 6.7, 2.5, 'B-', true),
(1,9, 4, 2, 8.7, 7.6, 5.5, 6.4, 2.0, 'C+', true),
(1,10, 4, 1, 9.3, 8.0, 8.6, 8.5, 4.0, 'A', true),
(1,11, 4, 2, 7.1, 8.9, 5.6, 6.7, 2.5, 'B-', true),
(1,12, 4, 2, 9.4, 7.3, 7.9, 7.9, 3.5, 'B+', true),
(1,13, 4, 2, 7.5, 6.6, 5.3, 5.9, 1.5, 'C', true),
(1,14, 4, 2, 9.3, 7.4, 5.7, 6.6, 2.5, 'B-', true),
(1,18, 5, 2, 9.5, 8.7, 8.5, 8.7, 4.0, 'A', true),
(1,19, 5, 2, 8.2, 7.9, 6.2, 6.9, 2.5, 'B-', true),
(1,20, 5, 3, 8.3, 6.4, 4.4, 5.4, 1.0, 'D+', true),
(1,21, 5, 3, 7.3, 7.2, 5.9, 6.4, 2.0, 'C+', true),
(1,22, 5, 2, 7.4, 8.8, 5.2, 6.5, 2.5, 'B-', true),
(1,23, 5, 2, 9.5, 8.9, 7.6, 8.2, 3.7, 'A-', true),
(1,24, 5, 3, 8.8, 6.4, 4.8, 5.7, 1.5, 'C', true),
(1,25, 5, 2, 8.9, 8.7, 8.3, 8.5, 4.0, 'A', true),
(1,26, 5, 2, 8.2, 8.8, 5.1, 6.5, 2.5, 'B-', true),
 
(2, 7, 4, 3, 9.5, 7.1, 7.3, 7.5, 3.5, 'B+', true),
(2, 8, 4, 2, 8.8, 9.2, 8.5, 8.7, 4.0, 'A', true),
(2, 9, 4, 2, 9.8, 7.3, 7.8, 7.8, 3.5, 'B+', true),
(2, 10, 4, 1, 8.5, 7.7, 8.0, 8.0, 3.7, 'A-', true),
(2, 11, 4, 2, 8.5, 7.6, 8.4, 8.2, 3.7, 'A-', true),
(2, 12, 4, 2, 9.3, 7.7, 8.3, 8.2, 3.7, 'A-', true),
(2, 13, 4, 2, 9.7, 7.0, 8.9, 8.4, 3.7, 'A-', true),
(2, 14, 4, 2, 9.5, 8.0, 7.0, 7.6, 3.5, 'B+', true),
(2, 18, 5, 2, 10.0, 8.1, 8.2, 8.3, 3.7, 'A-', true),
(2, 19, 5, 2, 9.7, 8.9, 9.1, 9.1, 4.0, 'A+', true),
(2, 20, 5, 3, 9.4, 9.1, 5.6, 7.0, 3.0, 'B', true),
(2, 21, 5, 3, 8.8, 7.9, 5.7, 6.7, 2.5, 'B-', true),
(2, 22, 5, 2, 8.8, 7.3, 7.3, 7.5, 3.5, 'B+', true),
(2, 23, 5, 2, 9.5, 8.1, 7.6, 7.9, 3.5, 'B+', true),
(2, 24, 5, 3, 8.8, 7.8, 8.3, 8.2, 3.7, 'A-', true),
(2, 25, 5, 2, 9.5, 8.8, 7.0, 7.8, 3.5, 'B+', true),
(2, 26, 5, 2, 9.6, 7.5, 7.6, 7.8, 3.5, 'B+', true),

(3, 7, 4, 3, 5.0, 5.2, 4.7, 4.9, 0.7, 'D', true),
(3, 8, 4, 2, 7.4, 3.2, 2.9, 3.4, 0.0, 'F', false),
(3, 9, 4, 2, 4.6, 3.4, 4.9, 4.4, 0.7,'D',true),
(3, 10, 4, 1, 4.5, 4.6, 4.6, 4.6, 0.7,'D',true),
(3, 11, 4, 2, 5.2, 4.2, 5.4, 5.0, 1.0,'D+',true),
(3, 12, 4, 2, 6.4, 2.6, 3.1, 3.3, 0.0,'F',false),
(3, 13, 4, 2, 4.6, 6.0, 5.2, 5.4, 1.0,'D+',true),
(3, 14, 4, 2, 7.2, 5.8, 5.0, 5.5, 1.5,'C',true),
(3, 18, 5, 2, 5.7, 3.8, 4.4, 4.3, 0.7,'D',true),
(3, 19, 5, 2, 7.3, 5.7, 7.0, 6.6, 2.5,'B-',true),
(3, 20, 5, 3, 6.8, 4.7, 5.0, 5.1, 1.0,'D+',true),
(3, 21, 5, 3, 4.5, 5.2, 3.8, 4.3, 0.7,'D',true),
(3, 22, 5, 2, 4.7, 3.3, 4.1, 3.9, 0.0,'F',false),
(3, 23, 5, 2, 5.0, 4.3, 4.4, 4.4, 0.7,'D',true),
(3, 24, 5, 3, 5.6, 3.0, 4.0, 3.9, 0.0,'F',false),
(3, 25, 5, 2, 4.9, 3.4, 4.6, 4.3, 0.7,'D',true),
(3, 26, 5, 2, 5.5, 2.8, 4.6, 4.2, 0.7,'D',true);


-- student_semester_summaries
INSERT INTO student_semester_summaries
(student_id, study_program_id, semester_id, credits_registered, credits_passed,
 semester_gpa, letter_gpa, conduct_score, activity_score, letter_activity_score,
 group_contribution, letter_group_contribution)
VALUES
(1, 2, 4, 23, 23, 2.57, 'B', 70, 8.2, 'B', 8.0, 'B'),
(1, 2, 5, 29, 29, 2.73, 'B', 81, 7.7, 'B', 6.7, 'C'),
(2, 5, 4, 23, 23, 3.65, 'A', 91, 9.3, 'A', 9.4, 'A'),
(2, 5, 5, 29, 29, 3.5, 'B+', 91, 8.7, 'A', 8.1, 'B'),
(3, 5, 4, 23, 17, 0.71, 'D', 59, 5.4, 'D', 6.0, 'C'),
(3, 5, 5, 29, 23, 0.74, 'D', 60, 6.1, 'C', 5.8, 'C');

-- application_types
INSERT INTO application_types (code, name)
VALUES
('LEAVE','Xin nghỉ học'),
('GRADE_REVIEW','Phúc khảo điểm'),
('ENROLL','Xin ép cứng môn học'),
('CERT','Xin giấy xác nhận');

-- student_applications
INSERT INTO student_applications
(student_id, application_type_id, content, status)
VALUES
(1,1,'Xin nghỉ ốm','PENDING'),
(2,2,'Phúc khảo môn Java','APPROVED'),
(3,3,'Xin giấy xác nhận sinh viên','PENDING'),
(5,1,'Xin nghỉ việc gia đình','REJECTED');

-- application_attachments
INSERT INTO application_attachments
(application_id, file_key, original_filename, file_size)
VALUES
(1,'file1.pdf','don_xin_nghi.pdf',123456),
(2,'file2.pdf','phuc_khao.pdf',223456),
(3,'file3.pdf','giay_xac_nhan.pdf',323456);

-- notification_templates
INSERT INTO notification_templates (code, name, content) VALUES
('GENERAL', 'Thông báo chung', '{{content}}'),
('EXAM', 'Thông báo lịch thi', 'Lich thi: {{content}}'),
('FEE', 'Nhắc nộp học phí', 'Sinh viên cần nộp học phí trước {{deadline}}'),
('SCHOLARSHIP', 'Thông báo học bổng', '{{content}}'),
('ACADEMIC_WARNING', 'Cảnh báo học vụ', '{{content}}'),
('DEFENSE', 'Thông báo bảo vệ đồ án', '{{content}}');

-- notifications
INSERT INTO notifications
(id, title, content, created_by, target_type, deadline, is_important, reference_type)
VALUES

-- GLOBAL
(1,'Thông báo hệ thống','Hệ thống sẽ bảo trì vào 23:00 tối nay','SYSTEM','GLOBAL',NULL, false, null),
(2,'Cập nhật cổng thông tin','Đã cập nhật giao diện mới','SYSTEM','GLOBAL',NULL, false, null),
(3,'Thông báo nghỉ lễ','Sinh viên nghỉ lễ quốc khánh','SYSTEM','GLOBAL',NULL, false, null),
(4,'Lịch thi HK1','Lịch thi đã được cập nhật trên portal','SYSTEM','GLOBAL','2024-01-05', true, 'EXAM_SCHEDULE'),
(5,'Kết quả học bổng','Danh sách học bổng HK1 đã được công bố','SYSTEM','GLOBAL',NULL, false, null),

-- STUDENT_CLASS
(6,'Cảnh báo học vụ','Kết quả học tập dưới mức yêu cầu','SYSTEM','STUDENT_CLASS','2024-03-01', true, null),
(7,'Cảnh báo học vụ lần 2','Sinh viên cần gặp cố vấn học tập','SYSTEM','STUDENT_CLASS','2024-03-10', true, null),
(8,'Thông báo riêng','Sinh viên được chọn tham gia workshop','SYSTEM','STUDENT_CLASS','2024-03-10', false, null),
(9,'Thông báo riêng','Sinh viên được cấp tài khoản lab','SYSTEM','STUDENT_CLASS','2024-03-10', false, null),
(10,'Thông báo riêng','Sinh viên cập nhật thông tin cá nhân','SYSTEM','STUDENT_CLASS','2024-03-10', false, null),

-- COURSE_CLASS
(11,'Thông báo lớp lập trình web','Lớp lập trình web thay đổi phòng học','LECTURER','COURSE_CLASS','2024-03-10', false, null),
(12,'Thông báo lớp cấu trúc dữ liệu','Buổi học tối sẽ học online','LECTURER','COURSE_CLASS','2024-03-10', false, null),
(13,'Thông báo lớp nhập môn lập trình','Deadline project được gia hạn','LECTURER','COURSE_CLASS','2024-04-10', false, null),

-- FACULTY
(14,'Thông báo khoa CNTT','Sinh viên tham gia hội thảo AI','FACULTY','FACULTY',NULL, false, null),
(15,'Thông báo khoa CNTT','Cuộc thi lập trình sắp diễn ra','FACULTY','FACULTY',NULL, false, null),
(16,'Thông báo khoa CNTT','Mở đăng ký CLB AI','FACULTY','FACULTY',NULL, false, null),
(17,'Thông báo khoa CNTT','Workshop Cloud Computing','FACULTY','FACULTY',NULL, false, null),
(18,'Thông báo khoa CNTT','Sinh viên đăng ký thực tập hè','FACULTY','FACULTY',NULL, false, null);

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
(500000, '2022-2023', 2023),
(480000, '2022-2023', 2022),

(500000, '2023-2024', 2022),
(520000, '2023-2024', 2023),

(520000, '2024-2025', 2022),
(540000, '2024-2025', 2023),

(540000, '2025-2026', 2022),
(560000, '2025-2026', 2023),

(560000, '2026-2027', 2022),
(580000, '2026-2027', 2023);

-- tuition_invoices
INSERT INTO tuition_invoices
(id, student_id, semester_id, due_date, total_amount, final_amount, status, created_at, updated_at)
VALUES

(1, 1, 4, '2025-09-27', 10528000, 10528000, 'PAID', '2025-09-01', '2025-09-10'),

(2, 2, 4, '2025-09-27', 10528000, 10528000, 'PAID', '2025-09-01', '2025-09-10'),

(3, 3, 4, '2025-09-27', 10528000, 10528000, 'OVERDUE', '2025-09-01', '2026-01-06'),

(4, 4, 4, '2026-09-27', 10528000, 10528000, 'UNPAID', '2025-12-01', '2025-12-01'),

-- học kỳ sau
(5, 1, 5, '2026-01-27', 16296000, 16296000, 'PAID', '2025-12-01', '2026-01-10'),

(6, 2, 5, '2026-01-27', 16296000, 16296000, 'PAID', '2025-12-01', '2026-01-10'),

(7, 3, 5, '2026-01-27', 16296000, 16296000, 'PAID', '2025-12-01', '2026-01-06'),

(8, 4, 5, '2026-01-27', 16296000, 16296000, 'PAID', '2025-12-01', '2026-01-06'),

-- học kỳ sau
(9, 1, 6, '2026-05-30', 11424000, 11424000, 'UNPAID', '2026-05-01', '2026-05-01'),

(10, 2, 6, '2026-05-30', 11424000, 11424000, 'PAID', '2026-05-01', '2026-05-01'),

(11, 3, 6, '2026-05-30', 11424000, 11424000, 'PAID', '2026-05-01', '2026-05-01'),

(12, 4, 6, '2026-05-30', 11424000, 11424000, 'PAID', '2026-05-01', '2026-05-01'),

-- tuition_invoice_items
INSERT INTO tuition_invoice_items
(invoice_id, course_class_id, price_per_credit, credits, coefficient, amount, created_at, updated_at)
VALUES

(1, 2, 560000, 3, 1.0, 1680000, '2025-12-01', '2025-12-01'),
(1, 3, 560000, 2, 1.0, 1120000, '2025-12-01', '2025-12-01'),
(1, 4, 560000, 2, 1.0, 1120000, '2025-12-01', '2025-12-01'),
(1, 5, 560000, 1, 1.0, 560000, '2025-12-01', '2025-12-01'),
(1, 6, 560000, 2, 1.5, 1680000, '2025-12-01', '2025-12-01'),
(1, 7, 560000, 2, 1.5, 1680000, '2025-12-01', '2025-12-01'),
(1, 8, 560000, 2, 1.2, 1344000, '2025-12-01', '2025-12-01'),
(1, 9, 560000, 2, 1.2, 1344000, '2025-12-01', '2025-12-01'),

(2, 2, 560000, 3, 1.0, 1680000, '2025-12-01', '2025-12-01'),
(2, 3, 560000, 2, 1.0, 1120000, '2025-12-01', '2025-12-01'),
(2, 4, 560000, 2, 1.0, 1120000, '2025-12-01', '2025-12-01'),
(2, 5, 560000, 1, 1.0, 560000, '2025-12-01', '2025-12-01'),
(2, 6, 560000, 2, 1.5, 1680000, '2025-12-01', '2025-12-01'),
(2, 7, 560000, 2, 1.5, 1680000, '2025-12-01', '2025-12-01'),
(2, 8, 560000, 2, 1.2, 1344000, '2025-12-01', '2025-12-01'),
(2, 9, 560000, 2, 1.2, 1344000, '2025-12-01', '2025-12-01'),

(3, 2, 560000, 3, 1.0, 1680000, '2025-12-01', '2025-12-01'),
(3, 3, 560000, 2, 1.0, 1120000, '2025-12-01', '2025-12-01'),
(3, 4, 560000, 2, 1.0, 1120000, '2025-12-01', '2025-12-01'),
(3, 5, 560000, 1, 1.0, 560000, '2025-12-01', '2025-12-01'),
(3, 6, 560000, 2, 1.5, 1680000, '2025-12-01', '2025-12-01'),
(3, 7, 560000, 2, 1.5, 1680000, '2025-12-01', '2025-12-01'),
(3, 8, 560000, 2, 1.2, 1344000, '2025-12-01', '2025-12-01'),
(3, 9, 560000, 2, 1.2, 1344000, '2025-12-01', '2025-12-01'),

(4, 2, 560000, 3, 1.0, 1680000, '2025-12-01', '2025-12-01'),
(4, 3, 560000, 2, 1.0, 1120000, '2025-12-01', '2025-12-01'),
(4, 4, 560000, 2, 1.0, 1120000, '2025-12-01', '2025-12-01'),
(4, 5, 560000, 1, 1.0, 560000, '2025-12-01', '2025-12-01'),
(4, 6, 560000, 2, 1.5, 1680000, '2025-12-01', '2025-12-01'),
(4, 7, 560000, 2, 1.5, 1680000, '2025-12-01', '2025-12-01'),
(4, 8, 560000, 2, 1.2, 1344000, '2025-12-01', '2025-12-01'),
(4, 9, 560000, 2, 1.2, 1344000, '2025-12-01', '2025-12-01'),

(5, 13, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),
(5, 14, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),
(5, 15, 560000, 3, 1.5, 2520000, '2026-01-10', '2026-01-10'),
(5, 16, 560000, 3, 1.5, 2520000, '2026-01-10', '2026-01-10'),
(5, 17, 560000, 3, 1.5, 2520000, '2026-01-10', '2026-01-10'),
(5, 18, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),
(5, 19, 560000, 3, 1.2, 2016000, '2026-01-10', '2026-01-10'),
(5, 20, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),
(5, 21, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),

(6, 13, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),
(6, 14, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),
(6, 15, 560000, 3, 1.5, 2520000, '2026-01-10', '2026-01-10'),
(6, 16, 560000, 3, 1.5, 2520000, '2026-01-10', '2026-01-10'),
(6, 17, 560000, 3, 1.5, 2520000, '2026-01-10', '2026-01-10'),
(6, 18, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),
(6, 19, 560000, 3, 1.2, 2016000, '2026-01-10', '2026-01-10'),
(6, 20, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),
(6, 21, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),

(7, 13, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),
(7, 14, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),
(7, 15, 560000, 3, 1.5, 2520000, '2026-01-10', '2026-01-10'),
(7, 16, 560000, 3, 1.5, 2520000, '2026-01-10', '2026-01-10'),
(7, 17, 560000, 3, 1.5, 2520000, '2026-01-10', '2026-01-10'),
(7, 18, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),
(7, 19, 560000, 3, 1.2, 2016000, '2026-01-10', '2026-01-10'),
(7, 20, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),
(7, 21, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),

(8, 13, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),
(8, 14, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),
(8, 15, 560000, 3, 1.5, 2520000, '2026-01-10', '2026-01-10'),
(8, 16, 560000, 3, 1.5, 2520000, '2026-01-10', '2026-01-10'),
(8, 17, 560000, 3, 1.5, 2520000, '2026-01-10', '2026-01-10'),
(8, 18, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),
(8, 19, 560000, 3, 1.2, 2016000, '2026-01-10', '2026-01-10'),
(8, 20, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),
(8, 21, 560000, 2, 1.2, 1344000, '2026-01-10', '2026-01-10'),

(9, 25, 560000, 2, 1.2, 1344000, '2026-05-10', '2026-05-10'),
(9, 26, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10'),
(9, 27, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10'),
(9, 28, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10'),
(9, 29, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10'),
(9, 30, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10'),

(10, 25, 560000, 2, 1.2, 1344000, '2026-05-10', '2026-05-10'),
(10, 26, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10'),
(10, 27, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10'),
(10, 28, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10'),
(10, 29, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10'),
(10, 30, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10'),

(11, 25, 560000, 2, 1.2, 1344000, '2026-05-10', '2026-05-10'),
(11, 26, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10'),
(11, 27, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10'),
(11, 28, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10'),
(11, 29, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10'),
(11, 30, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10'),

(12, 25, 560000, 2, 1.2, 1344000, '2026-05-10', '2026-05-10'),
(12, 26, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10'),
(12, 27, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10'),
(12, 28, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10'),
(12, 29, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10'),
(12, 30, 560000, 3, 1.2, 2016000, '2026-05-10', '2026-05-10');

-- payment
INSERT INTO payment
(invoice_id, amount, provider, transaction_code, status, created_at, updated_at)
VALUES

(1, 10528000, 'ZALOPAY', 'TXN_001_SUCCESS', 'SUCCESS', '2025-09-26', '2025-09-26'),
(2, 10528000, 'ZALOPAY', 'TXN_002_SUCCESS', 'FAILED', '2025-09-26', '2025-09-26'),
(2, 10528000, 'ZALOPAY', 'TXN_003_SUCCESS', 'PENDING', '2025-09-26', '2025-09-26'),
(3, 10528000, 'VNPAY', 'TXN_004_SUCCESS', 'PENDING', '2025-09-26', '2025-09-26'),
(5, 16296000, 'VNPAY', 'TXN_005_SUCCESS', 'SUCCESS', '2026-01-10', '2026-01-10'),
(6, 16296000, 'ZALOPAY', 'TXN_006_SUCCESS', 'SUCCESS', '2026-01-10', '2026-01-10'),
(7, 16296000, 'ZALOPAY', 'TXN_007_SUCCESS', 'SUCCESS', '2026-01-10', '2026-01-10'),
(8, 16296000, 'VNPAY', 'TXN_008_SUCCESS', 'SUCCESS', '2026-01-10', '2026-01-10'),
(9, 11424000, 'VNPAY', 'TXN_009_SUCCESS', 'SUCCESS', '2026-05-01', '2026-05-01'),
(10, 11424000, 'VNPAY', 'TXN_010_SUCCESS', 'SUCCESS', '2026-05-01', '2026-05-01'),
(11, 11424000, 'VNPAY', 'TXN_011_SUCCESS', 'SUCCESS', '2026-05-01', '2026-05-01'),
(12, 11424000, 'VNPAY', 'TXN_012_SUCCESS', 'SUCCESS', '2026-05-01', '2026-05-01'),

-- tuition_transactions
INSERT INTO tuition_transactions
(student_id, invoice_id, amount, type, reference_id, reference_type, description, created_at)
VALUES
-- ===== INVOICE 1 =====
-- phát sinh học phí
(1, 1, 10528000, 'TUITION', 1, 'INVOICE', 'Tao hoa don hoc phi HK1', '2025-12-01'),

-- thanh toán
(1, 1, 10528000, 'PAYMENT', 1, 'PAYMENT', 'Thanh toan qua ZaloPay', '2025-12-05'),

-- ===== INVOICE 2 =====
(2, 2, 10528000, 'TUITION', 2, 'INVOICE', 'Hoc phi HK1', '2025-12-01'),

-- failed payment
(2, 2, 10528000, 'PAYMENT', 2, 'PAYMENT', 'Thanh toan that bai', '2025-12-07'),

-- ===== INVOICE 3 =====
(3, 3, 10528000, 'TUITION', 3, 'INVOICE', 'Hoc phi HK1', '2025-12-01'),

-- pending payment (chưa ghi nhận success)
(3, 3, 10528000, 'PAYMENT', 4, 'PAYMENT', 'Dang xu ly thanh toan', '2025-12-09'),

-- ===== INVOICE 5 =====
(5, 5, 16296000, 'TUITION', 5, 'INVOICE', 'Hoc phi HK2', '2026-03-01'),

(5, 5, 16296000, 'PAYMENT', 5, 'PAYMENT', 'Thanh toan ngan hang', '2026-03-10'),

-- ===== INVOICE 6 (cancel + refund) =====
(6, 6, 16296000, 'TUITION', 6, 'INVOICE', 'Hoc phi HK1', '2025-12-01'),

-- đã thanh toán
(6, 6, 16296000, 'PAYMENT', 6, 'PAYMENT', 'Thanh toan thanh cong', '2025-12-10'),

-- refund
(6, 6, -16296000, 'REFUND', 6, 'PAYMENT', 'Hoan tien do huy hoa don', '2025-12-20');

INSERT INTO subject_prerequisite_groups
(id, subject_id, min_subjects_required, description)
VALUES

(1, 6, 1, 'Cần học học phần mã AD213'),
(2, 9, 1, 'Cần học học phần mã GF101'),
(3, 20, 1, 'Cần học học phần mã CS100'),
(4, 21, 1, 'Cần học học phần mã CS121'),
(5, 22, 1, 'Cần học học phần mã SE302'),
(6, 23, 1, 'Cần học học phần mã GE111'),
(7, 24, 1, 'Cần học học phần mã MA101'),
(8, 26, 1, 'Cần học học phần mã GE112'),
(9, 27, 1, 'Cần học học phần mã MA120'),
(10, 28, 1, 'Cần học học phần mã MA110'),
(11, 29, 1, 'Cần học học phần mã CS122'),
(12, 31, 1, 'Cần học học phần mã MA101'),
(13, 32, 1, 'Cần học học phần mã CS122'),
(14, 33, 1, 'Cần học học phần mã GE121'),
(15, 34, 2, 'Cần học 2 học phần mã CS121, MA101'),
(16, 35, 1, 'Cần học học phần mã MA120'),
(17, 37, 1, 'Cần học học phần mã NW212'),
(18, 38, 1, 'Cần học học phần mã IS222'),
(19, 39, 1, 'Cần học học phần mã IS222'),
(20, 40, 1, 'Cần học học phần mã CS121'),
(21, 41, 1, 'Cần học học phần mã MA239'),
(22, 42, 1, 'Cần học học phần mã CS212'),
(23, 43, 1, 'Cần học học phần mã MI201'),
(24, 44, 2, 'Cần học 2 học phần mã CS122, IS222'),
(25, 45, 2, 'Cần học 2 học phần mã CS122, MA120'),
(26, 46, 1, 'Cần học học phần mã MI201'),
(27, 47, 1, 'Cần học học phần mã IS332');

INSERT INTO subject_prerequisite_group_items
(group_id, prerequisite_subject_id)
VALUES

(1, 5),
(2, 8),
(3, 11),
(4, 20),
(5, 47),
(6, 14),
(7, 15),
(8, 23),
(9, 24),
(10, 27),
(11, 40),
(12, 15),
(13, 40),
(14, 26),
(15, 20),
(15, 15),
(16, 24),
(17, 42),
(18, 34),
(19, 34),
(20, 20),
(21, 35),
(22, 21),
(23, 29),
(24, 34),
(24, 40),
(25, 40),
(25, 24),
(26, 29),
(27, 44);

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

INSERT INTO feedback_category
(name, description, is_active, created_at, updated_at)
VALUES

-- ===== BUG REPORT =====
(
    'BUG',
    'Phản hồi liên quan đến lỗi hệ thống hoặc chức năng ứng dụng',
    TRUE,
    '2026-01-01 08:00:00',
    '2026-01-01 08:00:00'
),

-- ===== UI/UX =====
(
    'UI_UX',
    'Phản hồi liên quan đến giao diện và trải nghiệm người dùng',
    TRUE,
    '2026-01-01 08:10:00',
    '2026-01-01 08:10:00'
),

-- ===== ACCOUNT SUPPORT =====
(
    'ACCOUNT',
    'Các vấn đề liên quan đến tài khoản người dùng',
    TRUE,
    '2026-01-01 08:15:00',
    '2026-01-01 08:15:00'
);

 COMMIT;