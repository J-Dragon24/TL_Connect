package com.tl_connect.dev.modules.schedule;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.schedule.entity.ClassSchedule;
import com.tl_connect.dev.modules.schedule.projection.ScheduleRow;

@Repository
public interface ScheduleRepository extends JpaRepository<ClassSchedule, Long> {

    @Query(value = """
            SELECT
                cs.day_of_week AS dayOfWeek,
                cs.start_period AS startPeriod,
                cs.end_period AS endPeriod,
                cs.start_time AS startTime,
                cs.end_time AS endTime,
                cs.room AS room,
                cc.class_code AS classCode,
                s.subject_code AS subjectCode,
                s.subject_name AS subjectName,
                l.full_name AS lecturerName,
                l.email AS lecturerEmail,
                l.phone_number AS lecturerPhone,
                l.lecturer_code AS lecturerCode
            FROM student_course_classes scc
            JOIN course_classes cc ON scc.course_class_id = cc.id
            JOIN class_schedules cs ON cc.id = cs.course_class_id
            JOIN subjects s ON cc.subject_id = s.id
            JOIN semesters sem ON cc.semester_id = sem.id
            LEFT JOIN lecturers l ON cc.lecturer_id = l.id
            WHERE scc.student_id = :studentId
              AND sem.id = :semesterId
              AND scc.status = 'ENROLLED'
            """,
            nativeQuery = true)
    List<ScheduleRow> findScheduleByStudentId(
            @Param("studentId") Long studentId,
            @Param("semesterId") Long semesterId
    );

        @Query(value = """
            SELECT
                cs.day_of_week AS dayOfWeek,
                cs.start_period AS startPeriod,
                cs.end_period AS endPeriod,
                cs.start_time AS startTime,
                cs.end_time AS endTime,
                cs.room AS room,
                cc.class_code AS classCode,
                s.subject_code AS subjectCode,
                s.subject_name AS subjectName,
                l.full_name AS lecturerName,
                l.email AS lecturerEmail,
                l.phone_number AS lecturerPhone,
                l.lecturer_code AS lecturerCode
            FROM student_course_classes scc
            JOIN course_classes cc ON scc.course_class_id = cc.id
            JOIN class_schedules cs ON cc.id = cs.course_class_id
            JOIN subjects s ON cc.subject_id = s.id
            JOIN semesters sem ON cc.semester_id = sem.id
            LEFT JOIN lecturers l ON cc.lecturer_id = l.id
            WHERE scc.student_id = :studentId AND scc.status = 'ENROLLED' AND sem.id = :semesterId AND cs.day_of_week = :dayOfWeek
            """, nativeQuery = true)
    List<ScheduleRow> findDayOfWeekSchedule(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId, @Param("dayOfWeek") int dayOfWeek);

    List<ClassSchedule> findByCourseClassId(Long courseClassId);

    @Query(value = """
        SELECT 1
            FROM class_schedules s1
            JOIN class_schedules s2
            ON s1.day_of_week = s2.day_of_week
            AND s1.start_period < s2.end_period
            AND s1.end_period > s2.start_period
            WHERE s1.course_class_id = :newClassId
            AND s2.course_class_id IN (:registeredIds)
        """, nativeQuery = true)
    boolean isScheduleConflict(
        @Param("newClassId") Long newClassId,
        @Param("registeredClassIds") List<Long> registeredClassIds
    );

    @Query(value = """
        SELECT s.* FROM class_schedules s
        JOIN course_classes cc ON s.course_class_id = cc.id
        WHERE cc.semester_id = :semesterId
            AND s.day_of_week IN :days   
            AND (
                s.day_of_week IN :days
                OR s.course_class_id = :courseClassId
            )
    """, nativeQuery = true)
    List<ClassSchedule> findForConflict(
            @Param("days") Set<Integer> days,
            @Param("courseClassId") Long courseClassId,
            @Param("semesterId") Long semesterId
    );
}
