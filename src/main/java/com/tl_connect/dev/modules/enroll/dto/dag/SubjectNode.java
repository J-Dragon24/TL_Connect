package com.tl_connect.dev.modules.enroll.dto.dag;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SubjectNode {
    private String code;
    private List<PrerequisiteGroup> groups = new ArrayList<>();
}
