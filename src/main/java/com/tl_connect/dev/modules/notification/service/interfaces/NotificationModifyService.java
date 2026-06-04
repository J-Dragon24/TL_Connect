package com.tl_connect.dev.modules.notification.service.interfaces;


import com.tl_connect.dev.modules.notification.dto.CreateNotificationReqDTO;
import com.tl_connect.dev.modules.notification.dto.UpdateNotificationDTO;

public interface NotificationModifyService {

    void sendNotification(CreateNotificationReqDTO req);

    void updateNotification(Long id, UpdateNotificationDTO req);

    void deleteNotification(Long id);
}
