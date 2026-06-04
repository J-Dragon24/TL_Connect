package com.tl_connect.dev.shared.common.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportResultDTO {
    private int total;
    private int success;
    private int failed;
}
