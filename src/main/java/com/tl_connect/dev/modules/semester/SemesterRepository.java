package com.tl_connect.dev.modules.semester;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {

    List<Semester> findAll();

    @Query(value = """
        SELECT *
        FROM Semesters s
        WHERE 
            (EXTRACT(YEAR FROM s.start_date) > :startYear 
            OR (EXTRACT(YEAR FROM s.start_date) = :startYear AND s.semester_number >= 1))
        AND 
            (EXTRACT(YEAR FROM s.start_date) < :endYear
            OR (EXTRACT(YEAR FROM s.start_date) = :endYear AND s.semester_number <= 2))
    """, nativeQuery = true)
    List<Semester> findAllStudentSemester(@Param("startYear") int startYear, @Param("endYear") int endYear);

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
    
    @Query(value = """
            SELECT EXISTS(
                SELECT 1
                FROM semesters
                WHERE academic_years = :academicYears AND semester_number = :semesterNumber
            )
            """, nativeQuery = true)
    boolean existsByAcademicYearsAndSemesterNumber(@Param("academicYears") String academicYears, @Param("semesterNumber") int semesterNumber);

    @Query(value = """
            SELECT EXISTS(
                SELECT 1
                FROM semesters
                WHERE semester_code = :semesterCode
            )
            """, nativeQuery = true)
    boolean existsBySemesterCode(@Param("semesterCode") String semesterCode);

    boolean existsById(Long id);
}
