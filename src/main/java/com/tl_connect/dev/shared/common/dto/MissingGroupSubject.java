package com.tl_connect.dev.shared.common.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MissingGroupSubject {
    private Long groupId;
    private int needMore;
    private List<String> missingSubjectCodes;
}
