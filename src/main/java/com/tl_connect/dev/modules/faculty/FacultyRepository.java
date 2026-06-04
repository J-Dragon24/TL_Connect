package com.tl_connect.dev.modules.faculty;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByFacultyCode(String facultyCode);

    boolean existsByFacultyCode(String facultyCode);

    Optional<Faculty> findByIdAndIsActiveTrue(Long id);
}
