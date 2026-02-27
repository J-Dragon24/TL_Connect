BEGIN;
SET CONSTRAINTS ALL DEFERRED;

TRUNCATE TABLE oauth_users RESTART IDENTITY CASCADE;

-- oauth_users
INSERT INTO oauth_users (user_uuid, display_name, email, status) VALUES
('uuid-lecturer-001', 'Nguyen Van An', 'an.nguyen@university.edu.vn', 'ACTIVE'),
('uuid-lecturer-002', 'Tran Thi Bich', 'bich.tran@university.edu.vn', 'ACTIVE'),
('uuid-lecturer-003', 'Le Van Cuong', 'cuong.le@university.edu.vn', 'BLOCKED'),
('uuid-student-001', 'Pham Minh Duc', 'duc.pm@student.edu.vn', 'ACTIVE'),
('uuid-student-002', 'Hoang Thi Em', 'em.ht@student.edu.vn', 'ACTIVE'),
('uuid-student-003', 'Nguyen Quoc Hung', 'hung.nq@student.edu.vn', 'ACTIVE'),
('uuid-student-004', 'Vo Thi Lan', 'lan.vt@student.edu.vn', 'BLOCKED'),
('uuid-student-005', 'Dang Van Minh', 'minh.dv@student.edu.vn', 'ACTIVE');

-- roles
INSERT INTO roles (code, name, is_active) VALUES
('ADMIN', 'Quản trị viên', TRUE),
('LECTURER', 'Giảng viên', TRUE),
('STUDENT', 'Sinh viên', TRUE),
('ADVISOR', 'Cố vấn học tập', TRUE);

-- user_roles
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 2), -- an.nguyen → LECTURER
(2, 2), -- bich.tran → LECTURER
(2, 4), -- bich.tran → ADVISOR
(3, 2), -- cuong.le → LECTURER
(4, 3), -- duc.pm → STUDENT
(5, 3),
(6, 3),
(7, 3),
(8, 3);

-- faculties
INSERT INTO faculties (faculty_code, faculty_name, is_active) VALUES
('CNTT', 'Công nghệ thông tin', TRUE),
('KTKT', 'Kỹ thuật kinh tế', TRUE),
('NGOAI_NGU', 'Ngoại ngữ', FALSE);

-- departments
INSERT INTO departments (faculty_id, department_code, department_name, is_active) VALUES
(1, 'KHMT', 'Khoa học máy tính', TRUE),
(1, 'HTTT', 'Hệ thống thông tin', TRUE),
(2, 'KTCN', 'Kinh tế công nghệ', TRUE);

-- majors
INSERT INTO majors (major_code, major_name, faculty_id, is_active) VALUES
('KHMT', 'Khoa học máy tính', 1, TRUE),
('HTTT', 'Hệ thống thông tin', 1, TRUE),
('KTPM', 'Kỹ thuật phần mềm', 1, TRUE),
('KTKT', 'Kỹ thuật kinh tế', 2, FALSE);

-- student_classes
INSERT INTO student_classes (class_code, major_id, start_year) VALUES
('KHMT2021', 1, 2021),
('KHMT2022', 1, 2022),
('HTTT2021', 2, 2021),
('KTPM2022', 3, 2022);

-- lecturers
INSERT INTO lecturers (oauth_user_id, department_id, full_name, lecturer_code, phone_number, email, status) VALUES
(1, 1, 'Nguyen Van An', 'GV001', '0901234567', 'an.nguyen@university.edu.vn', 'ACTIVE'),
(2, 1, 'Tran Thi Bich', 'GV002', '0902345678', 'bich.tran@university.edu.vn', 'ACTIVE'),
(3, 2, 'Le Van Cuong', 'GV003', '0903456789', 'cuong.le@university.edu.vn', 'INACTIVE');

-- academic_advisors
INSERT INTO academic_advisors (lecturer_id, student_class_id) VALUES
(1, 1),
(2, 2),
(2, 3);

