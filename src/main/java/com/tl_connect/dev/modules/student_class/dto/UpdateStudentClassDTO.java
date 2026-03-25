package com.tl_connect.dev.modules.student_class.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentClassDTO {

    @Size(min = 1, max = 20, message = "Class code can not be blank")
    private String classCode;

    @Size(min = 1, max = 20, message = "Major ID can not be blank")
    private Long majorId;

    @Min(value = 1900, message = "Start year must be greater than or equal to 1900")
    private Integer startYear;
}
