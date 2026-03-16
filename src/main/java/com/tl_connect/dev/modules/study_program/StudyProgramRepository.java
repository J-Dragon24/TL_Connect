package com.tl_connect.dev.modules.study_program;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.core.common.enums.TrainingType;
import com.tl_connect.dev.modules.study_program.entity.StudyProgram;
import com.tl_connect.dev.modules.study_program.projection.SubjectPrerequisiteRow;
import com.tl_connect.dev.modules.study_program.projection.StudyProgramHeaderView;
import com.tl_connect.dev.modules.study_program.projection.StudyProgramRow;
import com.tl_connect.dev.modules.study_program.projection.StudyProgramSubjectRow;

@Repository
public interface StudyProgramRepository extends JpaRepository<StudyProgram, Long> {

    @Query(value = """
            SELECT
                s.student_code AS studentCode,
                sp.study_program_code AS studyProgramCode,
                sp.study_program_name AS studyProgramName,
                sm.is_primary AS isPrimary,
                sm.start_year AS startYear
            FROM students s
            JOIN student_majors sm ON s.id = sm.student_id
            JOIN study_programs sp ON sp.id = sm.study_program_id
            WHERE s.id = :studentId
            """, nativeQuery = true)
    List<StudyProgramRow> findAllStudyProgram(@Param("studentId") Long studentId);

    @Query(value = """
            SELECT
                sp.id AS id,
                sp.study_program_name AS studyProgramName,
                sp.start_year AS startYear,
                sp.total_credits AS totalCredits,
                m.major_code AS majorCode,
                m.major_name AS majorName,
                f.faculty_name AS faculty
            FROM study_programs sp
            JOIN majors m ON sp.major_id = m.id
            JOIN faculties f ON m.faculty_id = f.id
            WHERE sp.study_program_code = :studyProgramCode
            """, nativeQuery = true)
    Optional<StudyProgramHeaderView> findStudyProgramHeader(@Param("studyProgramCode") String studyProgramCode);

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
                sps.is_required AS isRequired,
                sps.elective_group AS electiveGroup,
                sub.lecture_hours AS lectureHours,
                sub.practice_hours AS practiceHours,
                f.faculty_name AS faculty,
                d.department_name AS department
            FROM study_program_subjects sps
            JOIN subjects sub ON sps.subject_id = sub.id
            LEFT JOIN faculties f ON sub.faculty_id = f.id
            LEFT JOIN departments d ON sub.department_id = d.id
            JOIN semesters sem ON sps.semester_id = sem.id
            WHERE sps.study_program_id = :studyProgramId
            ORDER BY sem.id, sub.subject_code
            """, nativeQuery = true)
    List<StudyProgramSubjectRow> findSubjectsByProgramId(@Param("studyProgramId") Long studyProgramId);

    @Query(value = """
            SELECT
                sps.subject_id AS subjectId,
                sp.prerequisite_subject_id AS prerequisiteSubjectId,
                s.subject_code AS prerequisiteSubjectCode,
                s.subject_name AS prerequisiteSubjectName
            FROM study_program_subjects sps
            JOIN subject_prerequisites sp ON sps.subject_id = sp.subject_id
            JOIN subjects s ON sp.prerequisite_subject_id = s.id
            WHERE sps.study_program_id = :studyProgramId
            """, nativeQuery = true)
    List<SubjectPrerequisiteRow> findSubjectPrerequisitesByProgramId(@Param("studyProgramId") Long studyProgramId);

    Optional<StudyProgram> findByMajorIdAndTrainingTypeAndStartYear(
            Long majorId, TrainingType trainingType, Integer startYear);
}
