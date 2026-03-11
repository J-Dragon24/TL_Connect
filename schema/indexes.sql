/*Student Subject Results indexes*/
CREATE INDEX IF NOT EXISTS idx_student_subject_results_student_id ON student_subject_results(student_id);

/*Student Semester Summaries indexes*/
CREATE INDEX IF NOT EXISTS idx_student_semester_summaries_student_id ON student_semester_summaries(student_id);

/*OAuth Users indexes*/
CREATE INDEX IF NOT EXISTS idx_oauth_users_user_uuid ON oauth_users(user_uuid);

/*Student Exam Registrations indexes*/
CREATE INDEX IF NOT EXISTS idx_student_exam_registrations_student_id ON student_exam_registrations(student_id);

/*Exam Schedules indexes*/
CREATE INDEX IF NOT EXISTS idx_exam_schedules_semester_id ON exam_schedules(semester_id);

/*Notifications indexes*/
CREATE INDEX IF NOT EXISTS idx_notifications_type_target ON notifications(target_type, target_id);

/*Class Schedules indexes*/
CREATE INDEX IF NOT EXISTS idx_class_schedules_course_class_day ON class_schedules(course_class_id, day_of_week);

/*Course Classes indexes*/
CREATE INDEX IF NOT EXISTS idx_course_classes_semester_id ON course_classes(semester_id);

/*Student Majors indexes*/
CREATE INDEX IF NOT EXISTS idx_student_majors_student_id_primary ON student_majors(student_id, is_primary);

/*Health Insurances indexes*/
CREATE INDEX IF NOT EXISTS idx_health_insurances_student_id ON health_insurances(student_id);


/*Students indexes*/
CREATE INDEX IF NOT EXISTS idx_students_student_class_id ON students(student_class_id);