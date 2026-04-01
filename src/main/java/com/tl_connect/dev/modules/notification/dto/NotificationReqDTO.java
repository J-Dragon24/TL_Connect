package com.tl_connect.dev.modules.notification.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationReqDTO {
    private Long oauthUserId;
    private Long facultyId;
    private Long studentClassId;
    private List<Long> courseClassIds;
}