-- students
INSERT INTO students (oauth_user_id, student_class_id, full_name, student_code, gender, date_of_birth, status) VALUES
(4, 1, 'Pham Minh Duc',   'SV2021001', 'NAM', '2003-05-10', 'ACTIVE'),
(5, 1, 'Hoang Thi Em',    'SV2021002', 'NU',  '2003-08-22', 'ACTIVE'),
(6, 2, 'Nguyen Quoc Hung','SV2022001', 'NAM', '2004-01-15', 'ACTIVE'),
(7, 3, 'Vo Thi Lan',      'SV2021003', 'NU',  '2003-11-30', 'SUSPENDED'),
(8, 4, 'Dang Van Minh',   'SV2022002', 'NAM', '2004-03-07', 'ACTIVE');

-- student_contacts
INSERT INTO student_contacts (student_id, phone_number, address, email_personal) VALUES
(1, '0911111111', '12 Nguyen Trai, HCM', 'duc.personal@gmail.com'),
(2, '0922222222', '34 Le Loi, HCM',      'em.personal@gmail.com'),
(3, '0933333333', '56 Tran Hung Dao, HN','hung.personal@gmail.com'),
(4, '0944444444', '78 Phan Boi Chau, DN','lan.personal@gmail.com'),
(5, '0955555555', '90 Le Duan, HCM',     'minh.personal@gmail.com');

-- emergency_contacts
INSERT INTO emergency_contacts (student_id, full_name, phone_number, address) VALUES
(1, 'Pham Van Bo',    '0981111111', '12 Nguyen Trai, HCM'),
(2, 'Hoang Van Cha',  '0982222222', '34 Le Loi, HCM'),
(3, 'Nguyen Van Doi', '0983333333', '56 Tran Hung Dao, HN'),
(4, 'Vo Van Em',      '0984444444', '78 Phan Boi Chau, DN'),
(5, 'Dang Van Gio',   '0985555555', '90 Le Duan, HCM');

-- identity_cards
INSERT INTO identity_cards (student_id, card_number, card_type, issued_date, issued_place) VALUES
(1, '079203001111', 'CCCD', '2021-01-10', 'Cục CS QLHC về TTXH - HCM'),
(2, '079203002222', 'CCCD', '2021-03-15', 'Cục CS QLHC về TTXH - HCM'),
(3, '001203003333', 'CCCD', '2022-06-20', 'Cục CS QLHC về TTXH - HN'),
(4, '048203004444', 'CMND', '2019-08-01', 'CA tỉnh Đà Nẵng'),
(5, '079203005555', 'CCCD', '2022-02-28', 'Cục CS QLHC về TTXH - HCM');

-- health_insurances
INSERT INTO health_insurances (student_id, insurance_number, provider, valid_from, valid_to, registered_hospital, status) VALUES
(1, 'HS4010001111', 'BHXH HCM', '2024-01-01', '2025-12-31', 'BV Chợ Rẫy', 'ACTIVE'),
(2, 'HS4010002222', 'BHXH HCM', '2024-01-01', '2025-12-31', 'BV Đại học Y Dược', 'ACTIVE'),
(3, 'HS4010003333', 'BHXH HN',  '2024-01-01', '2024-12-31', 'BV Bạch Mai', 'EXPIRED'),
(4, 'HS4010004444', 'BHXH ĐN',  '2023-01-01', '2023-12-31', 'BV C Đà Nẵng', 'EXPIRED'),
(5, 'HS4010005555', 'BHXH HCM', '2024-06-01', '2025-05-31', 'BV Nhân dân 115', 'ACTIVE');

-- academic_infos
INSERT INTO academic_infos (student_id, cohort, position, education_mode) VALUES
(1, 'K2021', 'Lớp trưởng', 'CHINH_QUY'),
(2, 'K2021', NULL,          'CHINH_QUY'),
(3, 'K2022', 'Lớp phó',    'CHINH_QUY'),
(4, 'K2021', NULL,          'LIEN_THONG'),
(5, 'K2022', NULL,          'CHINH_QUY');

