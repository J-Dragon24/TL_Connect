package com.tl_connect.dev.modules.lecturer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.lecturer.entity.Lecturer;
import com.tl_connect.dev.modules.lecturer.projection.LecturerRow;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {

    boolean existsByLecturerCode(String lecturerCode);

    @Query(value="""
        SELECT 
            l.id as id, 
            l.lecturer_code as lecturerCode, 
            l.full_name as fullName, 
            l.email as email, 
            l.phone_number as phoneNumber, 
            d.department_code as departmentCode, 
            l.status as status 
        FROM lecturers l
        LEFT JOIN departments d ON l.department_id = d.id
        ORDER BY l.full_name ASC
    """,
    countQuery = """
        SELECT 
            COUNT(l.id) 
        FROM lecturers l
        LEFT JOIN departments d ON l.department_id = d.id
    """,
    nativeQuery = true)
    Page<LecturerRow> findAllLecturer(Pageable pageable);


    @Query(value="""
        SELECT 
            l.id as id, 
            l.lecturer_code as lecturerCode, 
            l.full_name as fullName, 
            l.email as email, 
            l.phone_number as phoneNumber, 
            d.department_code as departmentCode, 
            l.status as status 
        FROM lecturers l
        LEFT JOIN departments d ON l.department_id = d.id
        WHERE l.id = :id
    """, nativeQuery = true)
    Optional<LecturerRow> findLecturerById(Long id);
}
