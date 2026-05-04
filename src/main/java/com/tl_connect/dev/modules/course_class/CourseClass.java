package com.tl_connect.dev.modules.course_class;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "course_classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lecturer_id")
    private Long lecturerId;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "semester_id", nullable = false)
    private Long semesterId;

    @Column(name = "class_code", nullable = false)
    private String classCode;

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "enrolled_count", nullable = false)
    @Builder.Default
    private Integer enrolledCount = 0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Version
    @Column(name = "version")
    @Builder.Default
    private Long version = 0L;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static CourseClass create(Long lecturerId, Long subjectId, Long semesterId, String classCode, String className, Integer capacity) {
        CourseClass courseClass = new CourseClass();
        if(lecturerId != null) courseClass.lecturerId = lecturerId;
        courseClass.subjectId = subjectId;
        courseClass.semesterId = semesterId;
        courseClass.classCode = classCode;
        courseClass.className = className;
        courseClass.capacity = capacity;
        courseClass.isActive = true;
        return courseClass;
    }

    public void update(Long lecturerId, Long subjectId, Long semesterId, String classCode, String className, Integer capacity) {
        if(lecturerId != null) this.lecturerId = lecturerId;
        if(subjectId != null) this.subjectId = subjectId;
        if(semesterId != null) this.semesterId = semesterId;
        if(classCode != null) this.classCode = classCode;
        if(className != null) this.className = className;
        if(capacity != null) this.capacity = capacity;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
