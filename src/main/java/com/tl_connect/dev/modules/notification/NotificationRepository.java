package com.tl_connect.dev.modules.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.notification.projection.NotificationRow;
import com.tl_connect.dev.modules.notification.projection.PrepareNotificationView;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(value = """
            SELECT DISTINCT
            s.student_class_id AS studentClassId,
            s.oauth_user_id AS oauthUserId,
            m.faculty_id AS facultyId
            FROM students s
            JOIN student_majors sm ON s.id = sm.student_id
            JOIN majors m ON sm.major_id = m.id
            WHERE s.id = :studentId
            """, nativeQuery = true)
    Optional<PrepareNotificationView> getStudentInfo(@Param("studentId") Long studentId);

    @Query(value = """
            SELECT 
                n.id AS id,
                n.title AS title,
                n.content AS content,
                n.created_by AS createdBy,
                n.target_type AS targetType,
                n.created_at AS createdAt,
                n.deadline AS deadLine,
                CASE 
                    WHEN nr.notification_id IS NULL THEN false
                    ELSE true
                END AS isRead
            FROM notifications n
            LEFT JOIN notification_read nr ON n.id = nr.notification_id AND nr.oauth_user_id = :oauthUserId
            WHERE

                n.target_type = 'ALL'

            OR (n.target_type = 'STUDENT'
                AND n.target_id = :studentId)

            OR (n.target_type = 'STUDENT_CLASS'
                AND n.target_id = :classId)

            OR (n.target_type = 'FACULTY'
                AND n.target_id = :facultyId)

            OR (n.target_type = 'COURSE_CLASS'
                AND EXISTS (
                        SELECT 1
                        FROM student_course_classes scc
                        WHERE scc.student_id = :studentId
                        AND scc.course_class_id = n.target_id
                ))

            ORDER BY n.created_at DESC
            LIMIT 20
            """, nativeQuery = true)
    List<NotificationRow> findAllNotification(@Param("studentId") Long studentId, @Param("oauthUserId") Long oauthUserId, @Param("classId") Long classId, @Param("facultyId") Long facultyId);

    Optional<Notification> findById(Long id);

    @Query(value = """
            SELECT COUNT(*)
            FROM notifications n
            LEFT JOIN notification_read nr
                ON nr.notification_id = n.id
                AND nr.oauth_user_id = :oauthUserId
            WHERE nr.notification_id IS NULL

            AND (
                n.target_type = 'ALL'
            OR (n.target_type = 'STUDENT' AND n.target_id = :studentId)
            OR (n.target_type = 'STUDENT_CLASS' AND n.target_id = :classId)
            OR (n.target_type = 'FACULTY' AND n.target_id = :facultyId)
            OR (n.target_type = 'COURSE_CLASS'
                    AND EXISTS (
                        SELECT 1
                        FROM student_course_classes scc
                        WHERE scc.student_id = :studentId
                        AND scc.course_class_id = n.target_id
                    ))
            )
            """, nativeQuery = true)
    Long countUnreadNotification(@Param("studentId") Long studentId, @Param("oauthUserId") Long oauthUserId, @Param("classId") Long classId, @Param("facultyId") Long facultyId);
}

