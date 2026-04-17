package com.tl_connect.dev.modules.department;

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
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "faculty_id", nullable = false)
    private Long facultyId;

    @Column(name = "department_code", unique = true, nullable = false)
    private String departmentCode;

    @Column(name = "department_name", nullable = false)
    private String departmentName;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static Department create(Long facultyId, String departmentCode, String departmentName) {
        Department department = new Department();
        department.facultyId = facultyId;
        department.departmentCode = departmentCode;
        department.departmentName = departmentName;
        department.isActive = true;
        return department;
    }

    public void update(String departmentCode, String departmentName) {
        if(departmentCode != null) this.departmentCode = departmentCode;
        if(departmentName != null) this.departmentName = departmentName;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
