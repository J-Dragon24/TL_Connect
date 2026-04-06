package com.tl_connect.dev.modules.application.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.application.entity.StudentApplication;
import com.tl_connect.dev.modules.application.projection.ApplicationRow;
import com.tl_connect.dev.modules.application.projection.DetailApplicationView;

@Repository
public interface ApplicationRepository extends JpaRepository<StudentApplication, Long> {
    
    @Query(value= """
        SELECT 
            sa.id as id, 
            s.student_code as studentCode, 
            s.full_name as studentName, 
            at.name as applicationTypeName, 
            sa.status as status 
        FROM student_applications sa 
        JOIN student s ON sa.student_id = s.id 
        JOIN application_types at ON sa.application_type_id = at.id 
        ORDER BY sa.created_at DESC
    """,
    countQuery = """
        SELECT COUNT(*) FROM student_applications sa 
        JOIN student s ON sa.student_id = s.id 
        JOIN application_types at ON sa.application_type_id = at.id
    """,
    nativeQuery = true)
    Page<ApplicationRow> findAllApplication(Pageable pageable);

    @Query(value= """
        SELECT 
            sa.id as id, 
            s.student_code as studentCode, 
            s.full_name as studentName, 
            at.name as applicationTypeName, 
            sa.status as status, 
            sa.content as content
        FROM student_applications sa 
        JOIN student s ON sa.student_id = s.id 
        JOIN application_types at ON sa.application_type_id = at.id 
        WHERE sa.id = :id
    """,
    nativeQuery = true)
    Optional<DetailApplicationView> findDetailById(Long id);
}
