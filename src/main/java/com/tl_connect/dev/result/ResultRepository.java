package com.tl_connect.dev.result;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.result.entity.StudentSubjectResult;
import com.tl_connect.dev.result.projection.SemesterSummaryView;
import com.tl_connect.dev.result.projection.SubjectResultRow;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<StudentSubjectResult, Long> {
    @Query(value = """
            SELECT
                sem.semester_name AS semester,
                sub.subject_code AS subjectCode,
                sub.subject_name AS subjectName,
                ssr.credits AS credits,
                ssr.score10 AS score10,
                ssr.score4 AS score4,
                ssr.letter_grade AS letterGrade,
                ssr.is_pass AS isPass
            FROM student_subject_results ssr
            JOIN subjects sub ON ssr.subject_id = sub.id
            JOIN semesters sem ON ssr.semester_id = sem.id
            WHERE ssr.student_id = :studentId
            ORDER BY sem.id, sub.subject_code
            """, nativeQuery = true)
    List<SubjectResultRow> findSubjectResult(@Param("studentId") Long studentId);



    @Query(value = """
            SELECT
                sem.semester_name AS semester,
                sss.credits_registered AS credits,
                sss.credits_passed AS creditsPassed,
                sss.semester_gpa AS semesterGpa,
                sss.conduct_score AS conductScore
            FROM student_semester_summaries sss
            JOIN semesters sem ON sss.semester_id = sem.id
            WHERE sss.student_id = :studentId
            ORDER BY sem.id
            """, nativeQuery = true)
    List<SemesterSummaryView> findSemesterSummary(@Param("studentId") Long studentId);
}
