package com.tl_connect.dev.modules.attendance.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "attendances")
@Getter
@Setter
@Builder
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_class_id", nullable = false)
    private Long courseClassId;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "session_id", nullable = false)
    private String sessionId;
    
    @Column(name = "check_in_time", nullable = false)
    private LocalDateTime checkInTime;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static Attendance create(Long courseClassId, String sessionId, Long studentId, LocalDateTime checkInTime) {
        return Attendance.builder()
                .courseClassId(courseClassId)
                .sessionId(sessionId)
                .studentId(studentId)
                .checkInTime(checkInTime)
                .build();
    }
}
