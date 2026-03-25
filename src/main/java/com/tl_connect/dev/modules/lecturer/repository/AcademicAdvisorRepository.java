package com.tl_connect.dev.modules.lecturer.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tl_connect.dev.modules.lecturer.entity.AcademicAdvisor;
import com.tl_connect.dev.modules.lecturer.projection.AcademicAdvisorDetailView;
import com.tl_connect.dev.modules.lecturer.projection.AcademicAdvisorRow;

public interface AcademicAdvisorRepository extends JpaRepository<AcademicAdvisor, Long> {
    boolean existsByStudentClassId(Long studentClassId);

    @Query(value="""
        SELECT 
            a.id as id, 
            l.lecturer_code as lecturerCode, 
            l.full_name as lecturerName, 
            l.email as lecturerEmail, 
            l.phone_number as lecturerPhoneNumber, 
            s.class_code as studentClassCode 
        FROM academic_advisors a
        LEFT JOIN lecturers l ON a.lecturer_id = l.id
        LEFT JOIN student_classes s ON a.student_class_id = s.id
        WHERE l.status = 'ACTIVE'
        ORDER BY l.full_name ASC
        """, countQuery = """
            SELECT COUNT(a.id)
            FROM academic_advisors a
            LEFT JOIN lecturers l ON a.lecturer_id = l.id
            LEFT JOIN student_classes s ON a.student_class_id = s.id
            WHERE l.status = 'ACTIVE'
        """, nativeQuery = true)
    Page<AcademicAdvisorRow> findAllAcademicAdvisors(Pageable pageable);


    @Query(value="""
        SELECT 
            a.id as id, 
            l.lecturer_code as lecturerCode, 
            l.full_name as lecturerName, 
            l.email as lecturerEmail, 
            l.phone_number as lecturerPhoneNumber,
            d.department_code as departmentCode,
            l.status as lecturerStatus,
            s.class_code as studentClassCode,
            m.major_code as classMajorCode,
            s.start_year as studentClassYear 
        FROM academic_advisors a
        LEFT JOIN lecturers l ON a.lecturer_id = l.id
        LEFT JOIN departments d ON l.department_id = d.id
        LEFT JOIN student_classes s ON a.student_class_id = s.id
        LEFT JOIN majors m ON s.major_id = m.id
        WHERE l.status = 'ACTIVE'
        AND a.id = :id
        """, nativeQuery = true)
    Optional<AcademicAdvisorDetailView> findAcademicAdvisorById(Long id);
}
