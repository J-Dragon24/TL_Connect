package com.tl_connect.dev.modules.lecturer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAcademicAdvisorDTO {
    private Long lecturerId;
    private Long studentClassId;
}
