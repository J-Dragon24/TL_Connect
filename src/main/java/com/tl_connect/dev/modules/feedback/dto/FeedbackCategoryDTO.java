package com.tl_connect.dev.modules.feedback.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackCategoryDTO {
    private Long id;
    private String name;
    private String description;
}
