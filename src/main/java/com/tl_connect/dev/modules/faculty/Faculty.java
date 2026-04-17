package com.tl_connect.dev.modules.faculty;

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
@Table(name = "faculties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "faculty_code", unique = true, nullable = false)
    private String facultyCode;

    @Column(name = "faculty_name", nullable = false)
    private String facultyName;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static Faculty create(String facultyCode, String facultyName) {
        Faculty faculty = new Faculty();
        faculty.facultyCode = facultyCode;
        faculty.facultyName = facultyName;
        faculty.isActive = true;
        return faculty;
    }

    public void update(String facultyCode, String facultyName) {
        if(facultyCode != null) this.facultyCode = facultyCode;
        if(facultyName != null) this.facultyName = facultyName;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
