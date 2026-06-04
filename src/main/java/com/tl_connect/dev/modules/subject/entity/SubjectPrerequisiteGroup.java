package com.tl_connect.dev.modules.subject.entity;

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
@Table(name = "subject_prerequisite_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectPrerequisiteGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "min_subjects_required")
    private Integer minSubjectsRequired;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static SubjectPrerequisiteGroup create(Long subjectId, Integer minSubjectsRequired, String description) {
        if(minSubjectsRequired < 0){
            throw new InvalidInputException("Invalid min subjects required");
        }
        SubjectPrerequisiteGroup subjectPrerequisiteGroup = new SubjectPrerequisiteGroup();
        subjectPrerequisiteGroup.setSubjectId(subjectId);
        subjectPrerequisiteGroup.setMinSubjectsRequired(minSubjectsRequired);
        subjectPrerequisiteGroup.setDescription(description);
        return subjectPrerequisiteGroup;
    }
}
