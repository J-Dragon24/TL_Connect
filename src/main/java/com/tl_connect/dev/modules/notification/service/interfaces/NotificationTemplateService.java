package com.tl_connect.dev.modules.notification.service.interfaces;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.notification.dto.CreateNotificationTemplateDTO;
import com.tl_connect.dev.modules.notification.dto.NotificationTemplateDTO;
import com.tl_connect.dev.modules.notification.dto.UpdateNotificationTemplateDTO;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

public interface NotificationTemplateService {

    PagedResponse<NotificationTemplateDTO> getAllNotificationTemplates(Pageable pageable);

    Long createNotificationTemplate(CreateNotificationTemplateDTO createNotificationTemplateDTO);

    void updateNotificationTemplate(Long id, UpdateNotificationTemplateDTO updateNotificationTemplateDTO);

    void deleteNotificationTemplate(Long id);
}
