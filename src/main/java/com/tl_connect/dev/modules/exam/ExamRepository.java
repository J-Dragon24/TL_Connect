package com.tl_connect.dev.modules.exam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.exam.entity.ExamSchedule;
import com.tl_connect.dev.modules.exam.projection.ExamScheduleAdminRow;
import com.tl_connect.dev.modules.exam.projection.ExamScheduleView;
import com.tl_connect.dev.modules.schedule.entity.ClassSchedule;

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
                ser.attendance_status AS attendanceStatus
            FROM student_exam_registrations ser
            JOIN exam_schedules es ON ser.exam_schedule_id = es.id
            JOIN subjects s ON es.subject_id = s.id
            JOIN student_course_classes scc ON ser.student_id = scc.student_id
            JOIN course_classes cc ON scc.course_class_id = cc.id AND cc.subject_id = s.id AND cc.semester_id = es.semester_id
            WHERE ser.student_id = :studentId
            AND es.semester_id = :semesterId
            """, nativeQuery = true)
    List<ExamScheduleView> findExamSchedule(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId);

    @Query(value = """
            SELECT
                es.id AS id,
                s.subject_code AS subjectCode,
                cc.class_code AS classCode,
                es.exam_date AS examDate,
                es.start_time AS startTime,
                es.end_time AS endTime,
                es.exam_room AS examRoom,
                es.exam_location AS examLocation,
                es.exam_format AS examFormat,
                es.exam_type AS examType
            FROM exam_schedules es
            JOIN subjects s ON es.subject_id = s.id
            JOIN course_classes cc ON es.course_class_id = cc.id
            WHERE es.semester_id = :semesterId
            """,
            countQuery = """
                    SELECT
                        COUNT(es.id)
                    FROM exam_schedules es
                    JOIN subjects s ON es.subject_id = s.id
                    JOIN course_classes cc ON es.course_class_id = cc.id
                    WHERE es.semester_id = :semesterId
                    """,
            nativeQuery = true)
    Page<ExamScheduleAdminRow> findAllExamSchedule(@Param("semesterId") Long semesterId, Pageable pageable);

        @Query(value = """
            SELECT
                es.id AS id,
                s.subject_code AS subjectCode,
                cc.class_code AS classCode,
                es.exam_date AS examDate,
                es.start_time AS startTime,
                es.end_time AS endTime,
                es.exam_room AS examRoom,
                es.exam_location AS examLocation,
                es.exam_format AS examFormat,
                es.exam_type AS examType
            FROM exam_schedules es
            JOIN subjects s ON es.subject_id = s.id
            JOIN course_classes cc ON es.course_class_id = cc.id
            WHERE es.semester_id = :semesterId
            AND s.faculty_id = :facultyId
            """,
            countQuery = """
                    SELECT
                        COUNT(es.id)
                    FROM exam_schedules es
                    JOIN subjects s ON es.subject_id = s.id
                    JOIN course_classes cc ON es.course_class_id = cc.id
                    WHERE es.semester_id = :semesterId
                    AND s.faculty_id = :facultyId
                    """,
            nativeQuery = true)
    Page<ExamScheduleAdminRow> findAllExamScheduleByFacultyId(@Param("semesterId") Long semesterId, @Param("facultyId") Long facultyId, Pageable pageable);

    @Query(value = """
        SELECT COUNT(es.id) > 0 FROM exam_schedules es
        WHERE es.semester_id = :semesterId
            AND es.exam_date = :examDate
            AND es.exam_room = :room
            AND es.start_time < :endTime
            AND es.end_time > :startTime
    """, nativeQuery = true)
    boolean existsConflict(
            @Param("examDate") LocalDate examDate,
            @Param("semesterId") Long semesterId,
            @Param("room") String room,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    @Query(value = """
        SELECT COUNT(es.id) > 0 FROM exam_schedules es
        WHERE es.semester_id = :semesterId
            AND es.exam_date = :examDate
            AND es.exam_room = :room
            AND es.start_time < :endTime
            AND es.end_time > :startTime
            AND es.id != :id
    """, nativeQuery = true)
    boolean existsConflictExcludingId(
            @Param("examDate") LocalDate examDate,
            @Param("semesterId") Long semesterId,
            @Param("room") String room,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("id") Long id
    );
}
