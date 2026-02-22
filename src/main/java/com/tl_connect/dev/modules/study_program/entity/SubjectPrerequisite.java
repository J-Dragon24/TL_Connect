package com.tl_connect.dev.modules.study_program.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subject_prerequisites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectPrerequisite {

    @EmbeddedId
    private SubjectPrerequisiteId id;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectPrerequisiteId implements Serializable {
        @Column(name = "subject_id")
        private Long subjectId;

        @Column(name = "prerequisite_subject_id")
        private Long prerequisiteSubjectId;
    }
}
