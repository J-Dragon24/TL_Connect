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
    @Query("""
            SELECT
                sem.id AS id,
                sem.semesterName AS semesterName,
                sem.startDate AS startDate,
                sem.endDate AS endDate
            FROM Semester sem
            WHERE :date BETWEEN sem.startDate AND sem.endDate
            """)
    Optional<Semester> findSemesterByDate(LocalDate date);

    @Query("""
            SELECT
                sem.id AS id,
                sem.startDate AS startDate,
                sem.endDate AS endDate
            FROM Semester sem
            WHERE sem.semesterName = :semesterName
            """)
    Optional<Semester> findSemesterByName(@Param("semesterName") String semesterName);
}
