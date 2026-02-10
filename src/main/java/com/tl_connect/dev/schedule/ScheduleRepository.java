package com.tl_connect.dev.schedule;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.schedule.entity.CourseClass;
import com.tl_connect.dev.schedule.projection.ScheduleRow;

@Repository
public interface ScheduleRepository extends JpaRepository<CourseClass, Long> {
    @Query("""
            SELECT
                cs.dayOfWeek AS dayOfWeek,
                cs.startPeriod AS startPeriod,
                cs.endPeriod AS endPeriod,
                cs.startTime AS startTime,
                cs.endTime AS endTime,
                cs.room AS room,
                cc.classCode AS classCode,
                s.subjectCode AS subjectCode,
                s.subjectName AS subjectName,
                l.fullName AS lecturerName,
                l.email AS lecturerEmail
            FROM StudentCourseClass scc
            JOIN CourseClass cc ON scc.courseClassId = cc.id
            JOIN ClassSchedule cs ON cc.id = cs.courseClassId
            JOIN Subject s ON cc.subjectId = s.id
            JOIN Semester sem ON cc.semesterId = sem.id
            LEFT JOIN Lecturer l ON cc.lecturerId = l.id
            WHERE scc.studentId = :studentId AND sem.id = :semesterId
            """)
    Optional<List<ScheduleRow>> findScheduleByStudentId(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId);

}
