package com.tl_connect.dev.modules.notification;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.notification.dto.DetailNotifyDTO;
import com.tl_connect.dev.modules.notification.dto.PrepareNotificationDTO;
import com.tl_connect.dev.modules.notification.dto.SummaryNotifyDTO;
import com.tl_connect.dev.modules.notification.dto.UnreadNotificationDTO;
import com.tl_connect.dev.modules.notification.projection.NotificationRow;
import com.tl_connect.dev.modules.notification.projection.PrepareNotificationView;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    private PrepareNotificationDTO prepareNotification(Long studentId) {
        PrepareNotificationView prepareNotificationView = notificationRepository.getStudentInfo(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));
        return PrepareNotificationDTO.builder()
                .studentClassId(prepareNotificationView.getStudentClassId())
                .oauthUserId(prepareNotificationView.getOauthUserId())
                .facultyId(prepareNotificationView.getFacultyId())
                .build();
    }

    public List<SummaryNotifyDTO> getAllNotification(Long studentId) {

        PrepareNotificationDTO studentInfo = prepareNotification(studentId);

        List<NotificationRow> notificationRows = notificationRepository.findAllNotification(studentId, studentInfo.getOauthUserId(), studentInfo.getStudentClassId(), studentInfo.getFacultyId());

        return notificationRows.stream()
                .map(notificationRow -> SummaryNotifyDTO.builder()
                        .id(notificationRow.getId())
                        .title(notificationRow.getTitle())
                        .content(notificationRow.getContent())
                        .createdBy(notificationRow.getCreatedBy())
                        .targetType(notificationRow.getTargetType())
                        .deadLine(notificationRow.getDeadLine())
                        .createdAt(notificationRow.getCreatedAt())
                        .isRead(notificationRow.getIsRead())
                        .build())
                .collect(Collectors.toList());
    }

    public DetailNotifyDTO getDetailNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        return DetailNotifyDTO.builder()
                .title(notification.getTitle())
                .content(notification.getContent())
                .createdBy(notification.getCreatedBy())
                .targetType(notification.getTargetType())
                .deadLine(notification.getDeadLine())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    public UnreadNotificationDTO countUnreadNotification(Long studentId) {
        PrepareNotificationDTO studentInfo = prepareNotification(studentId);
        Long count = notificationRepository.countUnreadNotification(studentId, studentInfo.getOauthUserId(), studentInfo.getStudentClassId(), studentInfo.getFacultyId());
        return UnreadNotificationDTO.builder().count(count).build();
    }
}
