package com.tl_connect.dev.modules.enroll.dto.dag;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectNode {
    private String code;
    private List<PrerequisiteGroup> groups = new ArrayList<>();
}
