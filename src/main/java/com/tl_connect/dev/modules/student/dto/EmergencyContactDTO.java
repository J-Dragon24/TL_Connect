package com.tl_connect.dev.modules.student.dto;

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
public class EmergencyContactDTO {
    private String name;
    private String phoneNumber;
    private String address;
    private String relationship;
}
