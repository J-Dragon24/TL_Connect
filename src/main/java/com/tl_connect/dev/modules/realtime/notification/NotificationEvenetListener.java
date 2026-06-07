package com.tl_connect.dev.modules.realtime.notification;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.tl_connect.dev.modules.realtime.notification.dto.NotificationCreatedEvent;
import com.tl_connect.dev.modules.realtime.notification.dto.NotificationRealtimeDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationEvenetListener {

    private final NotificationGateway gateway;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNotificationCreated(NotificationCreatedEvent event) {

        NotificationRealtimeDTO dto = NotificationRealtimeDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .content(event.getContent())
                .createdBy(event.getCreatedBy())
                .targetType(event.getTargetType())
                .isImportant(event.getIsImportant())
                .referenceType(event.getReferenceType())
                .deadLine(event.getDeadLine())
                .createdAt(event.getCreatedAt())
                .isRead(false)
                .build();

        gateway.send(
                dto,
                event.getTargetIds()
        );
    }
}
