package com.tl_connect.dev.modules.enroll.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.enroll.entity.CourseClass;
import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface CourseClassRepository extends JpaRepository<CourseClass, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cc FROM CourseClass cc WHERE cc.id = :id")
    Optional<CourseClass> findByIdForUpdate(@Param("id") Long id);
}
