package com.tl_connect.dev.modules.subject.dto;

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
public class SubjectPrerequisiteGroupItemDTO {
    private String subjectCode;
    private String subjectName;
}
