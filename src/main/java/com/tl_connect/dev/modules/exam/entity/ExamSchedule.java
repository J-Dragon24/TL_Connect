package com.tl_connect.dev.modules.exam.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tl_connect.dev.shared.common.enums.ExamType;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exam_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "semester_id", nullable = false)
    private Long semesterId;

    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "exam_room", nullable = false)
    private String examRoom;

    @Column(name = "exam_location")
    private String examLocation;

    @Column(name = "exam_format")
    private String examFormat;

    @Column(name = "exam_type")
    private ExamType examType;

    @Column(name = "note")
    private String note;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static ExamSchedule create(Long subjectId, Long semesterId, LocalDate examDate, LocalTime startTime, LocalTime endTime, String examRoom, String examLocation, String examFormat, ExamType examType, String note) {
        if(startTime.isAfter(endTime)){
            throw new InvalidInputException("Start time must be before end time");
        }
        ExamSchedule examSchedule = new ExamSchedule();
        examSchedule.subjectId = subjectId;
        examSchedule.semesterId = semesterId;
        examSchedule.examDate = examDate;
        examSchedule.startTime = startTime;
        examSchedule.endTime = endTime;
        examSchedule.examRoom = examRoom;
        examSchedule.examLocation = examLocation;
        examSchedule.examFormat = examFormat;
        examSchedule.examType = examType;
        examSchedule.note = note;
        return examSchedule;
    }

    public void update(Long subjectId, Long semesterId, LocalDate examDate, LocalTime startTime, LocalTime endTime, String examRoom, String examLocation, String examFormat, ExamType examType, String note) {
        LocalTime newStartTime = startTime != null ? startTime : this.startTime;
        LocalTime newEndTime = endTime != null ? endTime : this.endTime;
        if(newStartTime.isAfter(newEndTime)){
            throw new InvalidInputException("Start time must be before end time");
        }
        if(subjectId != null) this.subjectId = subjectId;
        if(semesterId != null) this.semesterId = semesterId;
        if(examDate != null) this.examDate = examDate;
        if(startTime != null) this.startTime = startTime;
        if(endTime != null) this.endTime = endTime;
        if(examRoom != null) this.examRoom = examRoom;
        if(examLocation != null) this.examLocation = examLocation;
        if(examFormat != null) this.examFormat = examFormat;
        if(examType != null) this.examType = examType;
        if(note != null) this.note = note;
    }
}
