package com.tl_connect.dev.modules.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendFeedbackRequestDTO {
    private String title;
    private Long categoryId;
    private String content;
    private String appVersion;
    private String deviceInfo;
}
