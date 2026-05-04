package com.tl_connect.dev.modules.academic_result.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.academic_result.entity.StudentSemesterSummary;
import com.tl_connect.dev.modules.academic_result.projection.SemesterSummaryRow;
import com.tl_connect.dev.modules.academic_result.projection.SemesterSummaryView;

@Repository
public interface StudentSemesterSummaryRepository extends JpaRepository<StudentSemesterSummary, Long> {
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
    int sumTotalCredits(@Param("studentId") Long studentId,
                    @Param("studyProgramId") Long studyProgramId);

    @Query(value = """
            SELECT
                COALESCE(SUM(sss.semester_gpa * sss.credits_passed)
                /
                NULLIF(SUM(sss.credits_passed)::decimal, 0), 0)
            FROM student_semester_summaries sss
            JOIN study_programs tp ON sss.study_program_id = tp.id
            WHERE sss.student_id = :studentId
            AND tp.id = :studyProgramId
                """, nativeQuery = true)
    BigDecimal calculateCumulativeGpa(@Param("studentId") Long studentId,
            @Param("studyProgramId") Long studyProgramId);

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
}
