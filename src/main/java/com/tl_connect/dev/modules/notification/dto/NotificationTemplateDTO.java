package com.tl_connect.dev.modules.notification.dto;

import lombok.Data;

@Data
public class NotificationTemplateDTO {
    private Long id;
    private String code;
    private String name;
    private String content;
}
