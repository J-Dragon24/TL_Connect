package com.tl_connect.dev.modules.academic_result.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.academic_result.entity.StudentSubjectResult;
import com.tl_connect.dev.modules.academic_result.projection.SubjectResultAdmRow;
import com.tl_connect.dev.modules.academic_result.projection.SubjectResultRow;

import java.util.List;
import java.util.Set;

@Repository
public interface StudentSubjectResultRepository extends JpaRepository<StudentSubjectResult, Long> {
    @Query(value = """
                    SELECT
                        sem.semester_name AS semester,
                        sub.subject_code AS subjectCode,
                        sub.subject_name AS subjectName,
                        ssr.credits AS credits,
                        ssr.attendance_score AS attendanceScore,
                        ssr.midterm_score AS midtermScore,
                        ssr.final_score AS finalScore,
                        ssr.score_10 AS score10,
                        ssr.score_4 AS score4,
                        ssr.letter_grade AS letterGrade,
                        ssr.is_pass AS isPass
                    FROM student_subject_results ssr
                    JOIN subjects sub ON ssr.subject_id = sub.id
                    JOIN semesters sem ON ssr.semester_id = sem.id
                    JOIN study_programs sp ON sp.study_program_code = :studyProgramCode
                    JOIN study_program_subjects sps ON sps.subject_id = ssr.subject_id 
                            AND sps.study_program_id = sp.id
                    WHERE ssr.student_id = :studentId
                    """, nativeQuery = true)
    List<SubjectResultRow> findSubjectResultByStudentIdAndStudyProgramCode(@Param("studentId") Long studentId,
                    @Param("studyProgramCode") String studyProgramCode);

    @Query(value = """
        SELECT
            s.id AS studentId,
            ssr.id AS studentSubjectResultId,
            s.student_code AS studentCode,
            s.full_name AS studentName,
            sm.start_year AS startYear,
            m.major_name AS majorName,
            sp.study_program_code AS studyProgramCode,
            sp.study_program_name AS studyProgramName,
            sem.semester_name AS semester,
            sub.subject_code AS subjectCode,
            sub.subject_name AS subjectName,
            ssr.credits AS credits,
            ssr.attendance_score AS attendanceScore,
            ssr.midterm_score AS midtermScore,
            ssr.final_score AS finalScore,
            ssr.score_10 AS score10,
            ssr.score_4 AS score4,
            ssr.letter_grade AS letterGrade,
            ssr.is_pass AS isPass
        FROM student_subject_results ssr
        JOIN students s ON s.id = ssr.student_id
        JOIN subjects sub ON ssr.subject_id = sub.id
        JOIN semesters sem ON ssr.semester_id = sem.id
        JOIN student_majors sm ON sm.student_id = s.id
        JOIN majors m ON m.id = sm.major_id
        JOIN faculties f ON f.id = m.faculty_id
        JOIN study_programs sp ON sp.id = sm.study_program_id
        WHERE (:facultyCode IS NULL OR f.faculty_code = :facultyCode)
        ORDER BY s.student_code
        """,
        countQuery = """
            SELECT COUNT(*)
            FROM student_subject_results ssr
            JOIN students s ON s.id = ssr.student_id
            JOIN subjects sub ON ssr.subject_id = sub.id
            JOIN semesters sem ON ssr.semester_id = sem.id
            JOIN student_majors sm ON sm.student_id = s.id
            JOIN majors m ON m.id = sm.major_id
            JOIN study_programs sp ON sp.id = sm.study_program_id
            JOIN faculties f ON f.id = m.faculty_id
            WHERE (:facultyCode IS NULL OR f.faculty_code = :facultyCode)
        """,
        nativeQuery = true)
    Page<SubjectResultAdmRow> findSubjectResult(Pageable pageable, String facultyCode);

    @Query(value = """
        SELECT ssr.is_pass
        FROM student_subject_results ssr
        JOIN semesters sem ON ssr.semester_id = sem.id
        WHERE ssr.student_id = :studentId
            AND ssr.subject_id = :subjectId
        ORDER BY sem.start_date DESC
        LIMIT 1
        """, nativeQuery = true)
    Boolean getLatestSubjectResult(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);

    @Query(value = """
        SELECT *
        FROM student_subject_results
        WHERE student_id = :studentId
        """, nativeQuery = true)
    List<StudentSubjectResult> findAllByStudentId(@Param("studentId") Long studentId);
}
