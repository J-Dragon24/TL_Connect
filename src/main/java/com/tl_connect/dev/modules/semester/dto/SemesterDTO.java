package com.tl_connect.dev.modules.semester.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemesterDTO {
    private Long id;
    private String semesterName;
    private String semesterCode;
    private String academicYears;
    private Integer semesterNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
}
