package com.tl_connect.dev.modules.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tl_connect.dev.modules.notification.entity.NotificationTarget;

public interface NotificationTargetRepository extends JpaRepository<NotificationTarget, Long> {
    
    List<NotificationTarget> findByNotificationIdAndTargetIdIn(Long notificationId, List<Long> targetIds);
}