package com.tl_connect.dev.modules.notification;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.notification.dto.DetailNotifyDTO;
import com.tl_connect.dev.modules.notification.dto.SummaryNotifyDTO;
import com.tl_connect.dev.modules.notification.projection.NotificationRow;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public List<SummaryNotifyDTO> getAllNotification(Long studentId) {

        List<NotificationRow> notificationRows = notificationRepository.findAllNotification(studentId);

        return notificationRows.stream()
                .map(notificationRow -> SummaryNotifyDTO.builder()
                        .id(notificationRow.getId())
                        .title(notificationRow.getTitle())
                        .sender(notificationRow.getSender())
                        .targetType(notificationRow.getTargetType())
                        .deadLine(notificationRow.getDeadLine())
                        .createdAt(notificationRow.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public DetailNotifyDTO getDetailNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        return DetailNotifyDTO.builder()
                .title(notification.getTitle())
                .content(notification.getContent())
                .sender(notification.getSender())
                .targetType(notification.getTargetType())
                .deadLine(notification.getDeadLine())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
