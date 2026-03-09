package com.tl_connect.dev.modules.chatbot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatbotService {
    @Value("${chatbot.api-key}")
    private String apiKey;

    @Value("${chatbot.domain}")
    private String domain;

    private final RestClient restClient;

    public void streamMessage(String message, ResponseBodyEmitter emitter) {
        restClient.post()
                .uri(domain + "/api/v1/stream")
                .header("Authorization", "Bearer " + apiKey)
                .body(Map.of("message", message))
                .exchange((request, response) -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(response.getBody()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            emitter.send(line);
                        }
                    }
                    emitter.complete();
                    return null;
                });
    }
}
