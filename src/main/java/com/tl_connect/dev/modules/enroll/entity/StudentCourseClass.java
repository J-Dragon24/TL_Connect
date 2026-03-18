package com.tl_connect.dev.modules.enroll.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tl_connect.dev.core.common.enums.StudentCourseClassStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_course_classes",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "course_class_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCourseClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_class_id", nullable = false)
    private Long courseClassId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StudentCourseClassStatus status;

    @Column(name = "is_retake", nullable = false)
    private Boolean isRetake;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
