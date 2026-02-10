package com.tl_connect.dev.exam;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import com.tl_connect.dev.exam.entity.ExamSchedule;
import com.tl_connect.dev.exam.projection.ExamScheduleView;

@Repository
public interface ExamRepository extends JpaRepository<ExamSchedule, Long> {
    @Query("""
            SELECT
                s.subjectCode AS subjectCode,
                s.subjectName AS subjectName,
                cc.classCode AS classCode,
                es.examDate AS examDate,
                es.startTime AS startTime,
                es.endTime AS endTime,
                es.examRoom AS examRoom,
                es.examLocation AS examLocation,
                es.examFormat AS examFormat,
                es.examType AS examType,
                ser.examAttempt AS examAttempt,
                ser.attendanceStatus AS attendanceStatus,
                ser.examStatus AS examStatus
            FROM StudentExamRegistration ser
            JOIN ExamSchedule es ON ser.examScheduleId = es.id
            JOIN CourseClass cc ON es.courseClassId = cc.id
            JOIN Subject s ON cc.subjectId = s.id
            WHERE ser.studentId = :studentId AND es.semesterId = :semesterId
            """)
    Optional<List<ExamScheduleView>> 
    
    findExamSchedule(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId);
}
