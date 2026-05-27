package com.tl_connect.dev.modules.chatbot.dto;


import java.time.LocalDate;
import java.util.List;

import com.tl_connect.dev.modules.semester.dto.SemesterDTO;
import com.tl_connect.dev.shared.common.enums.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class AIContextDTO {
    private String studentName;
    private String studentCode;
    private LocalDate dateOfBirth;
    private Gender gender;
    private List<SemesterDTO> semesters;
    private List<AcademicAIContext> academicInfo;
}
