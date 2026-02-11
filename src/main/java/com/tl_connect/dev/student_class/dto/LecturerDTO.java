package com.tl_connect.dev.student_class.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LecturerDTO {
    private String lecturerCode;
    private String fullName;
    private String phoneNumber;
    private String email;
}
