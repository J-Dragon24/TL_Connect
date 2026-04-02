package com.tl_connect.dev.modules.notification.dto;

import java.time.LocalDate;
import java.util.List;

import com.tl_connect.dev.core.common.enums.NotificationType;

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
    private Long templateId;
    @NotNull(message = "Title is required")
    private String title;
    private String content;
    @NotNull(message = "Target type is required")
    private NotificationType targetType;
    private List<Long> targetIds;
    private Long referenceId;
    private String referenceType;
    private String createdBy;
    private LocalDate deadLine;
    @NotNull(message = "Is important is required")
    private Boolean isImportant;

    @AssertTrue(message = "Target id is required")
    public boolean isTargetIdValid() {
        if (targetType == NotificationType.GLOBAL) {
            return true;
        }
        return targetIds != null && !targetIds.isEmpty();
    }

    @AssertTrue(message = "Content is required")
    public boolean isContentValid() {
        if (templateId != null) {
            return true;
        }
        return content != null && !content.isEmpty();
    }
}
