package com.tl_connect.dev.modules.chatbot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatbotRequest {
    @NotBlank
    private String prompt;
}
