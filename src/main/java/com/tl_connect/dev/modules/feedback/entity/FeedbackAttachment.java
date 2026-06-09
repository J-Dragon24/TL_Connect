package com.tl_connect.dev.modules.feedback.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "feedback_attachments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "feedback_id", nullable = false)
    private Long feedbackId;

    @Column(name = "file_key", nullable = false)
    private String fileKey;

    @Column(name = "original_filename")
    private String originalFileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "resource_type")
    private String resourceType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static FeedbackAttachment create(Long feedbackId, String fileKey, String fileName, Long fileSize, String resourceType) {
        return FeedbackAttachment.builder()
                .feedbackId(feedbackId)
                .fileKey(fileKey)
                .originalFileName(fileName)
                .fileSize(fileSize)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
