// package com.tl_connect.dev.modules.chatbot;

// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

// import com.tl_connect.dev.modules.chatbot.dto.ChatbotRequest;

// import java.util.concurrent.CompletableFuture;

// import org.springframework.http.MediaType;

// import lombok.RequiredArgsConstructor;

// @RestController
// @RequestMapping("/api/v1/chatbot")
// @RequiredArgsConstructor
// public class ChatbotController {

//     private final ChatbotService chatbotService;

//     @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//     public SseEmitter chat(@RequestBody ChatbotRequest request) {
//         SseEmitter emitter = new SseEmitter(0L);


//         CompletableFuture.runAsync(() -> {
//             try {
//                 chatbotService.streamMessage(request.getPrompt(), emitter);
//             } catch (Exception e) {
//                 emitter.completeWithError(e);
//             }
//     });
//         return emitter;
//     }

//     @PostMapping(value ="/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//     public SseEmitter chat(@RequestBody ChatbotRequest request, @PathVariable("id") String id) {
//         SseEmitter emitter = new SseEmitter(0L);


//         CompletableFuture.runAsync(() -> {
//             try {
//                 chatbotService.streamMessage(request.getPrompt(), emitter);
//             } catch (Exception e) {
//                 emitter.completeWithError(e);
//             }
//     });
//         return emitter;
//     }
// }
