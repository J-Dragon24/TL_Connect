DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS student_contacts CASCADE;
DROP TABLE IF EXISTS emergency_contacts CASCADE;
DROP TABLE IF EXISTS identity_cards CASCADE;
DROP TABLE IF EXISTS health_insurances CASCADE;
DROP TABLE IF EXISTS academic_infos CASCADE;
DROP TABLE IF EXISTS student_classes CASCADE;
DROP TABLE IF EXISTS majors CASCADE;
DROP TABLE IF EXISTS student_majors CASCADE;
DROP TABLE IF EXISTS academic_advisors CASCADE;
DROP TABLE IF EXISTS faculties CASCADE;
DROP TABLE IF EXISTS departments CASCADE;
DROP TABLE IF EXISTS subjects CASCADE;
DROP TABLE IF EXISTS subject_prerequisites CASCADE;
DROP TABLE IF EXISTS study_programs CASCADE;
DROP TABLE IF EXISTS study_program_subjects CASCADE;
DROP TABLE IF EXISTS semesters CASCADE;
DROP TABLE IF EXISTS student_course_classes CASCADE;
DROP TABLE IF EXISTS student_semester_summaries CASCADE;
DROP TABLE IF EXISTS exam_schedules CASCADE;
DROP TABLE IF EXISTS student_exam_registrations CASCADE;
DROP TABLE IF EXISTS application_types CASCADE;
DROP TABLE IF EXISTS student_applications CASCADE;
DROP TABLE IF EXISTS application_attachments CASCADE;
DROP TABLE IF EXISTS class_schedules CASCADE;
DROP TABLE IF EXISTS course_classes CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS oauth_users CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS lecturers CASCADE;
DROP TABLE IF EXISTS student_subject_results CASCADE;
DROP TABLE IF EXISTS grade_scale CASCADE;
DROP TABLE IF EXISTS news CASCADE;
DROP TABLE IF EXISTS fees CASCADE;
DROP TABLE IF EXISTS notification_template CASCADE;
DROP TABLE IF EXISTS notification_read CASCADE;

