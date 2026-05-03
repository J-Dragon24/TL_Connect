package com.tl_connect.dev.modules.subject.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Table(name = "subjects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "faculty_id")
    private Long facultyId;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "subject_code", nullable = false, unique = true)
    private String subjectCode;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @Column(name = "credits", nullable = false)
    private Integer credits;

    @Column(name = "coefficient", nullable = false)
    private BigDecimal coefficient;

    @Column(name = "lecture_hours")
    private Integer lectureHours;

    @Column(name = "practice_hours")
    private Integer practiceHours;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static Subject create(Long facultyId, Long departmentId, String subjectCode, String subjectName, Integer credits, BigDecimal coefficient, Integer lectureHours, Integer practiceHours) {
        if(credits < 0 || coefficient.compareTo(BigDecimal.ZERO) <= 0 || lectureHours < 0 || practiceHours < 0){
            throw new InvalidInputException("Invalid subject data");
        }
        Subject subject = new Subject();
        subject.setFacultyId(facultyId);
        subject.setDepartmentId(departmentId);
        subject.setSubjectCode(subjectCode);
        subject.setSubjectName(subjectName);
        subject.setCredits(credits);
        subject.setCoefficient(coefficient);
        subject.setLectureHours(lectureHours);
        subject.setPracticeHours(practiceHours);
        subject.setIsActive(true);
        return subject;
    }

    public void update(Long facultyId, Long departmentId, String subjectCode, String subjectName, Integer credits, BigDecimal coefficient, Integer lectureHours, Integer practiceHours) {
        if(facultyId != null){
            this.facultyId = facultyId;
        }
        if(departmentId != null){
            this.departmentId = departmentId;
        }
        if(credits != null && credits > 0){
            this.credits = credits;
        }
        if(coefficient != null && coefficient.compareTo(BigDecimal.ZERO) > 0){
            this.coefficient = coefficient;
        }
        if(lectureHours != null && lectureHours > 0){
            this.lectureHours = lectureHours;
        }
        if(practiceHours != null && practiceHours > 0){
            this.practiceHours = practiceHours;
        }
        if (subjectCode != null) {
            this.subjectCode = subjectCode;
        }
        if (subjectName != null) {
            this.subjectName = subjectName;
        }
    }

    public void deactivate() {
        this.isActive = false;
    }
}
