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
DROP TABLE IF EXISTS notification_templates CASCADE;
DROP TABLE IF EXISTS notification_read CASCADE;
DROP TABLE IF EXISTS tuition_invoices CASCADE;
DROP TABLE IF EXISTS tuition_invoice_items CASCADE;
DROP TABLE IF EXISTS tuition_fee_configs CASCADE;
DROP TABLE IF EXISTS payment CASCADE;
DROP TABLE IF EXISTS tuition_transactions CASCADE;
DROP TABLE IF EXISTS subject_prerequisite_group_items CASCADE;
DROP TABLE IF EXISTS subject_prerequisite_groups CASCADE;
DROP TABLE IF EXISTS subject_enrollment_conditions CASCADE;
DROP TABLE IF EXISTS student_course_class_logs CASCADE;
DROP TABLE IF EXISTS user_devices CASCADE;
DROP TABLE IF EXISTS notification_targets CASCADE;
DROP TABLE IF EXISTS enrollment_periods CASCADE;
DROP TABLE IF EXISTS feedback_attachments CASCADE;
DROP TABLE IF EXISTS feedback_category CASCADE;
DROP TABLE IF EXISTS feedback CASCADE;
DROP TABLE IF EXISTS attendances CASCADE;


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
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
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
  oauth_user_id BIGINT UNIQUE,
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
  oauth_user_id BIGINT UNIQUE,
  student_class_id BIGINT NOT NULL,
  full_name VARCHAR(255) NOT NULL,
  student_code VARCHAR(20) UNIQUE NOT NULL,
  gender VARCHAR(20) NOT NULL DEFAULT 'NAM' CHECK (gender IN ('NAM', 'NU')),
  date_of_birth DATE NOT NULL,
  avatar_url TEXT,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','SUSPENDED','GRADUATED','DROPPED_OUT','DELETED')),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (oauth_user_id) REFERENCES oauth_users(id) ON DELETE SET NULL,
  FOREIGN KEY (student_class_id) REFERENCES student_classes(id) ON DELETE SET NULL
);

CREATE TABLE academic_advisors (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
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
  relationship VARCHAR (255),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE TABLE identity_cards (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
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
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
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
  training_type VARCHAR(20) NOT NULL DEFAULT 'CHINH_QUY' CHECK (training_type IN ('CHINH_QUY','LIEN_THONG')),
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),

  FOREIGN KEY (major_id) REFERENCES majors(id) ON DELETE RESTRICT
);

CREATE TABLE student_majors (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  student_id BIGINT NOT NULL,
  major_id BIGINT NOT NULL,
  study_program_id BIGINT NOT NULL,
  is_primary BOOLEAN DEFAULT TRUE,
  start_year INT NOT NULL,
  end_year INT NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'STUDYING' CHECK (status IN ('STUDYING', 'GRADUATED', 'DROPPED')),
  UNIQUE(student_id, major_id),
  UNIQUE(student_id, study_program_id),
  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
  FOREIGN KEY (major_id) REFERENCES majors(id) ON DELETE CASCADE,
  FOREIGN KEY (study_program_id) REFERENCES study_programs(id) ON DELETE SET NULL
);

CREATE TABLE academic_infos (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  student_major_id BIGINT UNIQUE NOT NULL,
  cohort VARCHAR(20) NOT NULL,
  position VARCHAR(50),
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
  coefficient DECIMAL(3,2) DEFAULT 1.0,
  lecture_hours INT,
  practice_hours INT,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE SET NULL,
  FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
);

CREATE TABLE subject_prerequisite_groups (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  subject_id BIGINT NOT NULL,
  min_subjects_required INT DEFAULT 1,
  description TEXT,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);

CREATE TABLE subject_prerequisite_group_items (
  group_id BIGINT NOT NULL,
  prerequisite_subject_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT now(),
  PRIMARY KEY (group_id, prerequisite_subject_id),
  FOREIGN KEY (group_id) REFERENCES subject_prerequisite_groups(id) ON DELETE CASCADE,
  FOREIGN KEY (prerequisite_subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);

CREATE TABLE subject_enrollment_conditions (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  subject_id BIGINT NOT NULL,
  condition_type VARCHAR(50) NOT NULL CHECK (condition_type IN ('GPA','TOTAL_CREDITS')),
  condition_value DECIMAL(6,2) NOT NULL,
  condition_operator VARCHAR(5) DEFAULT '>=' CHECK (condition_operator IN ('>=', '>', '=', '<=')),
  description TEXT,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  UNIQUE(subject_id, condition_type),
  FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);

CREATE TABLE semesters (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  semester_name VARCHAR(50) NOT NULL,
  semester_code VARCHAR(20) UNIQUE NOT NULL,
  academic_years varchar(20) NOT NULL,
  semester_number INT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  UNIQUE(academic_years, semester_number),
  CONSTRAINT valid_date_range CHECK (end_date > start_date),
  EXCLUDE USING gist (
      daterange(start_date, end_date, '[]') WITH &&
  )
);

CREATE TABLE study_program_subjects (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  study_program_id BIGINT NOT NULL,
  subject_id BIGINT NOT NULL,
  semester_id BIGINT NOT NULL,

  elective_group VARCHAR(50),
  is_required BOOLEAN NOT NULL DEFAULT TRUE,

  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),

  UNIQUE(study_program_id, subject_id),

  FOREIGN KEY (study_program_id) REFERENCES study_programs(id) ON DELETE CASCADE,
  FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
  FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE RESTRICT
);

