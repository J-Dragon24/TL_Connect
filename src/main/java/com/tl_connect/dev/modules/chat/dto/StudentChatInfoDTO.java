package com.tl_connect.dev.modules.chat.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentChatInfoDTO {
    private String studentCode;
    private String fullName;
    private String classCode;
    private String majorName;
    private String position;
}
