package com.tl_connect.dev.modules.subject.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreGroupCreateDTO {
    private Integer minSubjectsRequired;
    private String description;
    private List<Long> prerequisiteSubjectIds;
}
