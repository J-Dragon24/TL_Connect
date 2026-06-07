package com.tl_connect.dev.modules.realtime.common;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirebaseGateway {
    public void sendToTopic(String topic, String title, String body) {
        Message message = Message.builder()
                .setTopic(topic)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("title", title)
                .putData("body", body)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM sent to topic {}: {}", topic, response);
        } catch (Exception e) {
            log.error("FCM send failed for topic {}", topic, e);
        }
    }

    public void sendToTopics(List<String> topics, String title, String body) {
        for (String topic : topics) {
            sendToTopic(topic, title, body);
        }
    }

    public void sendToTokens(List<String> tokens, String title, String body) {
        MulticastMessage message = MulticastMessage.builder()
            .addAllTokens(tokens)
            .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
            .putData("title", title)
            .putData("body", body)
            .build();
        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            List<SendResponse> responses = response.getResponses();
            for (int i = 0; i < responses.size(); i++) {
                if (!responses.get(i).isSuccessful()) {
                    String failedToken = tokens.get(i);
                    log.error("FCM send failed for token {}", failedToken);
                }
            }
        } catch (Exception e) {
            log.error("FCM send failed for tokens {}", tokens, e);
        }
    }

    public void sendToToken(String token, String title, String body) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("title", title)
                .putData("body", body)
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        log.info("FCM sent to token {}: {}", token, response);
    }
}