-- semesters
INSERT INTO semesters (semester_name, start_date, end_date) VALUES
('HK1 2021-2022', '2021-09-01', '2022-01-15'),
('HK2 2021-2022', '2022-02-01', '2022-06-30'),
('HK1 2022-2023', '2022-09-01', '2023-01-15'),
('HK2 2022-2023', '2023-02-01', '2023-06-30'),
('HK1 2025-2026', '2025-09-01', '2026-01-15'),
('HK2 2025-2026', '2025-02-01', '2026-06-30');

-- study_programs
INSERT INTO study_programs (major_id, study_program_code, study_program_name, total_credits, start_year) VALUES
(1, 'CTDT-KHMT-2021', 'Chương trình đào tạo KHMT 2021', 130, 2021),
(1, 'CTDT-KHMT-2022', 'Chương trình đào tạo KHMT 2022', 132, 2022),
(2, 'CTDT-HTTT-2021', 'Chương trình đào tạo HTTT 2021', 128, 2021),
(3, 'CTDT-KTPM-2022', 'Chương trình đào tạo KTPM 2022', 135, 2022);

-- student_majors
INSERT INTO student_majors (student_id, major_id, study_program_id, is_primary, start_year, end_year, status) VALUES
(1, 1, 1, TRUE,  2021, NULL, 'STUDYING'),
(2, 1, 1, TRUE,  2021, NULL, 'STUDYING'),
(3, 1, 2, TRUE,  2022, NULL, 'STUDYING'),
(4, 2, 3, TRUE,  2021, NULL, 'DROPPED'),
(5, 3, 4, TRUE,  2022, NULL, 'STUDYING'),
-- sinh vien 1 học thêm ngành 2
(1, 2, 3, FALSE, 2022, NULL, 'STUDYING');

-- subjects
INSERT INTO subjects (faculty_id, department_id, subject_code, subject_name, credits, lecture_hours, practice_hours, is_active) VALUES
(1, 1, 'INT1001', 'Nhập môn lập trình',         3, 30, 15, TRUE),
(1, 1, 'INT1002', 'Cấu trúc dữ liệu & giải thuật', 3, 30, 15, TRUE),
(1, 1, 'INT1003', 'Lập trình hướng đối tượng',  3, 30, 15, TRUE),
(1, 1, 'INT2001', 'Cơ sở dữ liệu',              3, 30, 15, TRUE),
(1, 1, 'INT2002', 'Mạng máy tính',              3, 30, 15, TRUE),
(1, 2, 'INT2003', 'Hệ thống thông tin',         3, 30, 15, TRUE),
(1, 1, 'INT3001', 'Lập trình web',              3, 15, 30, TRUE),
(1, 1, 'INT3002', 'Kiểm thử phần mềm',         3, 30, 15, TRUE),
(1, 1, 'INT3003', 'Trí tuệ nhân tạo',           3, 30, 15, FALSE);

-- subject_prerequisites
INSERT INTO subject_prerequisites (subject_id, prerequisite_subject_id) VALUES
(2, 1), -- CTDL yêu cầu Nhập môn lập trình
(3, 1), -- OOP yêu cầu Nhập môn lập trình
(4, 2), -- CSDL yêu cầu CTDL
(7, 3), -- Lập trình web yêu cầu OOP
(8, 3), -- Kiểm thử yêu cầu OOP
(9, 2); -- AI yêu cầu CTDL

-- study_program_subjects (chương trình 1 - KHMT 2021)
INSERT INTO study_program_subjects (study_program_id, subject_id, semester_id, elective_group, is_required) VALUES
(1, 1, 1, NULL,       TRUE),
(1, 2, 2, NULL,       TRUE),
(1, 3, 2, NULL,       TRUE),
(1, 4, 3, NULL,       TRUE),
(1, 5, 3, NULL,       TRUE),
(1, 7, 4, NULL,       TRUE),
(1, 8, 4, 'TU_CHON',  FALSE),
(1, 9, 5, 'TU_CHON',  FALSE),
-- chương trình 2 - KHMT 2022
(2, 1, 1, NULL,       TRUE),
(2, 2, 1, NULL,       TRUE),
(2, 3, 2, NULL,       TRUE),
(2, 4, 3, NULL,       TRUE),
(2, 7, 4, NULL,       TRUE);

