package com.tl_connect.dev.modules.feedback.dto;

import com.tl_connect.dev.shared.common.enums.FeedbackStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusFeedbackDTO {
    @NotNull(message = "Status is required")
    private FeedbackStatus status;
}
