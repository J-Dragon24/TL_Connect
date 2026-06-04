package com.tl_connect.dev.modules.enroll.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DropRequestDTO {
    @NotNull(message = "Course class ID is required")
    private Long courseClassId;
}
