package com.tl_connect.dev.modules.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.application.entity.ApplicationAttachment;

@Repository
public interface ApplicationAttachmentRepository extends JpaRepository<ApplicationAttachment, Long> {
    
}
