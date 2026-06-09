package com.tl_connect.dev.modules.feedback.projection;

import java.time.LocalDateTime;

import com.tl_connect.dev.shared.common.enums.FeedbackStatus;

public interface FeedbackRow {
    Long getFeedbackId();
    String getEmail();
    String getTitle();
    String getContent();
    String getCategoryName();
    String getAppVersion();
    String getDeviceInfo();
    String getFeedbackImages();
    String getResourceTypes();
    FeedbackStatus getStatus();
    LocalDateTime getCreatedAt();
}
