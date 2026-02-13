package com.tl_connect.dev.modules.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestDTO {
    private Long studentId;
    private String requestType;
    private String content;
    private String evidenceFile;
    private String status;
}
