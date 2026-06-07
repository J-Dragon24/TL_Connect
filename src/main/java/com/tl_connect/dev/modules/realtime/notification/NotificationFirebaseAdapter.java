package com.tl_connect.dev.modules.realtime.notification;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.tl_connect.dev.modules.oauth.service.interfaces.UserDeviceService;
import com.tl_connect.dev.modules.realtime.common.FirebaseGateway;
import com.tl_connect.dev.modules.realtime.notification.dto.NotificationRealtimeDTO;
import com.tl_connect.dev.shared.common.enums.NotificationType;
import com.tl_connect.dev.shared.ultility.NotificationHelper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationFirebaseAdapter {
    private final FirebaseGateway firebaseGateway;
    private final UserDeviceService userDeviceService;
    private final NotificationHelper notificationHelper;
    
    @Async
    public void send(NotificationRealtimeDTO dto, List<Long> targetIds) {
        Set<String> sentTopics = new HashSet<>();

        if(dto.getTargetType() == NotificationType.GLOBAL) {
            String topic = notificationHelper.buildTopic(dto.getTargetType(), null);
            firebaseGateway.sendToTopic(topic, dto.getTitle(), dto.getContent());
        }
        else if(dto.getTargetType() == NotificationType.STUDENT) {
            List<String> tokens = userDeviceService.findTokensByUserIds(targetIds);
            for (String token : tokens) {
                try{
                    firebaseGateway.sendToToken(token, dto.getTitle(), dto.getContent());
                }catch(FirebaseMessagingException e){
                    if(e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED){
                        userDeviceService.removeToken(token);
                    }
                }
            }
        } else {
            for(Long id : targetIds) {
                String topic = notificationHelper.buildTopic(dto.getTargetType(), id);
                if(sentTopics.contains(topic)) continue;
                firebaseGateway.sendToTopic(topic, dto.getTitle(), dto.getContent());
                sentTopics.add(topic);
            }
        }
    }
}
