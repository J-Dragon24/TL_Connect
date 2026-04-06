package com.tl_connect.dev.modules.application.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateApplicationTypeDTO {
    @Size(min = 3, max = 30, message = "Code must be between 3 and 30 characters")
    private String code;
    @Size(min = 1, message = "Name must be at least 1 character")
    private String name;
}
