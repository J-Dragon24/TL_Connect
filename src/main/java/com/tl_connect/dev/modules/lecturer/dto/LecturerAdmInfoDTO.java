package com.tl_connect.dev.modules.lecturer.dto;

import com.tl_connect.dev.shared.common.enums.LecturerStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LecturerAdmInfoDTO {
    private Long id;
    private String lecturerCode;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String departmentName;
    private Boolean isAcademicAdvisor;
    private LecturerStatus status;
}
