package com.tl_connect.dev.modules.attendance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.attendance.entity.Attendance;
import com.tl_connect.dev.modules.attendance.projection.ClassAttendanceSummaryView;
import com.tl_connect.dev.modules.attendance.projection.StudentAttendanceSummaryRow;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsBySessionIdAndStudentId(String sessionId, Long studentId);

    @Query(value = """
            SELECT
                cc.id,
                cc.class_code,
                cc.class_name,
                COUNT(DISTINCT a.session_id) AS total_sessions
            FROM attendances a
            JOIN course_classes cc ON a.course_class_id = cc.id
            WHERE a.course_class_id = :courseClassId
            GROUP BY cc.id, cc.class_code, cc.class_name
            """, nativeQuery = true)
    Optional<ClassAttendanceSummaryView> getClassAttendanceSummary(@Param("courseClassId") Long courseClassId);

    @Query(value = """
            SELECT
                s.id,
                s.full_name as studentName,
                s.student_code as studentCode,
                COUNT(DISTINCT a.session_id) AS presentCount
            FROM student_course_classes scc
            JOIN students s ON scc.student_id = s.id
            LEFT JOIN attendances a ON a.student_id = s.id AND a.course_class_id = :courseClassId
            WHERE scc.course_class_id = :courseClassId
            GROUP BY s.id, s.student_code, s.full_name
            """, nativeQuery = true)
    List<StudentAttendanceSummaryRow> getStudentAttendanceSummary(@Param("courseClassId") Long courseClassId);
}
