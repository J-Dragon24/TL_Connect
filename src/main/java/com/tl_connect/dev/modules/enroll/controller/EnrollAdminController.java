package com.tl_connect.dev.modules.enroll.controller;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.modules.enroll.dto.CreateEnrollPeriodDTO;
import com.tl_connect.dev.modules.enroll.dto.StudentCourseClassDTO;
import com.tl_connect.dev.modules.enroll.dto.StudentCourseClassFilter;
import com.tl_connect.dev.modules.enroll.dto.UpdateEnrollPeriodDTO;
import com.tl_connect.dev.modules.enroll.entity.EnrollmentPeriod;
import com.tl_connect.dev.modules.enroll.service.EnrollManagementService;
import com.tl_connect.dev.modules.enroll.service.EnrollmentPeriodService;
import com.tl_connect.dev.modules.enroll.service.PrerequisiteDAGService;
import com.tl_connect.dev.modules.enroll.service.StudentScheduleService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/enrollment")
@RequiredArgsConstructor
public class EnrollAdminController {

    private final EnrollmentPeriodService enrollmentPeriodService;
    private final EnrollManagementService enrollManagementService;
    private final StudentScheduleService studentScheduleService;
    private final PrerequisiteDAGService prerequisiteDAGService;

    @PostMapping("/periods/create")
    public ResponseEntity<?> createPeriod(@Valid @RequestBody CreateEnrollPeriodDTO dto) {
        return ResponseHelper.success("Period created successfully", enrollmentPeriodService.createPeriod(dto));
    }

    @GetMapping("/periods")
    public ResponseEntity<?> getAllPeriods(@RequestParam(name = "HocKy", required = false) String semesterCode,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        PagedResponse<EnrollmentPeriod> page = enrollmentPeriodService.getAllPeriods(pageable, semesterCode);
        return ResponseHelper.success("Periods retrieved successfully", page);
    }

    @PostMapping("/periods/update/{id}")
    public ResponseEntity<?> updatePeriod(@PathVariable Long id, @Valid @RequestBody UpdateEnrollPeriodDTO dto) {
        return ResponseHelper.success("Period updated successfully", enrollmentPeriodService.updatePeriod(id, dto));
    }

    @PostMapping("/periods/delete/{id}")
    public ResponseEntity<?> deletePeriod(@PathVariable Long id) {
        enrollmentPeriodService.deletePeriod(id);
        return ResponseHelper.success("Period deleted successfully", null);
    }

    @PostMapping("/periods/clear-cache/{semesterId}")
    public ResponseEntity<?> invalidatePeriod(@PathVariable Long semesterId) {
        enrollmentPeriodService.invalidate(semesterId);
        return ResponseHelper.success("Period cache invalidated successfully", null);
    }

    @PostMapping("/schedule/clear-cache/{semesterId}")
    public ResponseEntity<?> invalidateSchedule(@PathVariable Long semesterId) {
        studentScheduleService.invalidateAll(semesterId);
        return ResponseHelper.success("Schedule cache invalidated successfully", null);
    }

    @PostMapping("/prerequisite/clear-cache")
    public ResponseEntity<?> invalidatePrerequisite() {
        prerequisiteDAGService.invalidateDAG();
        return ResponseHelper.success("Prerequisite cache invalidated successfully", null);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllStudentEnrollment(
            @ModelAttribute StudentCourseClassFilter filter,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {

        PagedResponse<StudentCourseClassDTO> page = enrollManagementService.getAllStudentEnrollment(filter, pageable);
        return ResponseHelper.success("Student enrollments retrieved successfully", page);
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestBody Map<String, Long> body) {
        Long semesterId = body.get("semesterId");
        if (semesterId == null) {
            throw new InvalidInputException("Semester ID is required");
        }
        enrollManagementService.confirm(semesterId);
        return ResponseHelper.success("Confirmed successfully", null);
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        enrollManagementService.cancel(id);
        return ResponseHelper.success("Cancelled successfully", null);
    }


}
