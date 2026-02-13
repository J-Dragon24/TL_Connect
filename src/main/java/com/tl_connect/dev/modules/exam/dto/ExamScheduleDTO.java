package com.tl_connect.dev.modules.exam.dto;

import java.util.List;

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
public class ExamScheduleDTO {
    private String semesterName;
    private List<ExamScheduleDetailDTO> examSchedules;
}
