package com.tl_connect.dev.modules.notification.dto;

import java.time.LocalDate;
import java.util.List;

import com.tl_connect.dev.shared.common.enums.NotificationCreatedBy;
import com.tl_connect.dev.shared.common.enums.NotificationType;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateNotificationReqDTO {
    @NotNull(message = "Title is required")
    private String title;
    private String content;
    @NotNull(message = "Target type is required")
    private NotificationType targetType;
    private List<Long> targetIds;
    private NotificationCreatedBy createdBy;
    private LocalDate deadLine;
    @NotNull(message = "Is important is required")
    private Boolean isImportant;

    @AssertTrue(message = "Target ids is required")
    public boolean isTargetIdsValid() {
        if (targetType == NotificationType.GLOBAL) {
            return true;
        }
        return targetIds != null && !targetIds.isEmpty();
    }
}
