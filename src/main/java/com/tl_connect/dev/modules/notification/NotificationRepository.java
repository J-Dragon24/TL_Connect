package com.tl_connect.dev.modules.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.notification.projection.NotificationRow;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query(value = """
            SELECT n.id, n.title, n.sender, n.target_type, n.created_at, n.dead_line
            FROM notifications n
            WHERE n.target_type = 'ALL'

            UNION ALL

            SELECT n.id, n.title, n.sender, n.target_type, n.created_at, n.dead_line
            FROM notifications n
            WHERE n.target_type = 'STUDENT' AND n.target_id = :studentId

            UNION ALL

            SELECT n.id, n.title, n.sender, n.target_type, n.created_at, n.dead_line
            FROM notifications n
            WHERE n.target_type = 'STUDENT_CLASS' AND n.target_id = (
                SELECT student_class_id FROM students WHERE id = :studentId
            )

            UNION ALL

            SELECT n.id, n.title, n.sender, n.target_type, n.created_at, n.dead_line
            FROM notifications n
            JOIN student_course_classes scc 
                ON n.target_id = scc.course_class_id 
                AND scc.student_id = :studentId
            WHERE n.target_type = 'COURSE_CLASS'

            ORDER BY created_at DESC
            """, nativeQuery = true)
    List<NotificationRow> findAllNotification(@Param("studentId") Long studentId);

    Optional<Notification> findById(Long id);
}

