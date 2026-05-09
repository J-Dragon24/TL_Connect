package com.tl_connect.dev.shared.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetailException {
    private String field;
    private String resource;
}
