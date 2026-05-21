package com.tl_connect.dev.modules.schedule.service.interfaces;

import java.time.LocalDate;
import java.util.List;

import com.tl_connect.dev.modules.schedule.dto.ClassScheduleAdminDTO;
import com.tl_connect.dev.modules.schedule.dto.ClassScheduleDTO;
import com.tl_connect.dev.modules.schedule.dto.DayOfWeekScheduleDTO;
import com.tl_connect.dev.modules.schedule.dto.SemesterScheduleDTO;
import com.tl_connect.dev.modules.schedule.dto.UpdateScheduleDTO;
import com.tl_connect.dev.modules.schedule.dto.WeeklyScheduleDTO;

public interface ScheduleService {
    WeeklyScheduleDTO getWeeklySchedule(Long studentId, LocalDate startDate, LocalDate endDate);

    SemesterScheduleDTO getSemesterSchedule(Long studentId, String semesterCode);

    DayOfWeekScheduleDTO getDayOfWeekSchedule(Long studentId, int dayOfWeek);

    List<ClassScheduleAdminDTO> getAllClassSchedules(Long courseClassId);

    void createClassSchedule(Long courseClassId, List<ClassScheduleDTO> newSchedules);

    void updateClassSchedule(Long id, UpdateScheduleDTO dto);

    void deleteClassSchedule(Long id);
}
