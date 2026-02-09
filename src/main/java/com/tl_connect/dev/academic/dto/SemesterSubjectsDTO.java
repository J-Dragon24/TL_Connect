package com.tl_connect.dev.academic.dto;

import java.time.LocalDate;
import java.util.List;

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
public class SemesterSubjectsDTO {
    private String semesterName;
    private LocalDate semesterStartDate;
    private LocalDate semesterEndDate;
    private List<SubjectDTO> subjects;
}
