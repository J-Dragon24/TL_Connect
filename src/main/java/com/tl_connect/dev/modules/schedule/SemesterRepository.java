package com.tl_connect.dev.modules.schedule;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.study_program.entity.Semester;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    @Query(value = """
            SELECT *
            FROM semesters
            WHERE :date BETWEEN start_date AND end_date
            """, nativeQuery = true)
    Optional<Semester> findSemesterByDate(@Param("date") LocalDate date);

    @Query(value = """
            SELECT *
            FROM semesters
            WHERE semester_name = :semesterName
            """, nativeQuery = true)
    Optional<Semester> findSemesterByName(@Param("semesterName") String semesterName);
}
