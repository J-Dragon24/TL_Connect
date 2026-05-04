package com.tl_connect.dev.modules.enroll.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.modules.enroll.dto.CreateEnrollPeriodDTO;
import com.tl_connect.dev.modules.enroll.dto.UpdateEnrollPeriodDTO;
import com.tl_connect.dev.modules.enroll.entity.EnrollmentPeriod;
import com.tl_connect.dev.modules.enroll.service.EnrollmentPeriodService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/enrollment")
@RequiredArgsConstructor
public class EnrollAdminController {

    private final EnrollmentPeriodService enrollmentPeriodService;

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
    public ResponseEntity<?> invalidate(@PathVariable Long semesterId) {
        enrollmentPeriodService.invalidate(semesterId);
        return ResponseHelper.success("Period cache invalidated successfully", null);
    }
}
