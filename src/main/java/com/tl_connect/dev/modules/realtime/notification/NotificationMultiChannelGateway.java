package com.tl_connect.dev.modules.realtime.notification;

import java.util.List;

import org.springframework.stereotype.Component;

import com.tl_connect.dev.modules.realtime.notification.dto.NotificationRealtimeDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationMultiChannelGateway implements NotificationGateway {

    private final NotificationWebSocketAdapter websocketAdapter;

    private final NotificationFirebaseAdapter firebaseAdapter;

    @Override
    public void send(
            NotificationRealtimeDTO dto,
            List<Long> targetIds
    ) {

        websocketAdapter.send(
                dto,
                targetIds
        );

        firebaseAdapter.send(
                dto,
                targetIds
        );
    }
}
