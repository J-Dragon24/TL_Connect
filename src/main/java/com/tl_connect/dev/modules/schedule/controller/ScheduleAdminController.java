package com.tl_connect.dev.modules.schedule.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.modules.schedule.dto.ClassScheduleAdminDTO;
import com.tl_connect.dev.modules.schedule.dto.ClassScheduleDTO;
import com.tl_connect.dev.modules.schedule.dto.UpdateScheduleDTO;
import com.tl_connect.dev.modules.schedule.service.interfaces.ScheduleService;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/schedules")
@RequiredArgsConstructor
public class ScheduleAdminController {
    private final ScheduleService scheduleService;

    @GetMapping("/{courseClassId}")
    public ResponseEntity<?> getAll(@PathVariable Long courseClassId) {
        List<ClassScheduleAdminDTO> list = scheduleService.getAllClassSchedules(courseClassId);
        return ResponseHelper.success("Get all schedule successfully",list);
    }

    @PostMapping("/create/{courseClassId}")
    public ResponseEntity<?> create(@PathVariable Long courseClassId, @Valid @RequestBody List<ClassScheduleDTO> dto) {
        scheduleService.createClassSchedule(courseClassId, dto);
        return ResponseHelper.success("Create schedule successfully",null);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateScheduleDTO dto) {
        scheduleService.updateClassSchedule(id, dto);
        return ResponseHelper.success("Update schedule successfully",null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        scheduleService.deleteClassSchedule(id);
        return ResponseHelper.success("Delete schedule successfully",null);
    }
}
