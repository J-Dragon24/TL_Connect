package com.tl_connect.dev.notification;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.common.dto.ResponseHelper;
import com.tl_connect.dev.common.exception.InvalidInputException;
import com.tl_connect.dev.common.types.JwtUserInfo;
import com.tl_connect.dev.notification.dto.DetailNotifyDTO;
import com.tl_connect.dev.notification.dto.SummaryNotifyDTO;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getAllNotification(JwtUserInfo userInfo) {
        try {
            Long studentId = userInfo.userId();
            if(studentId == null) {
                throw new InvalidInputException("Student ID is required");
            }
            List<SummaryNotifyDTO> notifications = notificationService.getAllNotification(studentId);
            return ResponseHelper.success("Get all notification successfully", notifications);
        } catch (Exception e) {
            return ResponseHelper.internalError(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetailNotification(JwtUserInfo userInfo, @PathVariable Long id) {
        try {
            Long studentId = userInfo.userId();
            if(studentId == null) {
                throw new InvalidInputException("Student ID is required");
            }
            DetailNotifyDTO notification = notificationService.getDetailNotification(id);
            return ResponseHelper.success("Get detail notification successfully", notification);
        } catch (Exception e) {
            return ResponseHelper.internalError(e.getMessage());
        }
    }
}
