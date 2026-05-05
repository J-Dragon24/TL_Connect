package com.tl_connect.dev.modules.enroll.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.enroll.cache.StudentScheduleCache;
import com.tl_connect.dev.modules.enroll.dto.ScheduleForCheckDTO;
import com.tl_connect.dev.shared.common.exception.ConflictException;
import com.tl_connect.dev.shared.datastructure.intervaltree.ScheduleInterval;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleConflictService {

    private final StudentScheduleService studentScheduleService;

    public void check(Long studentId, Long semesterId, List<ScheduleForCheckDTO> newSchedules) {
        StudentScheduleCache studentSchedule = studentScheduleService.getStudentSchedule(studentId, semesterId);

        for (ScheduleForCheckDTO s : newSchedules) {
            ScheduleInterval overlap = studentSchedule.findConflict(
                s.dayOfWeek(),
                s.startPeriod(),
                s.endPeriod()
            );

            if (overlap != null) {
                throw new ConflictException(String.format(
                    "Tiết %d-%d thứ %d trùng với lớp %s",
                    s.startPeriod(), s.endPeriod(),
                    s.dayOfWeek() + 1, overlap.getClassCode()
                ));
            }
        }
    }
}
