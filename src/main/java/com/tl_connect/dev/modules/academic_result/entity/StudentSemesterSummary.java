package com.tl_connect.dev.modules.academic_result.entity;

import java.math.BigDecimal;
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
@Table(name = "student_semester_summaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSemesterSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "study_program_id", nullable = false)
    private Long studyProgramId;

    @Column(name = "semester_id", nullable = false)
    private Long semesterId;

    @Column(name = "credits_registered")
    private Integer creditsRegistered;

    @Column(name = "credits_passed")
    private Integer creditsPassed;

    @Column(name = "semester_gpa", nullable = false)
    private BigDecimal semesterGpa;

    @Column(name = "letter_gpa")
    private String letterGpa;

    @Column(name = "conduct_score")
    private Integer conductScore;

    @Column(name = "activity_score", nullable = false)
    private BigDecimal activityScore;

    @Column(name = "letter_activity_score")
    private String letterActivityScore;

    @Column(name = "group_contribution", nullable = false)
    private BigDecimal groupContribution;

    @Column(name = "letter_group_contribution")
    private String letterGroupContribution;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
