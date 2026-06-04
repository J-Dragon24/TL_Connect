package com.tl_connect.dev.modules.feedback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.feedback.entity.FeedbackAttachment;

@Repository
public interface FeedbackAttachmentRepository extends JpaRepository<FeedbackAttachment, Long> {

}
