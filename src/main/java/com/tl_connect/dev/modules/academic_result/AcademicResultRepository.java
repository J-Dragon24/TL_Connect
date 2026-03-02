package com.tl_connect.dev.modules.academic_result;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.academic_result.entity.StudentSubjectResult;
import com.tl_connect.dev.modules.academic_result.projection.SemesterSummaryView;
import com.tl_connect.dev.modules.academic_result.projection.SubjectResultRow;

import java.util.List;

@Repository
public interface AcademicResultRepository extends JpaRepository<StudentSubjectResult, Long> {
        @Query(value = """
                        SELECT
                            sem.semester_name AS semester,
                            sub.subject_code AS subjectCode,
                            sub.subject_name AS subjectName,
                            ssr.credits AS credits,
                            ssr.score_10 AS score10,
                            ssr.score_4 AS score4,
                            ssr.letter_grade AS letterGrade,
                            ssr.is_pass AS isPass
                        FROM student_subject_results ssr
                        JOIN subjects sub ON ssr.subject_id = sub.id
                        JOIN semesters sem ON ssr.semester_id = sem.id
                        JOIN study_programs tp ON tp.study_program_code = :studyProgramCode
                        JOIN study_program_subjects tps ON tps.subject_id = ssr.subject_id 
                                AND tps.study_program_id = tp.id
                        WHERE ssr.student_id = :studentId
                        """, nativeQuery = true)
        List<SubjectResultRow> findSubjectResult(@Param("studentId") Long studentId,
                        @Param("studyProgramCode") String studyProgramCode);

        @Query(value = """
                        SELECT
                            sem.semester_name AS semester,
                            sss.credits_registered AS credits,
                            sss.credits_passed AS creditsPassed,
                            sss.semester_gpa AS semesterGpa,
                            sss.conduct_score AS conductScore,
                            SUM(sss.semester_gpa * sss.credits_passed)
                            OVER (ORDER BY sem.id)
                            /
                            SUM(sss.credits_passed)
                            OVER (ORDER BY sem.id)
                            AS cumulativeGpa
                        FROM student_semester_summaries sss
                        JOIN semesters sem ON sss.semester_id = sem.id
                        JOIN study_programs tp ON sss.study_program_id = tp.id
                        WHERE sss.student_id = :studentId
                        AND tp.study_program_code = :studyProgramCode
                        """, nativeQuery = true)
        List<SemesterSummaryView> findSemesterSummary(@Param("studentId") Long studentId,
                        @Param("studyProgramCode") String studyProgramCode);
}
