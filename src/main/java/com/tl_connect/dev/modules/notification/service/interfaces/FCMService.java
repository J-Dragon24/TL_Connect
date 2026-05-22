package com.tl_connect.dev.modules.notification.service.interfaces;

import java.util.List;

import com.google.firebase.messaging.FirebaseMessagingException;

public interface FCMService {
    void sendToTopic(String topic, String title, String body);
    void sendToTopics(List<String> topics, String title, String body);
    void sendToTokens(List<String> tokens, String title, String body);
    void sendToToken(String token, String title, String body) throws FirebaseMessagingException;
}
