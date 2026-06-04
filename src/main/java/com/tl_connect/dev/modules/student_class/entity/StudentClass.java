package com.tl_connect.dev.modules.student_class.entity;

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
@Table(name = "student_classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_code", length = 20)
    private String classCode;

    @Column(name = "major_id", nullable = false)
    private Long majorId;

    @Column(name = "start_year", nullable = false)
    private Integer startYear;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static StudentClass create(String classCode, Long majorId, Integer startYear) {
        if(startYear < 1900){
            throw new InvalidInputException("Invalid start year");
        }
        return StudentClass.builder()
                .classCode(classCode)
                .majorId(majorId)
                .startYear(startYear)
                .build();
    }

    public void update(String classCode, Long majorId, Integer startYear) {
        if(startYear != null && startYear < 1900){
            throw new InvalidInputException("Invalid start year");
        }
        if (classCode != null) {
            this.classCode = classCode;
        }
        if (majorId != null) {
            this.majorId = majorId;
        }
        if (startYear != null) {
            this.startYear = startYear;
        }
    }
}
