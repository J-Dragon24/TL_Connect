package com.tl_connect.dev.modules.enroll.event;

import com.tl_connect.dev.shared.common.enums.EnrollAction;
import com.tl_connect.dev.shared.common.enums.StudentCourseClassStatus;

import lombok.Getter;

/**
 * Event được publish sau khi drop transaction commit thành công.
 * Listeners sẽ xử lý: ghi log, remove cache.
 */
@Getter
public class EnrollmentDroppedEvent {

    private final Long studentId;
    private final Long courseClassId;
    private final Long semesterId;
    private final StudentCourseClassStatus oldStatus;
    private final StudentCourseClassStatus newStatus;

    public EnrollmentDroppedEvent(
            Long studentId,
            Long courseClassId,
            Long semesterId,
            StudentCourseClassStatus oldStatus,
            StudentCourseClassStatus newStatus) {
        this.studentId = studentId;
        this.courseClassId = courseClassId;
        this.semesterId = semesterId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public EnrollAction getAction() {
        return EnrollAction.DROP;
    }
}
