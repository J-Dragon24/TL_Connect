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
    @Query("""
            SELECT
                n.id,
                n.title,
                n.sender,
                n.isRead,
                n.targetType,
                n.createdAt
            FROM Notification n
            LEFT JOIN StudentClass sc ON n.targetId = sc.id
            LEFT JOIN Student s ON s.id = :studentId
            LEFT JOIN StudentCourseClass scc ON n.targetId = scc.courseClassId
            WHERE 
                n.targetType = 'ALL'
                OR (n.targetType = 'STUDENT' AND n.targetId = :studentId)
                OR (n.targetType = 'STUDENT_CLASS' AND sc.id = s.studentClassId)
                OR (n.targetType = 'COURSE_CLASS' AND scc.studentId = :studentId)
            ORDER BY n.createdAt DESC
            """)
    Optional<List<NotificationRow>> findAllNotification(@Param("studentId") Long studentId);

    Optional<Notification> findById(Long id);
}

