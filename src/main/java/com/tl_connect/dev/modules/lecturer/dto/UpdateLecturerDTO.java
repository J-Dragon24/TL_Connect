package com.tl_connect.dev.modules.lecturer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLecturerDTO {

    @Size(min = 1, max = 20, message = "Lecturer code must be between 1 and 20 characters")
    private String lecturerCode;

    @Size(min = 1, max = 255, message = "Full name must be between 1 and 255 characters")
    private String fullName;

    @Email(message = "Invalid email format")
    @Size(min = 1, max = 255, message = "Email must be between 1 and 255 characters")
    private String email;

    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Invalid phone number")
    private String phoneNumber;

    private Long departmentId;
}
