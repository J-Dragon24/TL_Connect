package com.tl_connect.dev.modules.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.application.entity.ApplicationType;

@Repository
public interface ApplicationTypeRepository extends JpaRepository<ApplicationType, Long> {
    @Query(value = "SELECT * FROM application_types WHERE is_active = true ORDER BY created_at DESC", nativeQuery = true)
    List<ApplicationType> findAllApplicationType();
}
