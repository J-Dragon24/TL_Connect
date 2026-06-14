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
@Table(name = "student_subject_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSubjectResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "semester_id", nullable = false)
    private Long semesterId;

    @Column(name = "credits", nullable = false)
    private Integer credits;

    @Column(name = "attendance_score")
    private BigDecimal attendanceScore;

    @Column(name = "midterm_score")
    private BigDecimal midtermScore;

    @Column(name = "final_score")
    private BigDecimal finalScore;

    @Column(name = "score_10")
    private BigDecimal score10;

    @Column(name = "score_4")
    private BigDecimal score4;

    @Column(name = "letter_grade")
    private String letterGrade;

    @Column(name = "is_pass")
    private Boolean isPass;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static StudentSubjectResult create(Long studentId, Long subjectId, Long semesterId, Integer credits, BigDecimal attendanceScore, BigDecimal midtermScore, BigDecimal finalScore, BigDecimal score10, BigDecimal score4, String letterGrade, Boolean isPass) {
        return StudentSubjectResult.builder()
                .studentId(studentId)
                .subjectId(subjectId)
                .semesterId(semesterId)
                .credits(credits)
                .attendanceScore(attendanceScore)
                .midtermScore(midtermScore)
                .finalScore(finalScore)
                .score10(score10)
                .score4(score4)
                .letterGrade(letterGrade)
                .isPass(isPass)
                .build();
    }

    public void update(Long semesterId, BigDecimal attendanceScore, BigDecimal midtermScore, BigDecimal finalScore, BigDecimal score10, BigDecimal score4, String letterGrade, Boolean isPass) {
        if(semesterId != null) this.semesterId = semesterId;
        if(attendanceScore != null) this.attendanceScore = attendanceScore;
        if(midtermScore != null) this.midtermScore = midtermScore;
        if(finalScore != null) this.finalScore = finalScore;
        if(score10 != null) this.score10 = score10;
        if(score4 != null) this.score4 = score4;
        if(letterGrade != null) this.letterGrade = letterGrade;
        if(isPass != null) this.isPass = isPass;
    }
}
