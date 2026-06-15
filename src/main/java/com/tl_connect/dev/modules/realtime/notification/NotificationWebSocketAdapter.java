package com.tl_connect.dev.modules.realtime.notification;

import java.util.List;

import org.springframework.stereotype.Component;

import com.tl_connect.dev.modules.realtime.common.WebSocketGateway;
import com.tl_connect.dev.modules.realtime.notification.dto.NotificationRealtimeDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationWebSocketAdapter {

    private final WebSocketGateway gateway;

    public void send(
            NotificationRealtimeDTO dto,
            List<Long> targetIds
    ) {

        switch (dto.getTargetType()) {

            case GLOBAL -> {
                System.out.println("GLOBAL");
                gateway.send(NotificationDestination.GLOBAL, dto);
            }

            case STUDENT_CLASS -> {
                System.out.println("STUDENT_CLASS");
                for (Long classId : targetIds) {
                    gateway.send(
                            NotificationDestination.classTopic(classId),
                            dto
                    );
                }
            }

            case FACULTY -> {
                System.out.println("FACULTY");
                for (Long facultyId : targetIds) {
                    gateway.send(NotificationDestination.facultyTopic(facultyId), dto);
                }
            }

            case COURSE_CLASS -> {
                System.out.println("COURSE_CLASS");
                for (Long courseClassId : targetIds) {
                    gateway.send(
                            NotificationDestination.courseTopic(courseClassId),
                            dto
                    );
                }
            }

            case STUDENT -> {
                System.out.println("STUDENT");
                for (Long studentId : targetIds) {
                    gateway.sendToUser(
                            studentId,
                            NotificationDestination.PRIVATE,
                            dto
                    );
                }
            }
        }
    }
}
