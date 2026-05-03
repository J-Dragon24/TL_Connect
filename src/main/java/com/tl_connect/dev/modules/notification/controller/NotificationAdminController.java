package com.tl_connect.dev.modules.notification.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.modules.notification.dto.CreateNotificationReqDTO;
import com.tl_connect.dev.modules.notification.dto.NotificationAdmDTO;
import com.tl_connect.dev.modules.notification.dto.UpdateNotificationDTO;
import com.tl_connect.dev.modules.notification.service.NotificationModifyService;
import com.tl_connect.dev.modules.notification.service.NotificationService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/notification")
@RequiredArgsConstructor
public class NotificationAdminController {
    private final NotificationService notificationService;
    private final NotificationModifyService notificationModifyService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllNotification(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        PagedResponse<NotificationAdmDTO> notifications = notificationService.getAllNotification(pageable);
        return ResponseHelper.success("Get all notification successfully", notifications);
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody @Valid CreateNotificationReqDTO notificationReqDTO) {
        notificationModifyService.sendNotification(notificationReqDTO);
        return ResponseHelper.success("Create notification successfully", null);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable Long id, @Valid @RequestBody UpdateNotificationDTO notificationReqDTO) {
        notificationModifyService.updateNotification(id, notificationReqDTO);
        return ResponseHelper.success("Update notification successfully", null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        notificationModifyService.deleteNotification(id);
        return ResponseHelper.success("Delete notification successfully", null);
    }
}