CREATE TABLE course_classes (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  lecturer_id BIGINT,
  subject_id BIGINT NOT NULL,
  semester_id BIGINT NOT NULL,
  class_code VARCHAR(20) NOT NULL,
  class_name VARCHAR(100) NOT NULL,
  capacity INT NOT NULL,
  enrolled_count INT NOT NULL DEFAULT 0,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  version BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (lecturer_id) REFERENCES lecturers(id) ON DELETE SET NULL,
  FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE SET NULL,
  FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE RESTRICT
);

CREATE TABLE student_course_classes (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  student_id BIGINT NOT NULL,
  course_class_id BIGINT NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ENROLLED', 'DROPPED', 'REJECTED')),
  subject_id BIGINT NOT NULL,
  semester_id BIGINT NOT NULL,
  is_retake BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  UNIQUE (student_id, course_class_id),
  UNIQUE (student_id, subject_id, semester_id),
  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
  FOREIGN KEY (course_class_id) REFERENCES course_classes(id) ON DELETE CASCADE,
  FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE SET NULL,
  FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE RESTRICT
);

CREATE TABLE student_course_class_logs (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  student_id BIGINT NOT NULL,
  course_class_id BIGINT NOT NULL,
  action VARCHAR(20) NOT NULL DEFAULT 'REJECTED' CHECK (action IN ('ENROLL', 'DROP', 'REJECTED')),
  from_status VARCHAR(20) DEFAULT NULL CHECK (from_status IN ('PENDING', 'ENROLLED', 'DROPPED', 'REJECTED')),
  to_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (to_status IN ('PENDING', 'ENROLLED', 'DROPPED', 'REJECTED')),
  created_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
  FOREIGN KEY (course_class_id) REFERENCES course_classes(id) ON DELETE CASCADE
);

CREATE TABLE tuition_invoices (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  student_id BIGINT NOT NULL,
  semester_id BIGINT NOT NULL,
  due_date DATE NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  final_amount DECIMAL(10,2) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'UNPAID' CHECK (status IN ('UNPAID','PENDING','PAID','OVERDUE','CANCELLED')),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE RESTRICT,
  FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE RESTRICT
);

CREATE TABLE tuition_invoice_items (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  invoice_id BIGINT NOT NULL,
  course_class_id BIGINT NOT NULL,
  price_per_credit DECIMAL(10,2) NOT NULL,
  credits INT NOT NULL,
  coefficient DECIMAL(3,2) DEFAULT 1.0,
  amount DECIMAL(10,2) NOT NULL,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  UNIQUE (invoice_id, course_class_id),
  FOREIGN KEY (invoice_id) REFERENCES tuition_invoices(id) ON DELETE CASCADE,
  FOREIGN KEY (course_class_id) REFERENCES course_classes(id) ON DELETE SET NULL
);

CREATE TABLE tuition_fee_configs (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  academic_year VARCHAR(20) NOT NULL,
  cohort INT NOT NULL,
  base_price_per_credit DECIMAL(10,2) NOT NULL,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  UNIQUE (academic_year, cohort)
);

CREATE TABLE payment (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  invoice_id BIGINT NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  provider VARCHAR(20),
  transaction_code VARCHAR(100) UNIQUE NOT NULL,
  provider_trans_id VARCHAR(100),
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','SUCCESS','FAILED', 'REFUND_PENDING', 'REFUNDED')),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (invoice_id) REFERENCES tuition_invoices(id) ON DELETE RESTRICT
);

CREATE TABLE tuition_transactions (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  student_id BIGINT NOT NULL,
  invoice_id BIGINT,
  amount DECIMAL(10,2) NOT NULL,
  type VARCHAR(20) NOT NULL CHECK (type IN ('TUITION','PAYMENT','REFUND','ADJUSTMENT')),
  reference_id BIGINT,
  reference_type VARCHAR(20),
  description TEXT,
  created_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (invoice_id) REFERENCES tuition_invoices(id) ON DELETE RESTRICT
);

