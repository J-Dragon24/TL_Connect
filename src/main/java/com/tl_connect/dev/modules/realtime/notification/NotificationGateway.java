package com.tl_connect.dev.modules.realtime.notification;

import java.util.List;

import com.tl_connect.dev.modules.realtime.notification.dto.NotificationRealtimeDTO;

public interface NotificationGateway {

    void send(NotificationRealtimeDTO dto, List<Long> targetIds);
}
