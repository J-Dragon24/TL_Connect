package com.tl_connect.dev.modules.exam.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateExamScheduleDTO {
    @NotNull(message = "Subject ID is required")
    private Long subjectId;
    @NotNull(message = "Semester ID is required")
    private Long semesterId;
    @NotNull(message = "Exam date is required")
    private LocalDate examDate;
    @NotNull(message = "Start time is required")
    private LocalTime startTime;
    @NotNull(message = "End time is required")
    private LocalTime endTime;
    @NotBlank(message = "Exam room is required")
    private String examRoom;
    private String examLocation;
    private String examFormat;
    private String examType;
    private String note;

    @AssertTrue(message = "End time must be greater than start time")
    public boolean isValidTimeRange() {
        return endTime.isAfter(startTime);
    }
}
