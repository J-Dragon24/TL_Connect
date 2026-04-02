package com.tl_connect.dev.modules.notification.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.enums.NotificationType;
import com.tl_connect.dev.core.common.ultility.NotificationHelper;
import com.tl_connect.dev.modules.notification.entity.Notification;
import com.tl_connect.dev.modules.oauth.repository.UserDeviceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationPushService {
    private final FCMService fcmService;
    private final UserDeviceRepository userDeviceRepository;
    private final NotificationHelper notificationHelper;
    
    @Async
    public void pushNotifications(List<Notification> notifications) {
        Set<String> sentTopics = new HashSet<>();
        for (Notification n : notifications) {
            if(n.getTargetType() == NotificationType.STUDENT) {
                List<String> tokens = userDeviceRepository.findTokensByUserId(n.getTargetId());
                for (String token : tokens) {
                    fcmService.sendToToken(token, n.getTitle(), n.getContent());
                }
            } else {
                String topic = notificationHelper.buildTopic(n.getTargetType(), n.getTargetId());
                if(sentTopics.contains(topic)) continue;
                fcmService.sendToTopic(topic, n.getTitle(), n.getContent());
                sentTopics.add(topic);
            }
        }
    }
}
