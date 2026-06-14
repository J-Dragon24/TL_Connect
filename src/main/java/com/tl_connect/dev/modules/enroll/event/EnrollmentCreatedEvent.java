package com.tl_connect.dev.modules.enroll.event;

import java.util.List;

import com.tl_connect.dev.shared.common.enums.EnrollAction;
import com.tl_connect.dev.shared.common.enums.StudentCourseClassStatus;
import com.tl_connect.dev.shared.datastructure.intervaltree.ScheduleInterval;

import lombok.Getter;

/**
 * Event được publish sau khi enroll transaction commit thành công.
 * Listeners sẽ xử lý: ghi log, update cache, gửi notification.
 */
@Getter
public class EnrollmentCreatedEvent {

    private final Long studentId;
    private final Long courseClassId;
    private final Long semesterId;
    private final StudentCourseClassStatus oldStatus;
    private final StudentCourseClassStatus newStatus;
    private final List<ScheduleInterval> scheduleIntervals;

    public EnrollmentCreatedEvent(
            Long studentId,
            Long courseClassId,
            Long semesterId,
            StudentCourseClassStatus oldStatus,
            StudentCourseClassStatus newStatus,
            List<ScheduleInterval> scheduleIntervals) {
        this.studentId = studentId;
        this.courseClassId = courseClassId;
        this.semesterId = semesterId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.scheduleIntervals = scheduleIntervals;
    }

    public EnrollAction getAction() {
        return EnrollAction.ENROLL;
    }
}
