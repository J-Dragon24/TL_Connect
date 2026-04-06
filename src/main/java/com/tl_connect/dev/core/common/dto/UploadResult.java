package com.tl_connect.dev.core.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadResult {
    private String key;
    private String url;
}
