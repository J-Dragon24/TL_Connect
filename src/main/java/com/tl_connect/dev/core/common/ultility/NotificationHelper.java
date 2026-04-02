package com.tl_connect.dev.core.common.ultility;

import org.springframework.stereotype.Component;

import com.tl_connect.dev.core.common.enums.NotificationType;
import com.tl_connect.dev.core.common.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationHelper {
    public String buildTopic(NotificationType type, Long targetId) {
        return switch (type) {
            case GLOBAL -> "GLOBAL";
            case FACULTY -> "FACULTY_" + targetId;
            case STUDENT_CLASS -> "CLASS_" + targetId;
            case COURSE_CLASS -> "COURSE_" + targetId;
            default -> throw new BadRequestException("Invalid topic type");
        };
    }
}