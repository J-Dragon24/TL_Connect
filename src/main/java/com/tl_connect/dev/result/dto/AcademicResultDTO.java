package com.tl_connect.dev.result.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicResultDTO {
    private String trainingProgram;
    private List<SemesterResultDTO> semesterResults;
}
