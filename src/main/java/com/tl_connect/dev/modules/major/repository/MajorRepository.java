package com.tl_connect.dev.modules.major.repository;


import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.major.entity.Major;
import com.tl_connect.dev.modules.major.projection.MajorRow;

@Repository
public interface MajorRepository extends JpaRepository<Major, Long>{

    List<Major> findByMajorCodeIn(Set<String> majorCodes);

    Optional<Major> findByMajorCode(String majorCode);

    boolean existsByMajorCode(String majorCode);

    @Query(value = """
            SELECT 
                m.id as id,
                m.major_name as majorName,
                m.major_code as majorCode,
                f.faculty_code as facultyCode,
                m.is_active as isActive,
                m.created_at as createdAt,
                m.updated_at as updatedAt
            FROM majors m
            JOIN faculties f ON m.faculty_id = f.id
            """,
            countQuery = """
                    SELECT COUNT(m.id)
                    FROM majors m
                    JOIN faculties f ON m.faculty_id = f.id
                    """, 
                    nativeQuery = true)
    Page<MajorRow> findAllMajors(Pageable pageable);
}