CREATE TABLE oauth_users (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  user_uuid VARCHAR(255) UNIQUE NOT NULL,
  display_name VARCHAR(255),
  email VARCHAR(255) UNIQUE NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'BLOCKED')),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE roles (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code VARCHAR(50) UNIQUE NOT NULL,
  name VARCHAR(100),
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE user_roles (
  oauth_user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  assigned_at TIMESTAMP DEFAULT now(),
  PRIMARY KEY (oauth_user_id, role_id),
  FOREIGN KEY (oauth_user_id) REFERENCES oauth_users(id) ON DELETE CASCADE,
  FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE faculties (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  faculty_code VARCHAR(20) UNIQUE NOT NULL,
  faculty_name VARCHAR(255) NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE departments (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  faculty_id BIGINT NOT NULL,
  department_code VARCHAR(20) UNIQUE NOT NULL,
  department_name VARCHAR(255) NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE RESTRICT
);

CREATE TABLE majors (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  major_code VARCHAR(20) UNIQUE NOT NULL,
  major_name VARCHAR(100) NOT NULL,
  faculty_id BIGINT NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE RESTRICT
);

CREATE TABLE student_classes (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  class_code VARCHAR(20) UNIQUE NOT NULL,
  major_id BIGINT NOT NULL,
  start_year INT NOT NULL,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (major_id) REFERENCES majors(id) ON DELETE RESTRICT
);

CREATE TABLE lecturers (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  oauth_user_id BIGINT UNIQUE NOT NULL,
  department_id BIGINT,
  full_name VARCHAR(100),
  lecturer_code VARCHAR(20) UNIQUE NOT NULL,
  phone_number VARCHAR(20),
  email VARCHAR(255),
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE')),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (oauth_user_id) REFERENCES oauth_users(id) ON DELETE SET NULL,
  FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
);

CREATE TABLE students (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  oauth_user_id BIGINT UNIQUE NOT NULL,
  student_class_id BIGINT NOT NULL,
  full_name VARCHAR(255),
  student_code VARCHAR(20) UNIQUE NOT NULL,
  gender VARCHAR(20) NOT NULL DEFAULT 'NAM' CHECK (gender IN ('NAM', 'NU')),
  date_of_birth DATE,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','SUSPENDED','GRADUATED','DROPPED_OUT','DELETED')),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (oauth_user_id) REFERENCES oauth_users(id) ON DELETE SET NULL,
  FOREIGN KEY (student_class_id) REFERENCES student_classes(id) ON DELETE SET NULL
);

CREATE TABLE academic_advisors (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  lecturer_id BIGINT NOT NULL,
  student_class_id BIGINT UNIQUE NOT NULL,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (lecturer_id) REFERENCES lecturers(id) ON DELETE CASCADE,
  FOREIGN KEY (student_class_id) REFERENCES student_classes(id) ON DELETE CASCADE
);

CREATE TABLE student_contacts (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  student_id BIGINT UNIQUE NOT NULL,
  phone_number VARCHAR(20),
  address VARCHAR(255),
  email_personal VARCHAR(255),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),

  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE TABLE emergency_contacts (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  student_id BIGINT UNIQUE NOT NULL,
  full_name VARCHAR(100),
  phone_number VARCHAR(20),
  address VARCHAR(255),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE TABLE identity_cards (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  student_id BIGINT UNIQUE NOT NULL,
  card_number VARCHAR(20) UNIQUE NOT NULL,
  card_type VARCHAR(20) NOT NULL DEFAULT 'CCCD' CHECK (card_type IN ('CCCD','CMND')),
  issued_date DATE,
  issued_place VARCHAR(100),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE TABLE health_insurances (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  student_id BIGINT NOT NULL,
  insurance_number VARCHAR(20) UNIQUE,
  provider VARCHAR(100),
  valid_from DATE,
  valid_to DATE CHECK (valid_to >= valid_from),
  registered_hospital VARCHAR(255),
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','EXPIRED')),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE TABLE study_programs (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  major_id BIGINT NOT NULL,
  study_program_code VARCHAR(20) UNIQUE NOT NULL,
  study_program_name VARCHAR(255) NOT NULL,
  total_credits INT,
  start_year INT NOT NULL,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),

  FOREIGN KEY (major_id) REFERENCES majors(id) ON DELETE RESTRICT
);

CREATE TABLE student_majors (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  student_id BIGINT NOT NULL,
  major_id BIGINT NOT NULL,
  study_program_id BIGINT NOT NULL,
  is_primary BOOLEAN DEFAULT TRUE,
  start_year DATE NOT NULL,
  end_year DATE NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'STUDYING' CHECK (status IN ('STUDYING', 'GRADUATED', 'DROPPED')),
  UNIQUE(student_id, major_id),
  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
  FOREIGN KEY (major_id) REFERENCES majors(id) ON DELETE CASCADE,
  FOREIGN KEY (study_program_id) REFERENCES study_programs(id) ON DELETE SET NULL
);

CREATE TABLE academic_infos (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  student_major_id BIGINT UNIQUE NOT NULL,
  cohort VARCHAR(20) NOT NULL,
  position VARCHAR(50),
  education_mode VARCHAR(20) NOT NULL DEFAULT 'CHINH_QUY' CHECK (education_mode IN ('CHINH_QUY','LIEN_THONG')),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (student_major_id) REFERENCES student_majors(id) ON DELETE CASCADE
 );

CREATE TABLE subjects (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  faculty_id BIGINT,
  department_id BIGINT,
  subject_code VARCHAR(20) UNIQUE NOT NULL,
  subject_name VARCHAR(255) NOT NULL,
  credits INT NOT NULL,
  lecture_hours INT,
  practice_hours INT,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE SET NULL,
  FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
);

CREATE TABLE subject_prerequisites (
  subject_id BIGINT NOT NULL,
  prerequisite_subject_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT now(),
  PRIMARY KEY (subject_id, prerequisite_subject_id),
  FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
  FOREIGN KEY (prerequisite_subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);

CREATE TABLE semesters (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  semester_name VARCHAR(50) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE fees (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  student_id BIGINT NOT NULL,
  semester_id INT NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  due_date DATE,
  status VARCHAR(20) NOT NULL DEFAULT 'UNPAID' CHECK (status IN ('UNPAID','PAID','OVERDUE')),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
  FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE RESTRICT
);

CREATE TABLE study_program_subjects (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  study_program_id BIGINT NOT NULL,
  subject_id BIGINT NOT NULL,
  semester_id BIGINT NOT NULL,

  elective_group VARCHAR(50),
  is_required BOOLEAN DEFAULT TRUE,

  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),

  UNIQUE(study_program_id, subject_id),

  FOREIGN KEY (study_program_id) REFERENCES study_programs(id) ON DELETE CASCADE,
  FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
  FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE SET NULL
);

CREATE TABLE course_classes (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  lecturer_id BIGINT,
  subject_id BIGINT NOT NULL,
  semester_id BIGINT NOT NULL,
  class_code VARCHAR(20) NOT NULL,
  class_name VARCHAR(100) NOT NULL,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (lecturer_id) REFERENCES lecturers(id) ON DELETE SET NULL,
  FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE SET NULL,
  FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE SET NULL
);

CREATE TABLE student_course_classes (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  student_id BIGINT NOT NULL,
  course_class_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT now(),
  UNIQUE (student_id, course_class_id),
  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
  FOREIGN KEY (course_class_id) REFERENCES course_classes(id) ON DELETE CASCADE
);

CREATE TABLE class_schedules (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  course_class_id BIGINT NOT NULL,
  day_of_week INT NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
  start_period INT NOT NULL,
  end_period INT NOT NULL CHECK (end_period >= start_period),
  start_time TIME NOT NULL,
  end_time TIME NOT NULL CHECK (end_time > start_time),
  room varchar(50),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (course_class_id) REFERENCES course_classes(id) ON DELETE CASCADE
);

CREATE TABLE student_subject_results (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  student_id BIGINT NOT NULL,
  subject_id BIGINT NOT NULL,
  semester_id BIGINT NOT NULL,
  credits INT NOT NULL,
  attendance_score DECIMAL(4,2),
  midterm_score DECIMAL(4,2),
  final_score DECIMAL(4,2),
  score_10 DECIMAL(4,2),
  score_4 DECIMAL(3,2),
  letter_grade VARCHAR(2),
  is_pass BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  UNIQUE (student_id, subject_id, semester_id),
  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
  FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE RESTRICT,
  FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE RESTRICT
);

CREATE TABLE student_semester_summaries (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  student_id BIGINT NOT NULL,
  study_program_id BIGINT NOT NULL,
  semester_id BIGINT NOT NULL,
  credits_registered INT NOT NULL,
  credits_passed INT NOT NULL CHECK (credits_registered >= credits_passed),
  
  semester_gpa DECIMAL(4,2) NOT NULL,
  letter_gpa VARCHAR(2),
  
  conduct_score INT NOT NULL,
  
  activity_score DECIMAL(4,2) NOT NULL,
  letter_activity_score VARCHAR(2),
  
  group_contribution DECIMAL(4,2) NOT NULL,
  letter_group_contribution VARCHAR(2),
  
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (study_program_id) REFERENCES study_programs(id) ON DELETE RESTRICT,
  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
  FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE RESTRICT
);

CREATE TABLE exam_schedules (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  subject_id BIGINT NOT NULL,
  semester_id BIGINT NOT NULL,
  exam_date DATE NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL CHECK (end_time > start_time),
  exam_room VARCHAR(50) NOT NULL,
  exam_location VARCHAR(100),
  exam_format VARCHAR(50),
  exam_type VARCHAR(50),
  note VARCHAR(255),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
  FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE
);

CREATE TABLE student_exam_registrations (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  student_id BIGINT NOT NULL,
  exam_schedule_id BIGINT NOT NULL,
  exam_attempt INT NOT NULL,
  attendance_status VARCHAR(20) NOT NULL DEFAULT 'UPCOMING' CHECK (attendance_status IN ('ATTENDED','ABSENT','UPCOMING')),

  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  
  UNIQUE (student_id, exam_schedule_id, exam_attempt),
  
  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
  FOREIGN KEY (exam_schedule_id) REFERENCES exam_schedules(id) ON DELETE RESTRICT
);

CREATE TABLE application_types (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code VARCHAR(100) UNIQUE NOT NULL,
  name VARCHAR(255),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE student_applications (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  student_id BIGINT NOT NULL,
  application_type_id BIGINT NOT NULL,
  content TEXT,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','APPROVED','REJECTED')),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),

  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
  FOREIGN KEY (application_type_id) REFERENCES application_types(id) ON DELETE SET NULL
);

CREATE TABLE application_attachments (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  application_id BIGINT NOT NULL,
  file_key VARCHAR(255) NOT NULL,
  original_filename VARCHAR(255),
  file_size BIGINT,
  created_at TIMESTAMP DEFAULT now(),

  FOREIGN KEY (application_id) REFERENCES student_applications(id) ON DELETE CASCADE
);

CREATE TABLE notification_template (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code VARCHAR(100) UNIQUE NOT NULL,
  name VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE notifications (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  template_id BIGINT,
  title TEXT NOT NULL,
  content TEXT NOT NULL,
  created_by VARCHAR(255),
  target_type VARCHAR(20) NOT NULL DEFAULT 'ALL' CHECK (target_type IN ('ALL', 'CLASS', 'STUDENT_CLASS', 'COURSE_CLASS', 'FACULTY')),
  target_id BIGINT NOT NULL,
  reference_id BIGINT,
  reference_type VARCHAR(225),
  deadline DATE,
  created_at TIMESTAMP DEFAULT now(),

  FOREIGN KEY (template_id) REFERENCES notification_template(id) ON DELETE SET NULL
);

CREATE TABLE notification_read (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  notification_id BIGINT NOT NULL,
  oauth_user_id BIGINT NOT NULL,
  read_at TIMESTAMP DEFAULT now(),

  FOREIGN KEY (notification_id) REFERENCES notifications(id) ON DELETE CASCADE,
  FOREIGN KEY (oauth_user_id) REFERENCES oauth_users(id) ON DELETE CASCADE
);

CREATE TABLE grade_scale (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  min_score DECIMAL(4,2) NOT NULL,
  max_score DECIMAL(4,2) NOT NULL,
  letter_grade VARCHAR(2) NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE news (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  title TEXT,
  excerpt TEXT,
  image_url VARCHAR(255),
  source TEXT,
  publish_date DATE,
  news_url VARCHAR(255),
  created_at TIMESTAMP DEFAULT now()
);



