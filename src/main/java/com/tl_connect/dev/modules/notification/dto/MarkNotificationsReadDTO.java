package com.tl_connect.dev.modules.notification.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MarkNotificationsReadDTO {
    @NotEmpty(message = "Notification IDs cannot be empty")
    private List<Long> notificationIds;
}
