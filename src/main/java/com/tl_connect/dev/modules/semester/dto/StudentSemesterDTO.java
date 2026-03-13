package com.tl_connect.dev.modules.semester.dto;

import java.time.LocalDate;

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
public class StudentSemesterDTO {
    String semesterName;
    String academicYear;
    Integer semesterNumber;
    LocalDate startDate;
    LocalDate endDate;
}
