package com.tl_connect.dev.modules.chatbot;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import com.tl_connect.dev.modules.chatbot.dto.ChatbotRequest;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.MediaType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseBodyEmitter chat(@RequestBody ChatbotRequest request) {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        CompletableFuture.runAsync(() -> chatbotService.streamMessage(request.getMessage(), emitter));
        return emitter;
    }
}
