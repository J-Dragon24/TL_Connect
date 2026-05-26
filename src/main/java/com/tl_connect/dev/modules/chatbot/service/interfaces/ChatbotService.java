package com.tl_connect.dev.modules.chatbot.service.interfaces;

import com.tl_connect.dev.modules.chatbot.dto.AIContextDTO;

public interface ChatbotService {
    AIContextDTO getAIContext(Long studentId);
}
