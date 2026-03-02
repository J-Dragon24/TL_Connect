package com.tl_connect.dev.modules.study_program.entity;

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
@Table(name = "study_programs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "major_id", nullable = false)
    private Long majorId;

    @Column(name = "study_program_name", nullable = false)
    private String studyProgramName;

    @Column(name = "study_program_code", nullable = false)
    private String studyProgramCode;

    @Column(name = "start_year", nullable = false)
    private Integer startYear;

    @Column(name = "total_credits")
    private Integer totalCredits;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
