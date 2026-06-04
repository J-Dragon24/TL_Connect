package com.tl_connect.dev.modules.notification.service.interfaces;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.notification.dto.DetailNotifyDTO;
import com.tl_connect.dev.modules.notification.dto.NotificationReqDTO;
import com.tl_connect.dev.modules.notification.dto.PrepareNotificationDTO;
import com.tl_connect.dev.modules.notification.dto.SummaryNotifyDTO;
import com.tl_connect.dev.modules.notification.dto.UnreadNotificationDTO;
import com.tl_connect.dev.modules.notification.dto.NotificationAdmDTO;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

import java.util.List;

public interface NotificationService {

    PrepareNotificationDTO prepareNotification(Long studentId);

    PagedResponse<SummaryNotifyDTO> getAllNotification(Long studentId, NotificationReqDTO notificationReqDTO, Pageable pageable);

    DetailNotifyDTO getDetailNotification(Long id);

    UnreadNotificationDTO countUnreadNotification(Long studentId, NotificationReqDTO notificationReqDTO);

    PagedResponse<NotificationAdmDTO> getAllNotification(Pageable pageable);

    void markNotificationAsRead(Long studentId, List<Long> notificationIds);
}
