package com.tl_connect.dev.modules.notification.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.notification.entity.NotificationRead;

@Repository
public interface NotificationReadRepository extends JpaRepository<NotificationRead, Long> {
    @Modifying
    @Query(value = """
        INSERT INTO notification_read (notification_id, oauth_user_id)
        SELECT unnest(:notificationIds), :oauthUserId
        ON CONFLICT DO NOTHING;
    """, nativeQuery = true)
    void markAsRead(@Param("oauthUserId") Long oauthUserId,
                    @Param("notificationIds") Long[] notificationIds);
}
