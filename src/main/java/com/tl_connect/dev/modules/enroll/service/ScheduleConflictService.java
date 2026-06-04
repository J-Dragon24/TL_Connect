package com.tl_connect.dev.modules.enroll.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.enroll.cache.StudentScheduleCache;
import com.tl_connect.dev.modules.enroll.dto.ScheduleForCheckDTO;
import com.tl_connect.dev.shared.common.dto.ScheduleConflict;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ErrorException;
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
                ScheduleConflict conflict = ScheduleConflict.builder()
                    .dayOfWeek(s.dayOfWeek())
                    .startPeriod(s.startPeriod())
                    .endPeriod(s.endPeriod())
                    .classOverlapCode(overlap.getClassCode())
                    .build();

                throw new ErrorException(
                        ResponseStatus.SCHEDULE_CONFLICT,
                        "Conflict schedule",
                        conflict);
            }
        }
    }
}
