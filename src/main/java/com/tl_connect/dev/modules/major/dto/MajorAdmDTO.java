package com.tl_connect.dev.modules.major.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MajorAdmDTO {
    private Long id;
    private String majorName;
    private String majorCode;
    private String facultyCode;
    private Boolean isActive;
}
