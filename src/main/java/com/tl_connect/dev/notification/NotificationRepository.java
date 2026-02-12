package com.tl_connect.dev.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.notification.projection.NotificationRow;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query(value = """
            SELECT
                n.id AS id,
                n.title AS title,
                n.sender AS sender,
                n.is_read AS isRead,
                n.target_type AS targetType,
                n.created_at AS createdAt,
                n.deadline AS deadline
            FROM notifications n
            LEFT JOIN student_classes sc 
                ON n.target_id = sc.id
            LEFT JOIN student_course_classes scc 
                ON n.target_id = scc.course_class_id
            WHERE 
                n.target_type = 'ALL'
                OR (n.target_type = 'STUDENT' AND n.target_id = :studentId)
                OR (n.target_type = 'STUDENT_CLASS' AND n.target_id = (
                    SELECT student_class_id 
                    FROM students 
                    WHERE id = :studentId
                ))
                OR (n.target_type = 'COURSE_CLASS' AND scc.student_id = :studentId)
            ORDER BY n.created_at DESC
            """, nativeQuery = true)
    List<NotificationRow> findAllNotification(@Param("studentId") Long studentId);

    Optional<Notification> findById(Long id);
}

