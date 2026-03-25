package com.tl_connect.dev.modules.lecturer.dto;

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
public class ClassBasicInfoDTO {
    private String classCode;
    private String majorCode;
    private Integer startYear;
}
