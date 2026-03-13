package com.tl_connect.dev.core.common.dto;

import java.util.List;

import lombok.Value;

@Value
public class ImportResultDTO {
    private int total;
    private boolean success;
    private int failed;
    private List<String> errors;
}
