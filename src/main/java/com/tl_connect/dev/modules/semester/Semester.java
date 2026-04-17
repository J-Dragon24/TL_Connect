package com.tl_connect.dev.modules.semester;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tl_connect.dev.core.common.exception.InvalidInputException;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "semesters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "semester_name", nullable = false)
    private String semesterName;

    @Column(name = "semester_code", nullable = false, unique = true)
    private String semesterCode;

    @Column(name = "academic_years", nullable = false)
    private String academicYears;

    @Column(name = "semester_number", nullable = false)
    private int semesterNumber;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static Semester create(String semesterName, String semesterCode, String academicYears, int semesterNumber, LocalDate startDate, LocalDate endDate) {
        if(startDate.isAfter(endDate)){
            throw new InvalidInputException("Start date must be before end date");
        }
        if(semesterNumber < 1 || semesterNumber > 3){
            throw new InvalidInputException("Semester number must be between 1 and 3");
        }
        Semester semester = new Semester();
        semester.setSemesterName(semesterName);
        semester.setSemesterCode(semesterCode);
        semester.setAcademicYears(academicYears);
        semester.setSemesterNumber(semesterNumber);
        semester.setStartDate(startDate);
        semester.setEndDate(endDate);
        semester.setIsActive(true);
        return semester;
    }

    public void update(String semesterName, String semesterCode, String academicYears, int semesterNumber, LocalDate startDate, LocalDate endDate) {
        LocalDate newStartDate = startDate != null ? startDate : this.startDate;
        LocalDate newEndDate = endDate != null ? endDate : this.endDate;
        if (newStartDate.isAfter(newEndDate)) {
            throw new InvalidInputException("Start date must be before end date");
        }
        if (semesterNumber != 0 && (semesterNumber < 1 || semesterNumber > 3)) {
            throw new InvalidInputException("Semester number must be between 1 and 3");
        }
        if (semesterName != null) {
            this.semesterName = semesterName;
        }
        if (semesterCode != null) {
            this.semesterCode = semesterCode;
        }
        if (academicYears != null) {
            this.academicYears = academicYears;
        }
        if (semesterNumber != 0) {
            this.semesterNumber = semesterNumber;
        }
        if (startDate != null) {
            this.startDate = startDate;
        }
        if (endDate != null) {
            this.endDate = endDate;
        }
    }
}
