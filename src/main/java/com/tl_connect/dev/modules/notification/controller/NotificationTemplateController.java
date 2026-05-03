package com.tl_connect.dev.modules.notification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.modules.notification.dto.CreateNotificationTemplateDTO;
import com.tl_connect.dev.modules.notification.dto.NotificationTemplateDTO;
import com.tl_connect.dev.modules.notification.dto.UpdateNotificationTemplateDTO;
import com.tl_connect.dev.modules.notification.service.NotificationTemplateService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/notification-templates")
public class NotificationTemplateController {

    @Autowired
    private NotificationTemplateService notificationTemplateService;

    @PostMapping("/create")
    public ResponseEntity<?> createNotificationTemplate(@Valid @RequestBody CreateNotificationTemplateDTO createNotificationTemplateDTO) {
        Long id = notificationTemplateService.createNotificationTemplate(createNotificationTemplateDTO);
        return ResponseHelper.success("Create notification template successfully", id);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateNotificationTemplate(@PathVariable Long id, @Valid @RequestBody UpdateNotificationTemplateDTO updateNotificationTemplateDTO) {
        notificationTemplateService.updateNotificationTemplate(id, updateNotificationTemplateDTO);
        return ResponseHelper.success("Update notification template successfully", null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteNotificationTemplate(@PathVariable Long id) {
        notificationTemplateService.deleteNotificationTemplate(id);
        return ResponseHelper.success("Delete notification template successfully", null);
    }


    @GetMapping("/all")
    public ResponseEntity<?> getAllNotificationTemplates(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        PagedResponse<NotificationTemplateDTO> notificationTemplates = notificationTemplateService.getAllNotificationTemplates(pageable);
        return ResponseHelper.success("Get all notification templates successfully", notificationTemplates);
    }
}
