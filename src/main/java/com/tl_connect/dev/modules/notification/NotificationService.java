package com.tl_connect.dev.modules.notification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.course_class.CourseClassRepository;
import com.tl_connect.dev.modules.notification.dto.DetailNotifyDTO;
import com.tl_connect.dev.modules.notification.dto.NotificationReqDTO;
import com.tl_connect.dev.modules.notification.dto.PrepareNotificationDTO;
import com.tl_connect.dev.modules.notification.dto.SummaryNotifyDTO;
import com.tl_connect.dev.modules.notification.dto.UnreadNotificationDTO;
import com.tl_connect.dev.modules.notification.entity.Notification;
import com.tl_connect.dev.modules.notification.projection.NotificationRow;
import com.tl_connect.dev.modules.notification.projection.PrepareNotificationView;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final CourseClassRepository courseClassRepository;

    @Cacheable(value = "notificationTopics", key = "#studentId")
    public PrepareNotificationDTO prepareNotification(Long studentId) {
        LocalDate now = LocalDate.now();
        PrepareNotificationView prepareNotificationView = notificationRepository.getStudentInfo(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));
        List<Long> courseClassIds = courseClassRepository.findIdsByStudentIdAndSemesterId(studentId, now);

        if (courseClassIds == null) {
            courseClassIds = new ArrayList<>();
        }
        List<String> topics = new ArrayList<>();
        topics.add("GLOBAL");
        topics.add("FACULTY_" + prepareNotificationView.getFacultyId());
        topics.add("CLASS_" + prepareNotificationView.getStudentClassId());
        if (courseClassIds != null && !courseClassIds.isEmpty()) {
            for (Long courseClassId : courseClassIds) {
                topics.add("COURSE_" + courseClassId);
            }
        }
        return PrepareNotificationDTO.builder()
                .studentClassId(prepareNotificationView.getStudentClassId())
                .oauthUserId(prepareNotificationView.getOauthUserId())
                .facultyId(prepareNotificationView.getFacultyId())
                .courseClassIds(courseClassIds)
                .topics(topics)
                .build();
    }

    public PagedResponse<SummaryNotifyDTO> getAllNotification(Long studentId, NotificationReqDTO notificationReqDTO, Pageable pageable) {

        if (notificationReqDTO.getCourseClassIds() == null || notificationReqDTO.getCourseClassIds().isEmpty()) {
            notificationReqDTO.setCourseClassIds(List.of(-1L));
        }

        Page<NotificationRow> notificationRows = notificationRepository.findAllNotification(studentId, notificationReqDTO.getOauthUserId(), notificationReqDTO.getStudentClassId(), notificationReqDTO.getFacultyId(), notificationReqDTO.getCourseClassIds(), pageable);

        List<SummaryNotifyDTO> notificationList = notificationRows.stream()
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

        return new PagedResponse<>(
                notificationList,
                notificationRows.getNumber(),
                notificationRows.getSize(),
                notificationRows.getTotalElements(),
                notificationRows.getTotalPages(),
                notificationRows.isFirst(),
                notificationRows.isLast());
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

    public UnreadNotificationDTO countUnreadNotification(Long studentId, NotificationReqDTO notificationReqDTO) {
        Long count = notificationRepository.countUnreadNotification(studentId, notificationReqDTO.getOauthUserId(), notificationReqDTO.getStudentClassId(), notificationReqDTO.getFacultyId(), notificationReqDTO.getCourseClassIds());
        return UnreadNotificationDTO.builder().count(count).build();
    }
}
