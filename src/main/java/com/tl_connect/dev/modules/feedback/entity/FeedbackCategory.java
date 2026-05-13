package com.tl_connect.dev.modules.feedback.entity;

import java.time.LocalDateTime;
import java.util.Optional;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tl_connect.dev.shared.common.exception.InvalidInputException;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "feedback_category")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static FeedbackCategory create(String name, String description) {
        if (name == null || name.isEmpty()) {
            throw new InvalidInputException("Name is required");
        }
        return FeedbackCategory.builder()
                .name(name)
                .description(description)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void update(String name, String description) {
        Optional.ofNullable(name).ifPresent(this::setName);
        Optional.ofNullable(description).ifPresent(this::setDescription);
        this.setUpdatedAt(LocalDateTime.now());
    }

    public void deactive() {
        this.isActive = false;
        this.setUpdatedAt(LocalDateTime.now());
    }
}
