package com.tl_connect.dev.student.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MajorDTO {
    private String majorCode;
    private String majorName;
    private String faculty;
}
