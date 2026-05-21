package com.tl_connect.dev.modules.enroll.service.interfaces;

import java.util.List;

import com.tl_connect.dev.shared.datastructure.intervaltree.ScheduleInterval;

public interface StudentCourseClassService {
    List<ScheduleInterval> findCurrentSchedule(Long studentId, Long semesterId);
}
