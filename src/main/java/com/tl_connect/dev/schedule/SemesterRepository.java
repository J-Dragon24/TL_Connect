package com.tl_connect.dev.schedule;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.academic.entity.Semester;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    @Query(value="""
            SELECT
                sem.id AS id,
                sem.semester_name AS semesterName,
                sem.start_date AS startDate,
                sem.end_date AS endDate
            FROM semesters sem
            WHERE :date BETWEEN sem.start_date AND sem.end_date
            """, nativeQuery = true)
    Optional<Semester> findSemesterByDate(LocalDate date);

    @Query(value="""
            SELECT
                sem.id AS id,
                sem.start_date AS startDate,
                sem.end_date AS endDate
            FROM semesters sem
            WHERE sem.semester_name = :semesterName
            """, nativeQuery = true)
    Optional<Semester> findSemesterByName(@Param("semesterName") String semesterName);
}
