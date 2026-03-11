package com.tl_connect.dev.modules.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrepareNotificationDTO {
    private Long studentClassId;
    private Long oauthUserId;
    private Long facultyId;
}
