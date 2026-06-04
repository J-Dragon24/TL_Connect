package com.tl_connect.dev.modules.lecturer.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tl_connect.dev.modules.lecturer.entity.AcademicAdvisor;
import com.tl_connect.dev.modules.lecturer.projection.AcademicAdvisorDetailView;
import com.tl_connect.dev.modules.lecturer.projection.AcademicAdvisorClassRow;

public interface AcademicAdvisorRepository extends JpaRepository<AcademicAdvisor, Long> {

    void deleteByStudentClassId(Long studentClassId);

    @Query(value="""
        SELECT EXISTS(
            SELECT 1
            FROM academic_advisors a
            LEFT JOIN lecturers l ON a.lecturer_id = l.id
            WHERE l.status = 'ACTIVE'
            AND a.student_class_id = :studentClassId
        )
        """, nativeQuery = true)
    boolean existsByStudentClassId(@Param("studentClassId") Long studentClassId);

    
    void deleteByLecturerId(Long lecturerId);

    @Query(value="""
        SELECT 
            a.lecturer_id as lecturerId, 
            s.class_code as studentClassCode 
        FROM academic_advisors a
        JOIN student_classes s ON a.student_class_id = s.id
        WHERE a.lecturer_id IN :lecturerIds
        """, nativeQuery = true)
    List<AcademicAdvisorClassRow> getClassByLecturerIds(@Param("lecturerIds") List<Long> lecturerIds);


    @Query(value="""
        SELECT
            a.id as id,
            l.id as lecturerId,
            l.lecturer_code as lecturerCode, 
            l.full_name as lecturerName, 
            l.email as lecturerEmail, 
            l.phone_number as lecturerPhoneNumber,
            d.department_code as departmentCode,
            l.status as lecturerStatus,
            s.class_code as studentClassCode,
            m.major_code as classMajorCode,
            s.start_year as studentClassYear 
        FROM lecturers l
        JOIN academic_advisors a ON a.lecturer_id = l.id
        JOIN departments d ON l.department_id = d.id
        JOIN student_classes s ON a.student_class_id = s.id
        JOIN majors m ON s.major_id = m.id
        WHERE l.status = 'ACTIVE'
        AND l.id = :lecturerId
        """, nativeQuery = true)
    List<AcademicAdvisorDetailView> findAcademicAdvisorByLecturerId(@Param("lecturerId") Long lecturerId);
}