CREATE TABLE class_schedules (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
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
  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE RESTRICT,
  FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE RESTRICT,
  FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE RESTRICT
);

CREATE TABLE student_semester_summaries (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
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
  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE RESTRICT,
  FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE RESTRICT,
  UNIQUE (student_id, study_program_id, semester_id)
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
  exam_type VARCHAR(50) NOT NULL CHECK (exam_type IN ('MIDTERM','FINAL')),
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
  FOREIGN KEY (exam_schedule_id) REFERENCES exam_schedules(id) ON DELETE CASCADE
);

CREATE TABLE application_types (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code VARCHAR(100) UNIQUE NOT NULL,
  name VARCHAR(255),
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
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

  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE RESTRICT,
  FOREIGN KEY (application_type_id) REFERENCES application_types(id) ON DELETE SET NULL
);

CREATE TABLE application_attachments (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  application_id BIGINT NOT NULL,
  file_key VARCHAR(255) NOT NULL,
  original_filename VARCHAR(255),
  file_size BIGINT,
  resource_type VARCHAR(20),
  created_at TIMESTAMP DEFAULT now(),

  FOREIGN KEY (application_id) REFERENCES student_applications(id) ON DELETE CASCADE
);

CREATE TABLE notification_templates (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  code VARCHAR(100) UNIQUE NOT NULL,
  name VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE user_devices (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  oauth_user_id BIGINT NOT NULL,
  device_id VARCHAR(255) UNIQUE NOT NULL,
  fcm_token TEXT UNIQUE NOT NULL,
  platform VARCHAR(20),
  last_used_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),

  FOREIGN KEY (oauth_user_id) REFERENCES oauth_users(id) ON DELETE CASCADE
);

CREATE TABLE notifications (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  title TEXT NOT NULL,
  content TEXT NOT NULL,
  created_by VARCHAR(255) DEFAULT 'SYSTEM' CHECK (created_by IN ('SYSTEM', 'FACULTY', 'LECTURER')),
  target_type VARCHAR(20) NOT NULL DEFAULT 'GLOBAL' CHECK (target_type IN ('GLOBAL','FACULTY','STUDENT_CLASS','COURSE_CLASS', 'STUDENT')),
  reference_type VARCHAR(20) CHECK (reference_type IN ('TUITION','EXAM_SCHEDULE')),
  is_important BOOLEAN DEFAULT FALSE,
  deadline DATE,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE notification_targets (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    notification_id BIGINT NOT NULL,
    target_id BIGINT NOT NULL,

    FOREIGN KEY (notification_id) REFERENCES notifications(id) ON DELETE CASCADE
);

CREATE TABLE notification_read (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  notification_id BIGINT NOT NULL,
  oauth_user_id BIGINT NOT NULL,
  read_at TIMESTAMP DEFAULT now(),

  UNIQUE (notification_id, oauth_user_id),

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
  image_key VARCHAR(255),
  source TEXT,
  publish_date DATE,
  news_url VARCHAR(255),
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE enrollment_periods (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  semester_id BIGINT NOT NULL,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP NOT NULL,
  max_credits INT NOT NULL DEFAULT 18,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now(),
  CONSTRAINT valid_time_range CHECK (end_time > start_time),
  CONSTRAINT no_overlap_period
      EXCLUDE USING gist (
          tsrange(start_time, end_time, '[)') WITH &&
      ),
  FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE RESTRICT
);

CREATE TABLE feedback_category (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE,
  description TEXT,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE feedback (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  oauth_user_id BIGINT,
  title TEXT NOT NULL,
  category_id BIGINT,
  content TEXT NOT NULL,
  app_version VARCHAR(20),
  device_info TEXT,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','IN_PROGRESS','RESOLVED','REJECTED')),
  created_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (oauth_user_id) REFERENCES oauth_users(id) ON DELETE SET NULL,
  FOREIGN KEY (category_id) REFERENCES feedback_category(id) ON DELETE SET NULL
);


CREATE TABLE feedback_attachments (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  feedback_id BIGINT NOT NULL,
  file_key VARCHAR(255) NOT NULL,
  original_filename VARCHAR(255),
  file_size BIGINT,
  resource_type VARCHAR(20),
  created_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (feedback_id) REFERENCES feedback(id) ON DELETE CASCADE
);


CREATE TABLE attendances (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  course_class_id BIGINT NOT NULL,
  session_id VARCHAR(255) NOT NULL,
  student_id BIGINT NOT NULL,
  check_in_time TIMESTAMP NOT NULL,
  created_at TIMESTAMP DEFAULT now(),
  FOREIGN KEY (course_class_id) REFERENCES course_classes(id) ON DELETE CASCADE,
  FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);
