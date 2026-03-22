CREATE OR REPLACE FUNCTION sync_student_course_class_fields()
RETURNS trigger AS $$
BEGIN
  SELECT subject_id, semester_id
  INTO NEW.subject_id, NEW.semester_id
  FROM course_classes
  WHERE id = NEW.course_class_id;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_sync_student_course_class
BEFORE INSERT ON student_course_classes
FOR EACH ROW
EXECUTE FUNCTION sync_student_course_class_fields();