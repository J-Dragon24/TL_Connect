/*Student Subject Results indexes*/
CREATE INDEX IF NOT EXISTS idx_student_subject_results_student_id ON student_subject_results(student_id);

/*Student Semester Summaries indexes*/
CREATE INDEX IF NOT EXISTS idx_student_semester_summaries_student_id ON student_semester_summaries(student_id);
CREATE INDEX IF NOT EXISTS idx_student_semester_summaries_study_program_id ON student_semester_summaries(study_program_id);
CREATE INDEX IF NOT EXISTS idx_student_semester_summaries_semester_id ON student_semester_summaries(semester_id);

/*OAuth Users indexes*/
CREATE INDEX IF NOT EXISTS idx_oauth_users_user_uuid ON oauth_users(user_uuid);

/*Exam Schedules indexes*/
CREATE INDEX IF NOT EXISTS idx_exam_schedules_semester_id ON exam_schedules(semester_id);
CREATE INDEX IF NOT EXISTS idx_exam_schedules_subject_id ON exam_schedules(subject_id);

/*Notifications indexes*/
CREATE INDEX IF NOT EXISTS idx_notifications_type_target ON notifications(target_type, target_id);

/*Class Schedules indexes*/
CREATE INDEX IF NOT EXISTS idx_class_schedules_course_class_day ON class_schedules(course_class_id, day_of_week);

/*Course Classes indexes*/
CREATE INDEX IF NOT EXISTS idx_course_classes_semester_id ON course_classes(semester_id);
CREATE INDEX IF NOT EXISTS idx_course_classes_subject_id ON course_classes(subject_id);
CREATE INDEX IF NOT EXISTS idx_course_classes_lecturer_id ON course_classes(lecturer_id);

/*Student Majors indexes*/
CREATE INDEX IF NOT EXISTS idx_student_majors_student_id_primary ON student_majors(student_id, is_primary);
CREATE INDEX IF NOT EXISTS idx_student_majors_major_id ON student_majors(major_id);
CREATE INDEX IF NOT EXISTS idx_student_majors_program_id ON student_majors(study_program_id);

/*Health Insurances indexes*/
CREATE INDEX IF NOT EXISTS idx_health_insurances_student_id ON health_insurances(student_id);

/*Students indexes*/
CREATE INDEX IF NOT EXISTS idx_students_student_class_id ON students(student_class_id);

/*User Roles indexes*/
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);

/*Departments indexes*/
CREATE INDEX IF NOT EXISTS idx_departments_faculty_id ON departments(faculty_id);

/*Majors indexes*/
CREATE INDEX IF NOT EXISTS idx_majors_faculty_id ON majors(faculty_id);

/*Student Classes indexes*/
CREATE INDEX IF NOT EXISTS idx_student_classes_major_id ON student_classes(major_id);

/*Lecturers indexes*/
CREATE INDEX IF NOT EXISTS idx_lecturers_department_id ON lecturers(department_id);

/*Academic Advisors indexes*/
CREATE INDEX IF NOT EXISTS idx_academic_advisors_lecturer_id ON academic_advisors(lecturer_id);

/*Study Programs indexes*/
CREATE INDEX IF NOT EXISTS idx_study_programs_major_id ON study_programs(major_id);

/*Subjects indexes*/
CREATE INDEX IF NOT EXISTS idx_subjects_faculty_id ON subjects(faculty_id);
CREATE INDEX IF NOT EXISTS idx_subjects_department_id ON subjects(department_id);

/*Subject prerequisite group indexes*/
CREATE INDEX IF NOT EXISTS idx_subject_prerequisite_groups_subject_id ON subject_prerequisite_groups(subject_id);

/*Subject prerequisite group items indexes*/
CREATE INDEX IF NOT EXISTS idx_subject_prerequisite_group_items_prerequisite_subject_id ON subject_prerequisite_group_items(prerequisite_subject_id);

/*Subject enrollment conditions indexes*/
CREATE INDEX IF NOT EXISTS idx_subject_enrollment_conditions_subject_id ON subject_enrollment_conditions(subject_id);

/*Study Program Subjects indexes*/
CREATE INDEX IF NOT EXISTS idx_study_program_subjects_subject_id ON study_program_subjects(subject_id);

/*Student Course Classes indexes*/
CREATE INDEX IF NOT EXISTS idx_student_course_classes_course_class_id ON student_course_classes(course_class_id);

/*Tuition Invoices indexes*/
CREATE INDEX IF NOT EXISTS idx_tuition_invoices_student_id ON tuition_invoices(student_id);
CREATE INDEX IF NOT EXISTS idx_tuition_invoices_semester_id ON tuition_invoices(semester_id);

/*Payments indexes*/
CREATE INDEX IF NOT EXISTS idx_payment_invoice_id ON payment(invoice_id);

/*Student applications indexes*/
CREATE INDEX IF NOT EXISTS idx_student_applications_student_id_application_type_id ON student_applications(student_id, application_type_id);

/*Application attachments indexes*/
CREATE INDEX IF NOT EXISTS idx_application_attachments_application_id ON application_attachments(application_id);

/*User devices indexes*/
CREATE INDEX IF NOT EXISTS idx_user_devices_oauth_user_id ON user_devices(oauth_user_id);

/*Notifications indexes*/
CREATE INDEX IF NOT EXISTS idx_notifications_created
ON notifications(created_at DESC);

/*News indexes*/
CREATE INDEX IF NOT EXISTS idx_news_publish_date
ON news(publish_date DESC);

/*Notification targets indexes*/
CREATE INDEX IF NOT EXISTS idx_notification_targets_target_id 
ON notification_targets(target_id);

CREATE INDEX IF NOT EXISTS idx_notification_targets_notification_id 
ON notification_targets(notification_id);
