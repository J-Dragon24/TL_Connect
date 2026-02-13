package com.tl_connect.dev.modules.training_program;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.training_program.entity.TrainingProgram;
import com.tl_connect.dev.modules.training_program.projection.SubjectPrerequisiteRow;
import com.tl_connect.dev.modules.training_program.projection.TrainingProgramHeaderView;
import com.tl_connect.dev.modules.training_program.projection.TrainingProgramSubjectRow;

@Repository
public interface TrainingProgramRepository extends JpaRepository<TrainingProgram, Long> {

    @Query(value = """
            SELECT
                tp.id AS id,
                tp.training_program_name AS trainingProgramName,
                tp.year_start AS yearStart,
                tp.total_credits AS totalCredits,
                m.major_code AS majorCode,
                m.major_name AS majorName,
                f.faculty_name AS faculty
            FROM students s
            JOIN student_classes sc ON s.student_class_id = sc.id
            JOIN majors m ON sc.major_id = m.id
            JOIN training_programs tp ON m.id = tp.major_id
            JOIN faculties f ON m.faculty_id = f.id
            WHERE s.id = :studentId
            """, nativeQuery = true)
    Optional<TrainingProgramHeaderView> findTrainingProgramHeaderByStudentId(@Param("studentId") Long studentId);

    @Query(value = """
            SELECT
                sem.id AS semesterId,
                sem.semester_name AS semesterName,
                sem.start_date AS semesterStartDate,
                sem.end_date AS semesterEndDate,
                sub.id AS subjectId,
                sub.subject_code AS subjectCode,
                sub.subject_name AS subjectName,
                sub.credits AS credits,
                tps.is_required AS isRequired,
                tps.elective_group AS electiveGroup,
                sub.lecture_hours AS lectureHours,
                sub.practice_hours AS practiceHours,
                f.faculty_name AS faculty,
                d.department_name AS department
            FROM training_program_subjects tps
            JOIN subjects sub ON tps.subject_id = sub.id
            LEFT JOIN faculties f ON sub.faculty_id = f.id
            LEFT JOIN departments d ON sub.department_id = d.id
            JOIN semesters sem ON tps.semester_id = sem.id
            WHERE tps.program_id = :programId
            ORDER BY sem.id, sub.subject_code
            """, nativeQuery = true)
    List<TrainingProgramSubjectRow> findSubjectsByProgramId(@Param("programId") Long programId);

    @Query(value = """
            SELECT
                tps.subject_id AS subjectId,
                sp.prerequisite_subject_id AS prerequisiteSubjectId,
                s.subject_code AS prerequisiteSubjectCode,
                s.subject_name AS prerequisiteSubjectName
            FROM training_program_subjects tps
            JOIN subject_prerequisites sp ON tps.subject_id = sp.subject_id
            JOIN subjects s ON sp.prerequisite_subject_id = s.id
            WHERE tps.program_id = :programId
            """, nativeQuery = true)
    List<SubjectPrerequisiteRow> findSubjectPrerequisitesByProgramId(@Param("programId") Long programId);
}
