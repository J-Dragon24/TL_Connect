package com.tl_connect.dev.modules.exam;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.exam.entity.ExamSchedule;
import com.tl_connect.dev.modules.exam.projection.ExamScheduleView;

import org.springframework.data.repository.query.Param;

@Repository
public interface ExamRepository extends JpaRepository<ExamSchedule, Long> {
    @Query(value = """
            SELECT
                s.subject_code AS subjectCode,
                s.subject_name AS subjectName,
                cc.class_code AS classCode,
                es.exam_date AS examDate,
                es.start_time AS startTime,
                es.end_time AS endTime,
                es.exam_room AS examRoom,
                es.exam_location AS examLocation,
                es.exam_format AS examFormat,
                es.exam_type AS examType,
                ser.exam_attempt AS examAttempt,
                ser.attendance_status AS attendanceStatus,
                ser.exam_status AS examStatus
            FROM student_exam_registrations ser
            JOIN exam_schedules es ON ser.exam_schedule_id = es.id
            JOIN course_classes cc ON es.course_class_id = cc.id
            JOIN subjects s ON cc.subject_id = s.id
            WHERE ser.student_id = :studentId
            AND es.semester_id = :semesterId
            """, nativeQuery = true)
    List<ExamScheduleView> findExamSchedule(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId);
}
