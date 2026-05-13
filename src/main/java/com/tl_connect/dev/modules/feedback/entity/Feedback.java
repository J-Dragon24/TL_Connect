package com.tl_connect.dev.modules.feedback.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.tl_connect.dev.shared.common.enums.FeedbackStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "feedback")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "oauth_user_id")
    private Long oauthUserId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "app_version")
    private String appVersion;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FeedbackStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static Feedback create(Long oauthUserId, String title, String content, Long categoryId, String appVersion, String deviceInfo) {
        return Feedback.builder()
        .oauthUserId(oauthUserId)
                .title(title)
                .content(content)
                .categoryId(categoryId)
                .appVersion(appVersion)
                .deviceInfo(deviceInfo)
                .status(FeedbackStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
