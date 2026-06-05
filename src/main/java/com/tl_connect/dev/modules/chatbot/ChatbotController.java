package com.tl_connect.dev.modules.chatbot;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.modules.chatbot.dto.AIContextDTO;
import com.tl_connect.dev.modules.chatbot.service.interfaces.ChatbotService;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.types.JwtUserInfo;
import com.tl_connect.dev.shared.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/student/ai-context")
@RequiredArgsConstructor
public class ChatbotController {
    
    private final ChatbotService chatbotService;

    @GetMapping()
    public ResponseEntity<?> getAIContext(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        AIContextDTO aiContext = chatbotService.getAIContext(studentId);
        return ResponseHelper.success("Get user AI context successfully", aiContext);
    }
}
