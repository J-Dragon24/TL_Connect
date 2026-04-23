package com.tl_connect.dev.modules.notification.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.exception.UnauthorizeException;
import com.tl_connect.dev.core.common.types.JwtUserInfo;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.notification.dto.DetailNotifyDTO;
import com.tl_connect.dev.modules.notification.dto.MarkNotificationsReadDTO;
import com.tl_connect.dev.modules.notification.dto.NotificationReqDTO;
import com.tl_connect.dev.modules.notification.dto.PrepareNotificationDTO;
import com.tl_connect.dev.modules.notification.dto.SummaryNotifyDTO;
import com.tl_connect.dev.modules.notification.dto.UnreadNotificationDTO;
import com.tl_connect.dev.modules.notification.service.NotificationService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/prepare")
    public ResponseEntity<?> getPrepareNotification(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication is required");
        }
        Long studentId = userInfo.userId();
        PrepareNotificationDTO prepareNotificationDTO = notificationService.prepareNotification(studentId);
        return ResponseHelper.success("Get prepare notification successfully", prepareNotificationDTO);
    }

    @PostMapping
    public ResponseEntity<?> getAllNotification(Authentication authentication, @RequestBody NotificationReqDTO notificationReqDTO, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication is required");
        }
        Long studentId = userInfo.userId();
        PagedResponse<SummaryNotifyDTO> notifications = notificationService.getAllNotification(studentId, notificationReqDTO, pageable);
        return ResponseHelper.success("Get all notification successfully", notifications);
    }

    @PostMapping("/unread-count")
    public ResponseEntity<?> countUnreadNotification(Authentication authentication, @RequestBody NotificationReqDTO notificationReqDTO) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication is required");
        }
        Long studentId = userInfo.userId();
        UnreadNotificationDTO count = notificationService.countUnreadNotification(studentId, notificationReqDTO);
        return ResponseHelper.success("Count unread notification successfully", count);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getDetailNotification(Authentication authentication, @PathVariable("id") Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication is required");
        }
        DetailNotifyDTO notification = notificationService.getDetailNotification(id);
        return ResponseHelper.success("Get detail notification successfully", notification);
    }

    @PostMapping("/read")
    public ResponseEntity<?> markNotificationAsRead(Authentication authentication, @RequestBody MarkNotificationsReadDTO markNotificationsReadDTO) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication is required");
        }
        Long studentId = userInfo.userId();
        notificationService.markNotificationAsRead(studentId, markNotificationsReadDTO.getNotificationIds());
        return ResponseHelper.success("Mark notification as read successfully", null);
    }
}
