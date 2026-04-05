package com.tl_connect.dev.modules.notification.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_read", uniqueConstraints = @UniqueConstraint(columnNames = {
        "notification_id",
        "oauth_user_id"
}))
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationRead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "oauth_user_id")
    private Long oauthUserId;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}
