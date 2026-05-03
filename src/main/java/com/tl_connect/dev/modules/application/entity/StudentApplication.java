package com.tl_connect.dev.modules.application.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tl_connect.dev.shared.common.enums.ApplicationStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "application_type_id", nullable = false)
    private Long applicationTypeId;

    @Column(name = "content")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ApplicationStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static StudentApplication create(Long studentId, Long applicationTypeId, String content) {
        return StudentApplication.builder()
                .studentId(studentId)
                .applicationTypeId(applicationTypeId)
                .content(content)
                .status(ApplicationStatus.PENDING)
                .build();
    }

    public void updateStatus(ApplicationStatus status) {
        this.status = status;
    }
}
