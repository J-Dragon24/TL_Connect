package com.tl_connect.dev.modules.department;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.department.projection.DepartmentRow;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsById(Long id);

    boolean existsByDepartmentCode(String departmentCode);

    @Query(value= """
            SELECT 
                d.id as id,
                d.department_code as departmentCode,
                d.department_name as departmentName,
                f.faculty_code as facultyCode,
                d.is_active as isActive
            FROM departments d
            JOIN faculties f ON d.faculty_id = f.id
            WHERE d.is_active = true
            ORDER BY d.department_code ASC
            """,
            countQuery = """
                    SELECT COUNT(d.id) FROM departments d
                    JOIN faculties f ON d.faculty_id = f.id
                    WHERE d.is_active = true
                    """,
            nativeQuery = true)
    Page<DepartmentRow> findAllDepartment(Pageable pageable);
}