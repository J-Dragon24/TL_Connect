package com.tl_connect.dev.modules.schedule.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.schedule.ScheduleService;
import com.tl_connect.dev.modules.schedule.dto.ClassScheduleDTO;
import com.tl_connect.dev.modules.schedule.dto.UpdateScheduleDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/schedules")
@RequiredArgsConstructor
public class ScheduleAdminController {
    private final ScheduleService scheduleService;

    @GetMapping("/{courseClassId}")
    public ResponseEntity<?> getDetail(@PathVariable Long courseClassId) {
        List<ClassScheduleDTO> list = scheduleService.getAllClassSchedules(courseClassId);
        return ResponseHelper.success("Get detail schedule successfully",list);
    }

    @PostMapping("/create/{courseClassId}")
    public ResponseEntity<?> create(@PathVariable Long courseClassId, @Valid @RequestBody List<ClassScheduleDTO> dto) {
        scheduleService.createClassSchedule(courseClassId, dto);
        return ResponseHelper.success("Create schedule successfully",null);
    }

    @PutMapping("/update/{courseClassId}")
    public ResponseEntity<?> update(@PathVariable Long courseClassId, @Valid @RequestBody UpdateScheduleDTO dto) {
        scheduleService.updateClassSchedule(courseClassId, dto);
        return ResponseHelper.success("Update schedule successfully",null);
    }

    @DeleteMapping("/delete/{courseClassId}")
    public ResponseEntity<?> delete(@PathVariable Long courseClassId) {
        scheduleService.deleteClassSchedule(courseClassId);
        return ResponseHelper.success("Delete schedule successfully",null);
    }
}
