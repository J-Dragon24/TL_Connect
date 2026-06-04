package com.tl_connect.dev.shared.common.ultility;

import org.springframework.stereotype.Component;

import com.tl_connect.dev.shared.common.enums.NotificationType;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;

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
            default -> throw new InvalidInputException("Invalid topic type");
        };
    }
}