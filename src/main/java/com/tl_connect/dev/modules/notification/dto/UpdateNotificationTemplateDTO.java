package com.tl_connect.dev.modules.notification.dto;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationTemplateDTO {
    @Size(min = 1, max = 20, message = "Code must be between 1 and 20 characters")
    private String code;
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;
    @Size(min = 1, message = "Content must be at least 1 character")
    private String content;
}
