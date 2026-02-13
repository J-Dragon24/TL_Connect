package com.tl_connect.dev.modules.schedule.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
