package com.tl_connect.dev.modules.enroll.dto.dag;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PrerequisiteGroup {
    private Long groupId;
    private int minRequired;
    private List<Long> prereqs = new ArrayList<>();
}
