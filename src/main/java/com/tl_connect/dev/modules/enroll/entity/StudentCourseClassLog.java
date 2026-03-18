package com.tl_connect.dev.modules.enroll.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.tl_connect.dev.core.common.enums.EnrollAction;
import com.tl_connect.dev.core.common.enums.StudentCourseClassStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_course_class_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCourseClassLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_class_id", nullable = false)
    private Long courseClassId;

    @Column(name = "action", nullable = false)
    @Enumerated(EnumType.STRING)
    private EnrollAction action;

    @Column(name = "from_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StudentCourseClassStatus fromStatus;

    @Column(name = "to_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StudentCourseClassStatus toStatus;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
