package com.tl_connect.dev.modules.application.dto;

import jakarta.validation.constraints.NotBlank;
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
public class CreateApplicationTypeDTO {
    @NotBlank(message = "Application type code is required")
    private String code;
    private String name;
}
