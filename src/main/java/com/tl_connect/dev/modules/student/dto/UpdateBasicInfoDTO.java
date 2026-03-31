package com.tl_connect.dev.modules.student.dto;

import java.time.LocalDate;

import com.tl_connect.dev.core.common.enums.Gender;
import com.tl_connect.dev.core.common.enums.IdCardType;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBasicInfoDTO {
    @Size(min = 1, message = "Student code cannot be blank")
    private String studentCode;

    @Size(min = 1, message = "Full name cannot be blank")
    private String fullName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    private Gender gender;

    @Size(min = 5, max = 20, message = "Card number invalid")
    private String cardNumber;
    
    private IdCardType cardType;

    @Past(message = "Issued date must be in the past")
    private LocalDate issuedDate;

    @Size(min = 1, message = "Issued place cannot be blank")
    private String issuedPlace;

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
