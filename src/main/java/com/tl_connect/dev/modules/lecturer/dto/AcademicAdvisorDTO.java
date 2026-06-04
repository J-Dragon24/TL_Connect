package com.tl_connect.dev.modules.lecturer.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicAdvisorDTO {
    private String lecturerCode;
    private String lecturerName;
    private String lecturerEmail;
    private String lecturerPhoneNumber;
    private List<String> studentClassCodes;
}