package com.tl_connect.dev.modules.notification.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.tl_connect.dev.core.common.enums.NotificationCreatedBy;
import com.tl_connect.dev.core.common.enums.NotificationType;

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

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_type")
    private String referenceType;

    @Column(name = "deadline")
    private LocalDate deadLine;

    @Column(name = "is_important")
    private Boolean isImportant;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public void update(String title, String content, NotificationCreatedBy createdBy, NotificationType targetType, Long targetId, Long referenceId, String referenceType, LocalDate deadLine, Boolean isImportant) {
        if(title != null) this.title = title;
        if(content != null) this.content = content;
        if(createdBy != null) this.createdBy = createdBy;
        if(targetType != null) this.targetType = targetType;
        if(targetId != null) this.targetId = targetId;
        if(referenceId != null) this.referenceId = referenceId;
        if(referenceType != null) this.referenceType = referenceType;
        if(deadLine != null) this.deadLine = deadLine;
        if(isImportant != null) this.isImportant = isImportant;
    }
}
