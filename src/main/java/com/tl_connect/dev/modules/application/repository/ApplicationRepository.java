package com.tl_connect.dev.modules.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.application.entity.ApplicationType;
import com.tl_connect.dev.modules.application.entity.StudentApplication;

@Repository
public interface ApplicationRepository extends JpaRepository<StudentApplication, Long> {
    @Query(value = "SELECT * FROM application_types", nativeQuery = true)
    List<ApplicationType> findAllApplicationType();

    
}
