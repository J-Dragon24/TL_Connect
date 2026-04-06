package com.tl_connect.dev.modules.notification.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.core.common.enums.NotificationType;
import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.course_class.CourseClass;
import com.tl_connect.dev.modules.course_class.CourseClassRepository;
import com.tl_connect.dev.modules.faculty.Faculty;
import com.tl_connect.dev.modules.faculty.FacultyRepository;
import com.tl_connect.dev.modules.notification.dto.CreateNotificationReqDTO;
import com.tl_connect.dev.modules.notification.dto.UpdateNotificationDTO;
import com.tl_connect.dev.modules.notification.entity.Notification;
import com.tl_connect.dev.modules.notification.entity.NotificationTemplate;
import com.tl_connect.dev.modules.notification.repository.NotificationRepository;
import com.tl_connect.dev.modules.notification.repository.NotificationTemplateRepository;
import com.tl_connect.dev.modules.student.entity.Student;
import com.tl_connect.dev.modules.student.repository.StudentRepository;
import com.tl_connect.dev.modules.student_class.StudentClassRepository;
import com.tl_connect.dev.modules.student_class.entity.StudentClass;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationModifyService {

    private final NotificationRepository notificationRepository;
    private final FacultyRepository facultyRepository;
    private final StudentClassRepository studentClassRepository;
    private final CourseClassRepository courseClassRepository;
    private final StudentRepository studentRepository;
    private final NotificationTemplateRepository notificationTemplateRepository;
    private final NotificationPushService notificationPushService;
    private final Validator validator;

    @Transactional
    public void sendNotification(CreateNotificationReqDTO req) {
        Set<ConstraintViolation<CreateNotificationReqDTO>> violations = validator.validate(req);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }
        NotificationType type = req.getTargetType();

        if(type == NotificationType.GLOBAL) {
            Notification notification = buildNotification(req, null, req.getContent());
            notificationRepository.save(notification);

            notificationPushService.pushNotifications(List.of(notification));
            return;
        }
        
        List<Notification> notifications = new ArrayList<>();

        if(req.getTemplateId() != null) {
            NotificationTemplate template = notificationTemplateRepository.findById(req.getTemplateId())
                    .orElseThrow(() -> new NotFoundException("Notification template not found"));
            Map<Long, String> targetNameMap = getTargetNames(req.getTargetType(), req.getTargetIds());
            for(Long targetId : req.getTargetIds()) {
                String targetName = Optional.ofNullable(targetNameMap.get(targetId)).orElseThrow(() -> new NotFoundException("Target name not found"));
                String content = renderTemplate(template.getContent(), Map.of("target_name", targetName));
                Notification notification = buildNotification(req, targetId, content);
                notifications.add(notification);
            }
        }
        else{
            for(Long targetId : req.getTargetIds()) {
                Notification notification = buildNotification(req, targetId, req.getContent());
                notifications.add(notification);
            }
        } 
        notificationRepository.saveAll(notifications);

        notificationPushService.pushNotifications(notifications);
    }

    @Transactional
    public void updateNotification(Long id, UpdateNotificationDTO req) {
        Set<ConstraintViolation<UpdateNotificationDTO>> violations = validator.validate(req);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification not found"));

        Optional.ofNullable(req.getTitle()).ifPresent(notification::setTitle);
        Optional.ofNullable(req.getContent()).ifPresent(notification::setContent);
        Optional.ofNullable(req.getCreatedBy()).ifPresent(notification::setCreatedBy);
        Optional.ofNullable(req.getTargetType()).ifPresent(notification::setTargetType);
        Optional.ofNullable(req.getTargetId()).ifPresent(notification::setTargetId);
        Optional.ofNullable(req.getIsImportant()).ifPresent(notification::setIsImportant);
        Optional.ofNullable(req.getDeadLine()).ifPresent(notification::setDeadLine);
        Optional.ofNullable(req.getReferenceId()).ifPresent(notification::setReferenceId);
        Optional.ofNullable(req.getReferenceType()).ifPresent(notification::setReferenceType);
        notificationRepository.save(notification);

        notificationPushService.pushNotifications(List.of(notification));
    }

    @Transactional
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        notificationRepository.delete(notification);
    }

    private Map<Long, String> getTargetNames(NotificationType type, List<Long> ids) {
    return switch (type) {
        case FACULTY -> facultyRepository.findAllById(ids)
                .stream().collect(Collectors.toMap(Faculty::getId, Faculty::getFacultyName));

        case STUDENT_CLASS -> studentClassRepository.findAllById(ids)
                .stream().collect(Collectors.toMap(StudentClass::getId, StudentClass::getClassCode));

        case COURSE_CLASS -> courseClassRepository.findAllById(ids)
                .stream().collect(Collectors.toMap(CourseClass::getId, CourseClass::getClassName));

        case STUDENT -> studentRepository.findAllById(ids)
                .stream().collect(Collectors.toMap(Student::getId, Student::getFullName));

        default -> Map.of();
    };
}

    private String renderTemplate(String template, Map<String, Object> params) {
        String result = template;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        return result;
    }

    private Notification buildNotification(
        CreateNotificationReqDTO req,
        Long targetId,
        String content
    ) {
        Notification n = new Notification();
        n.setTitle(req.getTitle());
        n.setContent(content);
        n.setCreatedBy(req.getCreatedBy());
        n.setTargetType(req.getTargetType());
        n.setTargetId(targetId);
        n.setIsImportant(req.getIsImportant());

        Optional.ofNullable(req.getDeadLine()).ifPresent(n::setDeadLine);
        Optional.ofNullable(req.getReferenceId()).ifPresent(n::setReferenceId);
        Optional.ofNullable(req.getReferenceType()).ifPresent(n::setReferenceType);

        return n;
    }
}
