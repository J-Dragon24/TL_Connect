package com.tl_connect.dev.modules.student.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelfUpdateRequestDTO {

    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Invalid phone number")
    private String phoneNumber;
    private String address;
    private String email;
    private String emergencyContactName;

    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Invalid emergency phone")
    private String emergencyContactPhoneNumber;
    private String emergencyContactAddress;
    private String emergencyContactRelationship;
}
