package com.tl_connect.dev.modules.semester;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {

    @Query(value = """
            SELECT *
            FROM semesters
            ORDER BY academic_years DESC, semester_number DESC
            """,
            countQuery = """
                    SELECT COUNT(id)
                    FROM semesters
                    """,
            nativeQuery = true)
    Page<Semester> findAllSemesters(Pageable pageable);

    List<Semester> findBySemesterCodeIn(Set<String> semesterCodes);

    Optional<Semester> findBySemesterCode(String semesterCode);

    @Query(value = """
        SELECT *
        FROM Semesters s
        WHERE 
            (EXTRACT(YEAR FROM s.start_date) > :startYear 
            OR (EXTRACT(YEAR FROM s.start_date) = :startYear AND s.semester_number >= 1))
        AND 
            (EXTRACT(YEAR FROM s.start_date) < :endYear
            OR (EXTRACT(YEAR FROM s.start_date) = :endYear AND s.semester_number <= 3))
    """, nativeQuery = true)
    List<Semester> findAllStudentSemester(@Param("startYear") int startYear, @Param("endYear") int endYear);

    @Query(value = """
            SELECT *
            FROM semesters
            WHERE :date BETWEEN start_date AND end_date
                AND is_active = true
            ORDER BY start_date DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<Semester> findSemesterByDate(@Param("date") LocalDate date);
    
    @Query(value = """
            SELECT EXISTS(
                SELECT 1
                FROM semesters
                WHERE academic_years = :academicYears AND semester_number = :semesterNumber AND is_active = true
            )
            """, nativeQuery = true)
    boolean existsByAcademicYearsAndSemesterNumber(@Param("academicYears") String academicYears, @Param("semesterNumber") int semesterNumber);

    @Query(value = """
            SELECT EXISTS(
                SELECT 1
                FROM semesters
                WHERE academic_years = :academicYears AND semester_number = :semesterNumber AND id != :id AND is_active = true
            )
            """, nativeQuery = true)
    boolean existsByAcademicYearsAndSemesterNumberAndIdNot(@Param("academicYears") String academicYears, @Param("semesterNumber") int semesterNumber, @Param("id") Long id);

        @Query(value = """
            SELECT EXISTS(
                SELECT 1
                FROM semesters
                WHERE semester_code = :semesterCode AND id != :id
            )
            """, nativeQuery = true)
    boolean existsBySemesterCodeAndIdNot(@Param("semesterCode") String semesterCode, @Param("id") Long id);

    @Query(value = """
            SELECT EXISTS(
                SELECT 1
                FROM semesters
                WHERE semester_code = :semesterCode
            )
            """, nativeQuery = true)
    boolean existsBySemesterCode(@Param("semesterCode") String semesterCode);

    @Query(value = """
            SELECT EXISTS(
                SELECT 1
                FROM semesters
                WHERE (start_date BETWEEN :startDate AND :endDate)
                    OR (end_date BETWEEN :startDate AND :endDate)
                    OR (start_date <= :startDate AND end_date >= :endDate)
            )
            """, nativeQuery = true)
    boolean violateDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    boolean existsById(Long id);
}