-- course_classes
INSERT INTO course_classes (lecturer_id, subject_id, semester_id, class_code, class_name) VALUES
(1, 1, 1, 'INT1001-01', 'Nhập môn lập trình - Lớp 01'),
(1, 1, 1, 'INT1001-02', 'Nhập môn lập trình - Lớp 02'),
(1, 2, 2, 'INT1002-01', 'CTDL & GT - Lớp 01'),
(2, 3, 2, 'INT1003-01', 'Lập trình OOP - Lớp 01'),
(2, 4, 3, 'INT2001-01', 'Cơ sở dữ liệu - Lớp 01'),
(1, 5, 3, 'INT2002-01', 'Mạng máy tính - Lớp 01'),
(2, 7, 4, 'INT3001-01', 'Lập trình web - Lớp 01'),
(NULL, 8, 4, 'INT3002-01', 'Kiểm thử PM - Lớp 01'); -- chưa có giảng viên

-- student_course_classes
INSERT INTO student_course_classes (student_id, course_class_id) VALUES
(1, 1), -- duc học INT1001-01
(2, 2), -- em học INT1001-02
(3, 1),
(1, 3), -- duc học CTDL
(2, 3),
(3, 3),
(1, 4), -- duc học OOP
(2, 4),
(1, 5), -- duc học CSDL
(2, 5),
(5, 1),
(5, 3);

-- class_schedules
INSERT INTO class_schedules (course_class_id, day_of_week, start_period, end_period, start_time, end_time, room) VALUES
(1, 2, 1, 3,  '07:00', '09:30', 'A101'), -- thứ 2
(1, 4, 4, 5,  '09:45', '11:15', 'A101'), -- thứ 4
(2, 3, 1, 3,  '07:00', '09:30', 'A102'), -- thứ 3
(3, 2, 6, 8,  '11:30', '14:00', 'B201'), -- thứ 2
(4, 5, 1, 3,  '07:00', '09:30', 'B202'), -- thứ 5
(5, 3, 4, 6,  '09:45', '12:15', 'C301'), -- thứ 3
(6, 6, 1, 3,  '07:00', '09:30', 'C302'), -- thứ 6
(7, 4, 6, 8,  '11:30', '14:00', 'A103'), -- thứ 4
(8, 7, 1, 3,  '07:00', '09:30', 'B203'); -- thứ 7

-- exam_schedules
INSERT INTO exam_schedules (course_class_id, semester_id, exam_date, start_time, end_time, exam_room, exam_location, exam_format, exam_type, note) VALUES
(1, 1, '2022-01-10', '07:30', '09:30', 'P101', 'Co so 1', 'TRAC_NGHIEM', 'GIUA_KY',  NULL),
(2, 1, '2022-01-10', '07:30', '09:30', 'P102', 'Co so 1', 'TRAC_NGHIEM', 'GIUA_KY',  NULL),
(3, 2, '2022-06-15', '07:30', '09:30', 'P201', 'Co so 1', 'TU_LUAN',     'CUOI_KY',  NULL),
(4, 2, '2022-06-16', '07:30', '09:30', 'P202', 'Co so 1', 'TU_LUAN',     'CUOI_KY',  NULL),
(5, 3, '2023-01-05', '07:30', '10:30', 'P301', 'Co so 2', 'TU_LUAN',     'CUOI_KY',  NULL),
(6, 3, '2023-01-06', '07:30', '09:30', 'P302', 'Co so 2', 'TRAC_NGHIEM', 'CUOI_KY',  NULL),
(7, 4, '2023-06-10', '07:30', '09:30', 'P101', 'Co so 1', 'THUC_HANH',   'CUOI_KY',  'Thi thuc hanh tren may'),
(8, 4, '2023-06-11', '07:30', '09:30', 'P102', 'Co so 1', 'TU_LUAN',     'CUOI_KY',  NULL);

