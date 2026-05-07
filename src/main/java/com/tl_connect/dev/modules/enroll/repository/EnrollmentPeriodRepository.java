package com.tl_connect.dev.modules.enroll.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tl_connect.dev.modules.enroll.entity.EnrollmentPeriod;

public interface EnrollmentPeriodRepository extends JpaRepository<EnrollmentPeriod, Long> {
        @Query(value = """
                SELECT ep.* FROM enrollment_periods ep
                WHERE ep.semester_id = :semesterId
                ORDER BY ep.created_at DESC
                LIMIT 1
                """, nativeQuery = true)
        Optional<EnrollmentPeriod> findLatestBySemesterId(@Param("semesterId") Long semesterId);

        @Query(value = """
                SELECT ep.* FROM enrollment_periods ep
                LEFT JOIN semesters s ON ep.semester_id = s.id
                WHERE (:semesterCode is null OR s.semester_code LIKE CONCAT('%', :semesterCode, '%'))
                ORDER BY ep.start_time DESC
                """,
                countQuery = """
                        SELECT COUNT(*) FROM enrollment_periods ep
                        LEFT JOIN semesters s ON ep.semester_id = s.id
                        WHERE (:semesterCode is null OR s.semester_code LIKE CONCAT('%', :semesterCode, '%'))
                        """, nativeQuery = true)
        Page<EnrollmentPeriod> findAllPeriods(Pageable pageable, @Param("semesterCode") String semesterCode);

        @Query(value = """
                SELECT ep.* FROM enrollment_periods ep
                WHERE ep.start_time <= NOW() AND ep.end_time >= NOW()
                LIMIT 1
                """, nativeQuery = true)
        Optional<EnrollmentPeriod> findCurrent();
}
