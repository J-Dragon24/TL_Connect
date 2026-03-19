package com.tl_connect.dev.modules.subject.entity;

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
@Table(name = "subject_prerequisite_group_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectPrerequisiteGroupItem {

    @EmbeddedId
    private SubjectPrerequisiteGroupItemId id;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectPrerequisiteGroupItemId implements Serializable {
        @Column(name = "group_id")
        private Long groupId;

        @Column(name = "prerequisite_subject_id")
        private Long prerequisiteSubjectId;
    }
}
