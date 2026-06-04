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
                WHERE ep.end_time >= NOW()
                ORDER BY 
                CASE 
                        WHEN NOW() BETWEEN ep.start_time AND ep.end_time THEN 0
                        ELSE 1
                END,
                ep.start_time ASC
                LIMIT 1
                """, nativeQuery = true)
        Optional<EnrollmentPeriod> findNearestOrCurrent(); 
}
