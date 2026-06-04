package com.tl_connect.dev.modules.student.dto;

import java.time.LocalDate;

import com.tl_connect.dev.shared.common.enums.HealthInsuranceStatus;

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
public class HealthInsDetailDTO {
    private String insuranceNumber;
    private String provider;
    private HealthInsuranceStatus status;
    private LocalDate validFrom;
    private LocalDate validTo;
    private String registeredHospital;
}
