package com.tl_connect.dev.modules.lecturer.dto;

import java.util.List;

import com.tl_connect.dev.shared.common.enums.LecturerStatus;

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
public class AcademicAdvisorDetailDTO {
    private Long id;
    private String lecturerCode;
    private String lecturerName;
    private String lecturerEmail;
    private String lecturerPhoneNumber;
    private String departmentCode;
    private LecturerStatus lecturerStatus;
    private List<ClassBasicInfoDTO> classInfo;
}
