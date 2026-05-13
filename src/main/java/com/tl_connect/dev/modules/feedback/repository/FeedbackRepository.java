package com.tl_connect.dev.modules.feedback.repository;

import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.feedback.entity.Feedback;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tl_connect.dev.modules.feedback.projection.FeedbackRow;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query(value = """
        SELECT 
            f.id AS feedbackId,
            ou.email,
            f.title,
            f.content,
            f.app_version AS appVersion,
            f.device_info AS deviceInfo,
            f.status,
            f.created_at AS createdAt,
            fc.name AS categoryName,
            STRING_AGG(fa.file_key, ',') AS feedbackImages
        FROM feedback f
        LEFT JOIN feedback_category fc ON f.category_id = fc.id
        LEFT JOIN oauth_users ou ON f.oauth_user_id = ou.id
        LEFT JOIN feedback_attachments fa ON f.id = fa.feedback_id
        GROUP BY f.id,
        ou.email,
        f.title,
        f.content,
        f.app_version,
        f.device_info,
        f.status,
        f.created_at,
        fc.name
        ORDER BY f.created_at DESC
    """, nativeQuery = true)
    List<FeedbackRow> findAllFeedback();
}
