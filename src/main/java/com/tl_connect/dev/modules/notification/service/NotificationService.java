package com.tl_connect.dev.modules.notification.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.course_class.CourseClassRepository;
import com.tl_connect.dev.modules.notification.dto.DetailNotifyDTO;
import com.tl_connect.dev.modules.notification.dto.NotificationAdmDTO;
import com.tl_connect.dev.modules.notification.dto.NotificationReqDTO;
import com.tl_connect.dev.modules.notification.dto.PrepareNotificationDTO;
import com.tl_connect.dev.modules.notification.dto.SummaryNotifyDTO;
import com.tl_connect.dev.modules.notification.dto.UnreadNotificationDTO;
import com.tl_connect.dev.modules.notification.entity.Notification;
import com.tl_connect.dev.modules.notification.projection.NotificationAdmRow;
import com.tl_connect.dev.modules.notification.projection.NotificationRow;
import com.tl_connect.dev.modules.notification.projection.PrepareNotificationView;
import com.tl_connect.dev.modules.notification.repository.NotificationReadRepository;
import com.tl_connect.dev.modules.notification.repository.NotificationRepository;
import com.tl_connect.dev.modules.student.entity.Student;
import com.tl_connect.dev.modules.student.repository.StudentRepository;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.enums.NotificationType;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.shared.common.ultility.NotificationHelper;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final CourseClassRepository courseClassRepository;
    private final NotificationReadRepository notificationReadRepository;
    private final StudentRepository studentRepository;
    private final NotificationHelper notificationHelper;

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
        topics.add(notificationHelper.buildTopic(NotificationType.GLOBAL, null));
        topics.add(notificationHelper.buildTopic(NotificationType.FACULTY, prepareNotificationView.getFacultyId()));
        topics.add(notificationHelper.buildTopic(NotificationType.STUDENT_CLASS, prepareNotificationView.getStudentClassId()));
        if (courseClassIds != null && !courseClassIds.isEmpty()) {
            for (Long courseClassId : courseClassIds) {
                topics.add(notificationHelper.buildTopic(NotificationType.COURSE_CLASS, courseClassId));
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

        Page<NotificationRow> notificationRows = notificationRepository.findAllNotificationByStudent(studentId, notificationReqDTO.getOauthUserId(), notificationReqDTO.getStudentClassId(), notificationReqDTO.getFacultyId(), notificationReqDTO.getCourseClassIds(), pageable);

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
        if (notificationReqDTO.getCourseClassIds() == null || notificationReqDTO.getCourseClassIds().isEmpty()) {
            notificationReqDTO.setCourseClassIds(List.of(-1L));
        }
        Long count = notificationRepository.countUnreadNotification(studentId, notificationReqDTO.getOauthUserId(), notificationReqDTO.getStudentClassId(), notificationReqDTO.getFacultyId(), notificationReqDTO.getCourseClassIds());
        return UnreadNotificationDTO.builder().count(count).build();
    }

    public PagedResponse<NotificationAdmDTO> getAllNotification(Pageable pageable) {
        Page<NotificationAdmRow> notifications = notificationRepository.findAllNotifications(pageable);
        List<NotificationAdmDTO> notificationList = notifications.stream()
                .map(notification -> NotificationAdmDTO.builder()
                        .id(notification.getId())
                        .title(notification.getTitle())
                        .content(notification.getContent())
                        .createdBy(notification.getCreatedBy())
                        .targetType(notification.getTargetType())
                        .targetIds(notification.getTargetIds())
                        .deadLine(notification.getDeadLine())
                        .isImportant(notification.getIsImportant())
                        .build())
                .collect(Collectors.toList());
        return new PagedResponse<>(
            notificationList,
            notifications.getNumber(),
            notifications.getSize(),
            notifications.getTotalElements(),
            notifications.getTotalPages(),
            notifications.isFirst(),
            notifications.isLast());
    }

    @Transactional
    public void markNotificationAsRead(Long studentId, List<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return;
        }
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));
        List<Long> validNotificationIds = notificationRepository.existsByIdIn(notificationIds.toArray(new Long[0]));
        if (validNotificationIds.isEmpty()) {
            return;
        }
        notificationReadRepository.markAsRead(student.getOauthUserId(), validNotificationIds.toArray(new Long[0]));
    }
}
