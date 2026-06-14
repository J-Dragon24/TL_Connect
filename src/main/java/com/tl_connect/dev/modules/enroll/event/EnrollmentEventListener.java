package com.tl_connect.dev.modules.enroll.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.tl_connect.dev.modules.enroll.entity.StudentCourseClassLog;
import com.tl_connect.dev.modules.enroll.repository.CourseClassLogRepository;
import com.tl_connect.dev.modules.enroll.service.StudentScheduleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@RequiredArgsConstructor
public class EnrollmentEventListener {

    private final CourseClassLogRepository courseClassLogRepository;
    private final StudentScheduleService studentScheduleService;

    // -------------------------------------------------------------------------
    // Enroll side effects
    // -------------------------------------------------------------------------


    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEnrollmentCreated_writeLog(EnrollmentCreatedEvent event) {
        try {
            StudentCourseClassLog logEntry = StudentCourseClassLog.create(
                    event.getStudentId(),
                    event.getCourseClassId(),
                    event.getAction(),
                    event.getOldStatus(),
                    event.getNewStatus());
            courseClassLogRepository.save(logEntry);
        } catch (Exception ex) {
            log.error("[EnrollmentLog] Failed to write enroll log for studentId={}, courseClassId={}",
                    event.getStudentId(), event.getCourseClassId(), ex);
        }
    }


    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEnrollmentCreated_updateCache(EnrollmentCreatedEvent event) {
        try {
            studentScheduleService.addToCache(
                    event.getStudentId(),
                    event.getSemesterId(),
                    event.getScheduleIntervals());
        } catch (Exception ex) {
            log.error("[EnrollmentCache] Failed to update schedule cache for studentId={}, courseClassId={}",
                    event.getStudentId(), event.getCourseClassId(), ex);
        }
    }

    // -------------------------------------------------------------------------
    // Drop side effects
    // -------------------------------------------------------------------------

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEnrollmentDropped_writeLog(EnrollmentDroppedEvent event) {
        try {
            StudentCourseClassLog logEntry = StudentCourseClassLog.create(
                    event.getStudentId(),
                    event.getCourseClassId(),
                    event.getAction(),
                    event.getOldStatus(),
                    event.getNewStatus());
            courseClassLogRepository.save(logEntry);
        } catch (Exception ex) {
            log.error("[EnrollmentLog] Failed to write drop log for studentId={}, courseClassId={}",
                    event.getStudentId(), event.getCourseClassId(), ex);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEnrollmentDropped_removeCache(EnrollmentDroppedEvent event) {
        try {
            studentScheduleService.removeFromCache(
                    event.getStudentId(),
                    event.getSemesterId(),
                    event.getCourseClassId());
        } catch (Exception ex) {
            log.error("[EnrollmentCache] Failed to remove schedule cache for studentId={}, courseClassId={}",
                    event.getStudentId(), event.getCourseClassId(), ex);
        }
    }
}
