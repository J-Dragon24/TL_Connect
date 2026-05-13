package com.tl_connect.dev.modules.feedback.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.tl_connect.dev.shared.common.enums.FeedbackStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackDTO {
    Long id;
    String email;
    String title;
    String content;
    String categoryName;
    String appVersion;
    String deviceInfo;
    List<String> feedbackImages;
    FeedbackStatus status;
    LocalDateTime createdAt;
}
