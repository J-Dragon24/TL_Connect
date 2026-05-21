package com.tl_connect.dev.modules.notification.service.interfaces;

import java.util.List;

import com.tl_connect.dev.modules.notification.entity.Notification;

public interface NotificationPushService {

    void pushNotifications(Notification notification, List<Long> targetIds);
}
