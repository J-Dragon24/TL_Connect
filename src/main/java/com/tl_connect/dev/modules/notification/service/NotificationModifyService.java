package com.tl_connect.dev.modules.notification.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.tl_connect.dev.shared.common.exception.BadRequestException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationModifyService {

    private final NotificationRepository notificationRepository;
    private final NotificationPushService notificationPushService;
    private final NotificationTargetRepository notificationTargetRepository;

    @Transactional
    public void sendNotification(CreateNotificationReqDTO req) {
        NotificationType type = req.getTargetType();

        if(type == NotificationType.GLOBAL) {
            Notification notification = buildNotification(req);
            notificationRepository.save(notification);
            notificationPushService.pushNotifications(notification, null);
            return;
        }
    
        Notification notification = buildNotification(req);
        List<NotificationTarget> notificationTargets = new ArrayList<>();

        for(Long targetId : req.getTargetIds()){
            NotificationTarget notificationTarget = NotificationTarget.create(notification.getId(), targetId);
            notificationTargets.add(notificationTarget);
        }

        try{
            notificationRepository.save(notification);
            notificationTargetRepository.saveAll(notificationTargets);
        }
        catch(DataIntegrityViolationException e){
            throw new BadRequestException("Failed to create notification" + e.getMessage());
        }

        notificationPushService.pushNotifications(notification, req.getTargetIds());
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

        notification.update(req.getTitle(), req.getContent(), req.getCreatedBy(), req.getTargetType(), req.getDeadLine(), req.getIsImportant());

        try{
            notificationRepository.save(notification);
            notificationTargetRepository.saveAll(notificationTargets);
        }
        catch(DataIntegrityViolationException e){
            throw new BadRequestException("Failed to update notification");
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

        Optional.ofNullable(req.getDeadLine()).ifPresent(n::setDeadLine);

        return n;
    }
}
