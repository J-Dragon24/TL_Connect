package com.tl_connect.dev.modules.subject.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectPrerequisiteGroupDTO {
    private Integer minSubjectsRequired;
    private String description;
    private List<SubjectPrerequisiteGroupItemDTO> items;
}
