package com.tl_connect.dev.modules.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification_targets")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationTarget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "target_id")
    private Long targetId;

    public static NotificationTarget create(Long notificationId, Long targetId) {
        NotificationTarget notificationTarget = new NotificationTarget();
        notificationTarget.setNotificationId(notificationId);
        notificationTarget.setTargetId(targetId);
        return notificationTarget;
    }
}
