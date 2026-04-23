package com.tl_connect.dev.modules.notification.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.enums.NotificationType;
import com.tl_connect.dev.core.common.ultility.NotificationHelper;
import com.tl_connect.dev.modules.notification.entity.Notification;
import com.tl_connect.dev.modules.notification.service.interfaces.FCMService;
import com.tl_connect.dev.modules.oauth.repository.UserDeviceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationPushService {
    private final FCMService fcmService;
    private final UserDeviceRepository userDeviceRepository;
    private final NotificationHelper notificationHelper;
    
    @Async
    public void pushNotifications(Notification notification, List<Long> targetIds) {
        Set<String> sentTopics = new HashSet<>();
        if(notification.getTargetType() == NotificationType.STUDENT) {
            List<String> tokens = userDeviceRepository.findTokensByUserIds(targetIds);
            for (String token : tokens) {
                fcmService.sendToToken(token, notification.getTitle(), notification.getContent());
            }
        } else {
            for(Long id : targetIds) {
                String topic = notificationHelper.buildTopic(notification.getTargetType(), id);
                if(sentTopics.contains(topic)) continue;
                fcmService.sendToTopic(topic, notification.getTitle(), notification.getContent());
                sentTopics.add(topic);
            }
        }
    }
}
