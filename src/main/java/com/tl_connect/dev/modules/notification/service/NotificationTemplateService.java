package com.tl_connect.dev.modules.notification.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.exception.BadRequestException;
import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.notification.dto.CreateNotificationTemplateDTO;
import com.tl_connect.dev.modules.notification.dto.NotificationTemplateDTO;
import com.tl_connect.dev.modules.notification.dto.UpdateNotificationTemplateDTO;
import com.tl_connect.dev.modules.notification.entity.NotificationTemplate;
import com.tl_connect.dev.modules.notification.repository.NotificationTemplateRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationTemplateService {
    private final NotificationTemplateRepository notificationTemplateRepository;
    private final Validator validator;

    public PagedResponse<NotificationTemplateDTO> getAllNotificationTemplates(Pageable pageable) {
        Page<NotificationTemplate> notificationTemplates = notificationTemplateRepository.findAllTemplate(pageable);
        List<NotificationTemplateDTO> notificationTemplateDTOs = notificationTemplates.getContent().stream()
                .map(this::toNotificationTemplateDTO)
                .collect(Collectors.toList());
        return new PagedResponse<>(
                notificationTemplateDTOs,
                notificationTemplates.getNumber(),
                notificationTemplates.getSize(),
                notificationTemplates.getTotalElements(),
                notificationTemplates.getTotalPages(),
                notificationTemplates.isFirst(),
                notificationTemplates.isLast()
        );
    }

    public Long createNotificationTemplate(CreateNotificationTemplateDTO createNotificationTemplateDTO) {
        Set<ConstraintViolation<CreateNotificationTemplateDTO>> violations = validator.validate(createNotificationTemplateDTO);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }

        NotificationTemplate notificationTemplate = new NotificationTemplate();
        notificationTemplate.setCode(createNotificationTemplateDTO.getCode());
        notificationTemplate.setName(createNotificationTemplateDTO.getName());
        notificationTemplate.setContent(createNotificationTemplateDTO.getContent());
        try{
            return notificationTemplateRepository.save(notificationTemplate).getId();
        }catch(DataIntegrityViolationException e){
            throw new BadRequestException("Create notification template failed: " + e.getMessage());
        }
    }

    public void updateNotificationTemplate(Long id, UpdateNotificationTemplateDTO updateNotificationTemplateDTO) {
        Set<ConstraintViolation<UpdateNotificationTemplateDTO>> violations = validator.validate(updateNotificationTemplateDTO);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }

        NotificationTemplate notificationTemplate = notificationTemplateRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Notification template not found"));

        Optional.ofNullable(updateNotificationTemplateDTO.getCode()).ifPresent(notificationTemplate::setCode);
        Optional.ofNullable(updateNotificationTemplateDTO.getName()).ifPresent(notificationTemplate::setName);
        Optional.ofNullable(updateNotificationTemplateDTO.getContent()).ifPresent(notificationTemplate::setContent);
        try{
            notificationTemplateRepository.save(notificationTemplate);
        }catch(DataIntegrityViolationException e){
            throw new BadRequestException("Update notification template failed: " + e.getMessage());
        }
    }

    public void deleteNotificationTemplate(Long id) {
        NotificationTemplate notificationTemplate = notificationTemplateRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Notification template not found"));
        notificationTemplateRepository.delete(notificationTemplate);
    }

    private NotificationTemplateDTO toNotificationTemplateDTO(NotificationTemplate notificationTemplate) {
        NotificationTemplateDTO notificationTemplateDTO = new NotificationTemplateDTO();
        notificationTemplateDTO.setId(notificationTemplate.getId());
        notificationTemplateDTO.setCode(notificationTemplate.getCode());
        notificationTemplateDTO.setName(notificationTemplate.getName());
        notificationTemplateDTO.setContent(notificationTemplate.getContent());
        return notificationTemplateDTO;
    }
}