-- student_exam_registrations
INSERT INTO student_exam_registrations (student_id, exam_schedule_id, exam_attempt, attendance_status, exam_status) VALUES
(1, 1, 1, 'ATTENDED', 'DONE'),
(2, 2, 1, 'ATTENDED', 'DONE'),
(3, 1, 1, 'ATTENDED', 'DONE'),
(1, 3, 1, 'ATTENDED', 'DONE'),
(2, 3, 1, 'ABSENT',   'DONE'),
(3, 3, 1, 'ATTENDED', 'DONE'),
(1, 4, 1, 'ATTENDED', 'DONE'),
(2, 4, 1, 'ATTENDED', 'DONE'),
(1, 5, 1, 'ATTENDED', 'DONE'),
(2, 5, 1, 'ATTENDED', 'DONE'),
-- thi lan 2
(2, 3, 2, 'ATTENDED', 'DONE'),
-- upcoming
(5, 7, 1, 'UPCOMING', 'NOT_YET'),
(5, 8, 1, 'UPCOMING', 'NOT_YET');

-- student_subject_results
INSERT INTO student_subject_results (student_id, subject_id, semester_id, credits, score_10, score_4, letter_grade, is_pass) VALUES
-- SV Duc (id=1)
(1, 1, 1, 3, 8.5,  3.5,  'A',  TRUE),
(1, 2, 2, 3, 7.0,  3.0,  'B',  TRUE),
(1, 3, 2, 3, 9.0,  4.0,  'A+', TRUE),
(1, 4, 3, 3, 6.5,  2.5,  'C',  TRUE),
(1, 5, 3, 3, 7.5,  3.0,  'B',  TRUE),
-- SV Em (id=2)
(2, 1, 1, 3, 5.0,  1.0,  'D',  FALSE), -- rớt
(2, 1, 2, 3, 6.5,  2.5,  'C',  TRUE),  -- thi lại qua
(2, 2, 2, 3, 7.5,  3.0,  'B',  TRUE),
(2, 3, 2, 3, 8.0,  3.5,  'A',  TRUE),
(2, 4, 3, 3, 6.0,  2.0,  'C-', TRUE),
-- SV Hung (id=3)
(3, 1, 1, 3, 9.5,  4.0,  'A+', TRUE),
(3, 2, 2, 3, 8.5,  3.5,  'A',  TRUE),
(3, 3, 2, 3, 7.0,  3.0,  'B',  TRUE),
-- SV Lan (id=4) - DROPPED
(4, 1, 1, 3, 4.5,  0.0,  'F',  FALSE),
-- SV Minh (id=5)
(5, 1, 1, 3, 8.0,  3.5,  'A',  TRUE),
(5, 2, 2, 3, 7.5,  3.0,  'B',  TRUE);

-- student_semester_summaries
INSERT INTO student_semester_summaries (student_id, study_program_id, semester_id, credits_registered, credits_passed, semester_gpa, conduct_score) VALUES
-- SV Duc
(1, 1, 1, 3,  3,  3.5,  85),
(1, 1, 2, 6,  6,  3.2,  88),
(1, 1, 3, 6,  6,  2.8,  80),
-- SV Em
(2, 1, 1, 3,  0,  1.0,  75),
(2, 1, 2, 9,  9,  2.7,  78),
(2, 1, 3, 6,  6,  2.1,  76),
-- SV Hung
(3, 2, 1, 3,  3,  4.0,  90),
(3, 2, 2, 6,  6,  3.4,  92),
-- SV Minh
(5, 4, 1, 3,  3,  3.5,  83),
(5, 4, 2, 3,  3,  3.0,  85);

