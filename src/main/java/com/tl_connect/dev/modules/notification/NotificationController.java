package com.tl_connect.dev.modules.notification;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.core.common.exception.UnauthorizeException;
import com.tl_connect.dev.core.common.types.JwtUserInfo;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.notification.dto.DetailNotifyDTO;
import com.tl_connect.dev.modules.notification.dto.SummaryNotifyDTO;

import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getAllNotification(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication is required");
        }
        Long studentId = userInfo.userId();
        List<SummaryNotifyDTO> notifications = notificationService.getAllNotification(studentId);
        return ResponseHelper.success("Get all notification successfully", notifications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetailNotification(Authentication authentication, @PathVariable("id") Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication is required");
        }
        DetailNotifyDTO notification = notificationService.getDetailNotification(id);
        return ResponseHelper.success("Get detail notification successfully", notification);
    }
}
