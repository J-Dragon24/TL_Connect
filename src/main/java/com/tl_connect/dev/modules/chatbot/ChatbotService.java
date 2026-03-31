// package com.tl_connect.dev.modules.chatbot;

// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.nio.charset.StandardCharsets;
// import java.util.Map;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestClient;
// import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

// @Service
// public class ChatbotService {
//     @Value("${chatbot.api-key}")
//     private String apiKey;

//     @Value("${chatbot.domain}")
//     private String domain;

//     private final RestClient restClient = RestClient.create();

//     public void streamMessage(String prompt, ResponseBodyEmitter emitter) {
//         restClient.post()
//                 .uri(domain + "/api/v1/stream")
//                 .header("Authorization", "Bearer " + apiKey)
//                 .body(Map.of("prompt", prompt))
//                 .exchange((request, response) -> {
//                     try (BufferedReader reader = new BufferedReader(
//                             new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
//                         String line;
//                         while ((line = reader.readLine()) != null) {
//                             emitter.send(line);
//                         }
//                     }
//                     emitter.complete();
//                     return null;
//                 });
//     }
// }
