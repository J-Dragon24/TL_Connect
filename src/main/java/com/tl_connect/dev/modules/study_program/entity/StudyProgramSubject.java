package com.tl_connect.dev.modules.study_program.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "study_program_subjects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyProgramSubject {

    @EmbeddedId
    private StudyProgramSubjectId id;

    @Column(name = "semester_id", nullable = false)
    private Long semesterId;

    @Column(name = "elective_group", length = 50)
    private String electiveGroup;

    @Column(name = "is_required")
    private Boolean isRequired;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudyProgramSubjectId implements Serializable {
        @Column(name = "study_program_id")
        private Long studyProgramId;

        @Column(name = "subject_id")
        private Long subjectId;
    }
}
