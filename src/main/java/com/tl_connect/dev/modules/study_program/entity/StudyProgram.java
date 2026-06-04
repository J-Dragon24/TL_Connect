package com.tl_connect.dev.modules.study_program.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tl_connect.dev.shared.common.enums.TrainingType;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "study_programs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "major_id", nullable = false)
    private Long majorId;

    @Column(name = "study_program_name", nullable = false)
    private String studyProgramName;

    @Column(name = "study_program_code", nullable = false)
    private String studyProgramCode;

    @Column(name = "start_year", nullable = false)
    private Integer startYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "training_type", nullable = false)
    private TrainingType trainingType;

    @Column(name = "total_credits")
    private Integer totalCredits;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static StudyProgram create(String studyProgramCode, String studyProgramName, Long majorId, Integer startYear, TrainingType trainingType, Integer totalCredits) {
        if(startYear < 1900){
            throw new InvalidInputException("Invalid start year");
        }
        StudyProgram studyProgram = new StudyProgram();
        studyProgram.setStudyProgramCode(studyProgramCode);
        studyProgram.setStudyProgramName(studyProgramName);
        studyProgram.setMajorId(majorId);
        studyProgram.setStartYear(startYear);
        studyProgram.setTrainingType(trainingType);
        studyProgram.setTotalCredits(totalCredits);
        return studyProgram;
    }

    public void update(String studyProgramCode, String studyProgramName, Long majorId, Integer startYear, TrainingType trainingType, Integer totalCredits) {
        if(startYear != null && startYear < 1900){
            throw new InvalidInputException("Invalid start year");
        }
        if (studyProgramCode != null) {
            this.studyProgramCode = studyProgramCode;
        }
        if (studyProgramName != null) {
            this.studyProgramName = studyProgramName;
        }
        if (majorId != null) {
            this.majorId = majorId;
        }
        if (startYear != null) {
            this.startYear = startYear;
        }
        if (trainingType != null) {
            this.trainingType = trainingType;
        }
        if (totalCredits != null) {
            this.totalCredits = totalCredits;
        }
    }

    public void deactivate() {
        this.isActive = false;
    }
}
