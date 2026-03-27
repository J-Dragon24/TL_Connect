package com.tl_connect.dev.modules.exam.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamScheduleBasicInfoDTO {
    private Long id;
    private String subjectCode;
    private String classCode;
    private LocalDate examDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String examRoom;
    private String examLocation;
    private String examFormat;
    private String examType;
}