-- application_types
INSERT INTO application_types (code, name) VALUES
('HOC_BONG',       'Don xin hoc bong'),
('NGHI_HOC',       'Don xin nghi hoc'),
('CHUYEN_NGANH',   'Don xin chuyen nganh'),
('XAC_NHAN_SV',    'Xac nhan sinh vien'),
('HOAN_THI',       'Don xin hoan thi'),
('PHU_CAP_KTX',    'Don xin phu cap ky tuc xa');

-- student_applications
INSERT INTO student_applications (student_id, application_type_id, content, status) VALUES
(1, 4, 'Xin xac nhan dang la sinh vien de vay von ngan hang',      'APPROVED'),
(2, 1, 'Xin xet hoc bong hoc ky 1 nam hoc 2022-2023',             'APPROVED'),
(3, 4, 'Xac nhan sinh vien phuc vu xin viec lam them',             'APPROVED'),
(4, 2, 'Xin nghi hoc 1 hoc ky vi ly do suc khoe',                 'APPROVED'),
(5, 3, 'Xin chuyen tu nganh KTPM sang nganh KHMT',                'PENDING'),
(1, 5, 'Xin hoan thi mon Co so du lieu vi bi om',                  'PENDING'),
(2, 6, 'Xin phu cap noi tru ky tuc xa hoc ky 2',                  'REJECTED');

-- application_attachments
INSERT INTO application_attachments (application_id, file_key, original_filename, file_size) VALUES
(1, 'files/app1/cccd.jpg',          'CCCD_PhamMinhDuc.jpg',       512000),
(1, 'files/app1/giaykhaisinh.jpg',  'GiayKhaiSinh.jpg',           480000),
(2, 'files/app2/bang_diem.pdf',     'BangDiemHK1.pdf',            256000),
(3, 'files/app3/cccd.jpg',          'CCCD_NguyenQuocHung.jpg',    520000),
(4, 'files/app4/giay_benh.pdf',     'GiayChungNhanBenh.pdf',      320000),
(5, 'files/app5/don_chuyen.pdf',    'DonXinChuyenNganh.pdf',      180000),
(6, 'files/app6/giay_benh.pdf',     'GiayChungNhanBenh_CSDL.pdf', 290000);

-- notifications
INSERT INTO notifications (title, content, sender, target_type, target_id, dead_line) VALUES
('Thong bao lich thi HK1 2021-2022',
 'Phong dao tao thong bao lich thi cuoi ky HK1 2021-2022. Sinh vien xem lich thi tren cong thong tin.',
 'Phong Dao Tao', 'ALL', NULL, '2022-01-05 17:00:00'),

('Thong bao nghi le 30/4',
 'Truong thong bao nghi le 30/4 - 1/5. Sinh vien nghi hoc tu ngay 29/4 den 2/5.',
 'Ban Giam Hieu', 'ALL', NULL, NULL),

('Nhac nho nop hoc phi HK2',
 'Sinh vien lop KHMT2021 chu y nop hoc phi HK2 truoc ngay 28/02/2022.',
 'Phong Tai Chinh', 'CLASS', 1, '2022-02-28 17:00:00'),

('Ket qua xet hoc bong HK1',
 'Danh sach sinh vien duoc hoc bong HK1 2022-2023 da duoc cong bo. Sinh vien kiem tra ket qua.',
 'Phong Cong Tac Sinh Vien', 'ALL', NULL, NULL),

('Canh bao hoc vu',
 'Ban nhan duoc canh bao hoc vu do ket qua hoc tap HK1 chua dat yeu cau. De nghi lien he co van hoc tap.',
 'Phong Dao Tao', 'STUDENT', 2, '2022-03-01 17:00:00'),

('Lich bao ve do an cuoi ky',
 'Sinh vien lop KTPM2022 xem lich bao ve do an mon Lap trinh web.',
 'Giang vien Tran Thi Bich', 'CLASS', 4, '2023-06-20 17:00:00');

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

 COMMIT;