package com.tl_connect.dev.result;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.result.entity.StudentSubjectResult;
import com.tl_connect.dev.result.projection.SemesterSummaryView;
import com.tl_connect.dev.result.projection.SubjectResultRow;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<StudentSubjectResult, Long> {
    @Query("""
            SELECT
                s.semesterName AS semester,
                sub.subjectCode AS subjectCode,
                sub.subjectName AS subjectName,
                ssr.credits AS credits,
                ssr.score10 AS score10,
                ssr.score4 AS score4,
                ssr.letterGrade AS letterGrade,
                ssr.isPass AS isPass
            FROM StudentSubjectResult ssr
            JOIN Subject sub ON ssr.subjectId = sub.id
            JOIN Semester s ON ssr.semesterId = s.id
            WHERE ssr.studentId = :studentId
            """)
    Optional<List<SubjectResultRow>> findSubjectResult(@Param("studentId") Long studentId);

    @Query("""
            SELECT
                s.semesterName AS semester,
                ssr.creditsRegistered AS credits,
                ssr.creditsPassed AS creditsPassed,
                ssr.semesterGpa AS semesterGpa,
                ssr.conductScore AS conductScore
            FROM StudentSemesterSummary sss 
            JOIN Semester s ON sss.semesterId = s.id
            WHERE sss.studentId = :studentId
            """)
    Optional<List<SemesterSummaryView>> findSemesterSummary(@Param("studentId") Long studentId);
}
