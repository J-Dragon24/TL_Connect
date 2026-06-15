package com.tl_connect.dev.modules.notification.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.notification.dto.CreateNotificationReqDTO;
import com.tl_connect.dev.modules.notification.dto.UpdateNotificationDTO;
import com.tl_connect.dev.modules.notification.entity.Notification;
import com.tl_connect.dev.modules.notification.entity.NotificationTarget;
import com.tl_connect.dev.modules.notification.repository.NotificationRepository;
import com.tl_connect.dev.modules.notification.repository.NotificationTargetRepository;
import com.tl_connect.dev.shared.common.enums.NotificationType;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.modules.notification.service.interfaces.NotificationModifyService;
import com.tl_connect.dev.modules.realtime.notification.dto.NotificationCreatedEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationModifyServiceImpl implements NotificationModifyService {

    private final NotificationRepository notificationRepository;
    private final NotificationTargetRepository notificationTargetRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void sendNotification(CreateNotificationReqDTO req) {
        NotificationType type = req.getTargetType();
        

        if(type == NotificationType.GLOBAL) {
            Notification notification = buildNotification(req);
            try{
                notificationRepository.save(notification);
            }
            catch(DataIntegrityViolationException e){
                throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to create notification");
            }
            publisher.publishEvent(
                NotificationCreatedEvent.builder()
                        .id(notification.getId())
                        .title(notification.getTitle())
                        .content(notification.getContent())
                        .createdBy(notification.getCreatedBy())
                        .targetType(req.getTargetType())
                        .isImportant(notification.getIsImportant())
                        .referenceType(notification.getReferenceType())
                        .deadLine(notification.getDeadLine())
                        .createdAt(notification.getCreatedAt())
                        .targetIds(null)
                        .build()
            );
            return;
        }
    
        Notification notification = buildNotification(req);

        try{
            notification = notificationRepository.save(notification);
        }
        catch(DataIntegrityViolationException e){
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to create notification" + e.getMessage());
        }

        List<NotificationTarget> notificationTargets = new ArrayList<>();

        for(Long targetId : req.getTargetIds()){
            NotificationTarget notificationTarget = NotificationTarget.create(notification.getId(), targetId);
            notificationTargets.add(notificationTarget);
        }

        try{
            notificationTargetRepository.saveAll(notificationTargets);
        }
        catch(DataIntegrityViolationException e){
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to create notification" + e.getMessage());
        }


        publisher.publishEvent(
                NotificationCreatedEvent.builder()
                        .id(notification.getId())
                        .title(notification.getTitle())
                        .content(notification.getContent())
                        .createdBy(notification.getCreatedBy())
                        .targetType(req.getTargetType())
                        .isImportant(notification.getIsImportant())
                        .referenceType(notification.getReferenceType())
                        .deadLine(notification.getDeadLine())
                        .createdAt(notification.getCreatedAt())
                        .targetIds(req.getTargetIds())
                        .build()
        );
    }

    @Transactional
    public void updateNotification(Long id, UpdateNotificationDTO req) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification not found"));

        List<Long> targetIds = null;
        List<NotificationTarget> notificationTargets = new ArrayList<>();

        if(req.getTargetIds() != null && !req.getTargetIds().isEmpty()) {
            List<NotificationTarget> existingTargets = notificationTargetRepository.findByNotificationIdAndTargetIdIn(id, req.getTargetIds());
            Set<Long> existingIds = existingTargets.stream().map(nt -> nt.getTargetId()).collect(Collectors.toSet());
            targetIds = req.getTargetIds().stream().filter(t -> !existingIds.contains(t)).collect(Collectors.toList());
        }

        if(targetIds != null && !targetIds.isEmpty()) {
            for(Long targetId : targetIds){
                NotificationTarget notificationTarget = NotificationTarget.create(notification.getId(), targetId);
                notificationTargets.add(notificationTarget);
            }
        }

        notification.update(req.getTitle(), req.getContent(), req.getCreatedBy(), req.getTargetType(), req.getDeadLine(), req.getIsImportant(), req.getReferenceType());

        try{
            notificationRepository.save(notification);
            notificationTargetRepository.saveAll(notificationTargets);
        }
        catch(DataIntegrityViolationException e){
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to update notification");
        }
    }

    @Transactional
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        notificationRepository.delete(notification);
    }

    private Notification buildNotification(CreateNotificationReqDTO req) {
        Notification n = new Notification();
        n.setTitle(req.getTitle());
        n.setContent(req.getContent());
        n.setCreatedBy(req.getCreatedBy());
        n.setTargetType(req.getTargetType());
        n.setIsImportant(req.getIsImportant());
        n.setReferenceType(req.getReferenceType());

        Optional.ofNullable(req.getDeadLine()).ifPresent(n::setDeadLine);

        return n;
    }
}
