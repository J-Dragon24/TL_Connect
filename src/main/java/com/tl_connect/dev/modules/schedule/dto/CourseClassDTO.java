package com.tl_connect.dev.modules.schedule.dto;

import java.time.LocalTime;

import com.tl_connect.dev.modules.lecturer.dto.LecturerDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseClassDTO {
    String classCode;
    int dayOfWeek;
    String subjectName;
    String subjectCode;
    int startPeriod;
    int endPeriod;
    LocalTime startTime;
    LocalTime endTime;
    String room;
    LecturerDTO lecturer;
}
