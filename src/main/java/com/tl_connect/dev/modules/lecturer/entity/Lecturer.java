package com.tl_connect.dev.modules.lecturer.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tl_connect.dev.shared.common.enums.LecturerStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lecturers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lecturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "oauth_user_id", unique = true)
    private Long oauthUserId;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "lecturer_code", nullable = false, unique = true)
    private String lecturerCode;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LecturerStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static Lecturer create(String lecturerCode, String fullName, String email, String phoneNumber, Long departmentId) {
        Lecturer lecturer = new Lecturer();
        lecturer.lecturerCode = lecturerCode;
        lecturer.fullName = fullName;
        lecturer.email = email;
        lecturer.phoneNumber = phoneNumber;
        if(departmentId != null) lecturer.departmentId = departmentId;
        lecturer.status = LecturerStatus.ACTIVE;
        return lecturer;
    }

    public void update(String lecturerCode, String fullName, String email, String phoneNumber, Long departmentId) {
        if(lecturerCode != null) this.lecturerCode = lecturerCode;
        if(fullName != null) this.fullName = fullName;
        if(email != null) this.email = email;
        if(phoneNumber != null) this.phoneNumber = phoneNumber;
        if(departmentId != null) this.departmentId = departmentId;
    }

    public void deactivate() {
        this.status = LecturerStatus.INACTIVE;
    }
}
