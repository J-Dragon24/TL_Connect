package com.tl_connect.dev.modules.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.application.entity.StudentApplication;

@Repository
public interface ApplicationRepository extends JpaRepository<StudentApplication, Long> {
    
    
}
