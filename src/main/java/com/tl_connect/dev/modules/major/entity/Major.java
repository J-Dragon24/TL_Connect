package com.tl_connect.dev.modules.major.entity;

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
@Table(name = "majors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "major_code", unique = true, nullable = false)
    private String majorCode;

    @Column(name = "major_name", nullable = false)
    private String majorName;

    @Column(name = "faculty_id", nullable = false)
    private Long facultyId;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static Major create(String majorCode, String majorName, Long facultyId) {
        Major major = new Major();
        major.majorCode = majorCode;
        major.majorName = majorName;
        major.facultyId = facultyId;
        major.isActive = true;
        return major;
    }

    public void update(String majorCode, String majorName, Long facultyId) {
        if (majorCode != null) {
            this.majorCode = majorCode;
        }
        if (majorName != null) {
            this.majorName = majorName;
        }
        if (facultyId != null) {
            this.facultyId = facultyId;
        }
    }

    public void delete() {
        this.isActive = false;
    }
}
