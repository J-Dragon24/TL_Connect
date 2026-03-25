package com.tl_connect.dev.modules.student_class.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateStudentClassDTO {
    @NotBlank(message = "Class code is required")
    private String classCode;

    @NotNull(message = "Major id is required")
    @Min(value = 1, message = "Major id must be greater than 0")
    private Long majorId;

    @NotNull(message = "Start year is required")
    @Min(value = 1900, message = "Start year must be greater than or equal to 1900")
    private Integer startYear;
}
