package com.tl_connect.dev.modules.training_program.dto;

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
public class SubjectPrerequisiteDTO {
    private String subjectCode;
    private String subjectName;
}
