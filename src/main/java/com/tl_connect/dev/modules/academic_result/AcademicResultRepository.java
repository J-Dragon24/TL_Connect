package com.tl_connect.dev.modules.academic_result;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.academic_result.entity.StudentSubjectResult;
import com.tl_connect.dev.modules.academic_result.projection.SemesterSummaryRow;
import com.tl_connect.dev.modules.academic_result.projection.SemesterSummaryView;
import com.tl_connect.dev.modules.academic_result.projection.SubjectResultAdmRow;
import com.tl_connect.dev.modules.academic_result.projection.SubjectResultRow;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AcademicResultRepository extends JpaRepository<StudentSubjectResult, Long> {
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
            JOIN study_programs sp ON sp.id = sm.study_program_id
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
            """,
            nativeQuery = true)
        Page<SubjectResultAdmRow> findSubjectResult(Pageable pageable);

                @Query(value = """
                        SELECT
                            sem.semester_name AS semester,
                            sss.credits_registered AS creditsRegistered,
                            sss.credits_passed AS creditsPassed,
                            sss.semester_gpa AS semesterGpa,
                            sss.conduct_score AS conductScore
                        FROM student_semester_summaries sss
                        JOIN semesters sem ON sss.semester_id = sem.id
                        JOIN study_programs sp ON sss.study_program_id = sp.id
                        WHERE sss.student_id = :studentId
                        AND sp.study_program_code = :studyProgramCode
                        """, nativeQuery = true)
        List<SemesterSummaryView> findSemesterSummaryByStudentIdAndStudyProgramCode(@Param("studentId") Long studentId,
                        @Param("studyProgramCode") String studyProgramCode);

        @Query(value = """
                        SELECT
                            sss.student_id AS studentId,
                            sem.semester_name AS semester,
                            sp.study_program_code AS studyProgramCode,
                            sss.credits_registered AS creditsRegistered,
                            sss.credits_passed AS creditsPassed,
                            sss.semester_gpa AS semesterGpa,
                            sss.conduct_score AS conductScore
                        FROM student_semester_summaries sss
                        JOIN semesters sem ON sss.semester_id = sem.id
                        JOIN study_programs sp ON sss.study_program_id = sp.id
                        WHERE sss.student_id IN :studentIds
                        """, nativeQuery = true)
        List<SemesterSummaryRow> findSemesterSummaryByStudentIds(@Param("studentIds") List<Long> studentIds);

        @Query(value = """
                        SELECT
                            COALESCE(SUM(sss.credits_passed), 0) AS creditsPassed
                        FROM student_semester_summaries sss
                        JOIN study_programs tp ON sss.study_program_id = tp.id
                        WHERE sss.student_id = :studentId
                        AND tp.id = :studyProgramId
                        """, nativeQuery = true)
        Integer findCreditsPassed(@Param("studentId") Long studentId,
                        @Param("studyProgramId") Long studyProgramId);

        @Query(value = """
                    SELECT
                        SUM(sss.semester_gpa * sss.credits_passed)
                        /
                        NULLIF(SUM(sss.credits_passed)::decimal, 0) AS cumulativeGpa
                    FROM student_semester_summaries sss
                    JOIN study_programs tp ON sss.study_program_id = tp.id
                    WHERE sss.student_id = :studentId
                    AND tp.id = :studyProgramId
                        """, nativeQuery = true)
        BigDecimal findCurrentSemesterGpa(@Param("studentId") Long studentId,
                        @Param("studyProgramId") Long studyProgramId);

        @Query(value = """
            SELECT ssr.is_pass
            FROM student_subject_results ssr
            JOIN semesters sem ON ssr.semester_id = sem.id
            WHERE ssr.student_id = :studentId
                AND ssr.subject_id = :subjectId
            ORDER BY sem.start_date DESC
            LIMIT 1
            """, nativeQuery = true)
        @Cacheable("latest_subject_result")
        Boolean getLatestSubjectResult(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);
}
