package com.tl_connect.dev.modules.faculty;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByFacultyCode(String facultyCode);

    boolean existsByFacultyCode(String facultyCode);

    Optional<Faculty> findByIdAndIsActiveTrue(Long id);

    @Query(value = "SELECT * FROM faculties WHERE is_active = true ORDER BY faculty_code ASC",
            countQuery = "SELECT COUNT(*) FROM faculties WHERE is_active = true",
            nativeQuery = true)
    Page<Faculty> findAllActive(Pageable pageable);
}
