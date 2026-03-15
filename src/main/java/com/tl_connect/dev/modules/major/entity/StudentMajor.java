package com.tl_connect.dev.modules.major.entity;

import com.tl_connect.dev.core.common.enums.StudentMajorStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_majors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentMajor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "study_program_id", nullable = false)
    private Long studyProgramId;

    @Column(name = "major_id", nullable = false)
    private Long majorId;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary;

    @Column(name = "start_year", nullable = false)
    private Integer startYear;

    @Column(name = "end_year")
    private Integer endYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StudentMajorStatus status;
}
