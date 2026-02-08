package com.tl_connect.dev.student_class.dto;

import com.tl_connect.dev.common.enums.Gender;

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
public class StudentInClassDTO {
    private String studentCode;
    private String fullName;
    private Gender gender;
}
