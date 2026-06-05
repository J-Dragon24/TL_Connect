package com.tl_connect.dev.modules.exam.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.tl_connect.dev.shared.common.enums.ExamType;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateExamScheduleDTO {
    private Long id;
    private Long subjectId;
    private Long semesterId;
    private LocalDate examDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String examRoom;
    private String examLocation;
    private String examFormat;
    private ExamType examType;
    private String note;

    @AssertTrue(message = "End time must be greater than start time")
    public boolean isValidTimeRange() {
        if (startTime == null || endTime == null) return true;
        return endTime.isAfter(startTime);
    }
}
