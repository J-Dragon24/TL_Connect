package com.tl_connect.dev.modules.tuition.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateInvoiceReqDTO {
    @NotNull(message = "Semester ID is required")
    private Long semesterId;
}
