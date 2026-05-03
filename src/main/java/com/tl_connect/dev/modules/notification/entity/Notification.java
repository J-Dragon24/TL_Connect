package com.tl_connect.dev.modules.notification.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.tl_connect.dev.shared.common.enums.NotificationCreatedBy;
import com.tl_connect.dev.shared.common.enums.NotificationType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_by")
    @Enumerated(EnumType.STRING)
    private NotificationCreatedBy createdBy;

    @Column(name = "target_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType targetType;

    @Column(name = "deadline")
    private LocalDate deadLine;

    @Column(name = "is_important")
    private Boolean isImportant;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public void update(String title, String content, NotificationCreatedBy createdBy, NotificationType targetType,LocalDate deadLine, Boolean isImportant) {
        if(title != null) this.title = title;
        if(content != null) this.content = content;
        if(createdBy != null) this.createdBy = createdBy;
        if(targetType != null) this.targetType = targetType;
        if(deadLine != null) this.deadLine = deadLine;
        if(isImportant != null) this.isImportant = isImportant;
    }
}
