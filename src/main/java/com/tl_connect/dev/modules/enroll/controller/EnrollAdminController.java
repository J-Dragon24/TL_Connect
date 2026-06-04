package com.tl_connect.dev.modules.enroll.controller;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.modules.enroll.dto.CreateEnrollPeriodDTO;
import com.tl_connect.dev.modules.enroll.dto.StudentCourseClassDTO;
import com.tl_connect.dev.modules.enroll.dto.StudentCourseClassFilter;
import com.tl_connect.dev.modules.enroll.dto.UpdateEnrollPeriodDTO;
import com.tl_connect.dev.modules.enroll.entity.EnrollmentPeriod;
import com.tl_connect.dev.modules.enroll.service.PrerequisiteDAGService;
import com.tl_connect.dev.modules.enroll.service.StudentScheduleService;
import com.tl_connect.dev.modules.enroll.service.interfaces.EnrollManagementService;
import com.tl_connect.dev.modules.enroll.service.interfaces.EnrollmentPeriodService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.common.types.JwtUserInfo;
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
    public ResponseEntity<?> createPeriod(Authentication authentication, @Valid @RequestBody CreateEnrollPeriodDTO dto) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        return ResponseHelper.success("Period created successfully", enrollmentPeriodService.createPeriod(dto));
    }

    @GetMapping("/periods")
    public ResponseEntity<?> getAllPeriods(Authentication authentication,
        @RequestParam(name = "HocKy", required = false) String semesterCode,
        @PageableDefault(size = 10, page = 0) Pageable pageable) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        PagedResponse<EnrollmentPeriod> page = enrollmentPeriodService.getAllPeriods(pageable, semesterCode);
        return ResponseHelper.success("Periods retrieved successfully", page);
    }

    @PostMapping("/periods/update/{id}")
    public ResponseEntity<?> updatePeriod(Authentication authentication, @PathVariable Long id, @Valid @RequestBody UpdateEnrollPeriodDTO dto) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        return ResponseHelper.success("Period updated successfully", enrollmentPeriodService.updatePeriod(id, dto));
    }

    @PostMapping("/periods/delete/{id}")
    public ResponseEntity<?> deletePeriod(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        enrollmentPeriodService.deletePeriod(id);
        return ResponseHelper.success("Period deleted successfully", null);
    }

    @PostMapping("/periods/clear-cache/{semesterId}")
    public ResponseEntity<?> invalidatePeriod(Authentication authentication, @PathVariable Long semesterId) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        enrollmentPeriodService.invalidate(semesterId);
        return ResponseHelper.success("Period cache invalidated successfully", null);
    }

    @PostMapping("/schedule/clear-cache/{semesterId}")
    public ResponseEntity<?> invalidateSchedule(Authentication authentication, @PathVariable Long semesterId) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        studentScheduleService.invalidateAll(semesterId);
        return ResponseHelper.success("Schedule cache invalidated successfully", null);
    }

    @PostMapping("/prerequisite/clear-cache")
    public ResponseEntity<?> invalidatePrerequisite(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        prerequisiteDAGService.invalidateDAG();
        return ResponseHelper.success("Prerequisite cache invalidated successfully", null);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllStudentEnrollment(
            Authentication authentication,
            @ModelAttribute StudentCourseClassFilter filter,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        PagedResponse<StudentCourseClassDTO> page = enrollManagementService.getAllStudentEnrollment(filter, pageable);
        return ResponseHelper.success("Student enrollments retrieved successfully", page);
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(Authentication authentication, @RequestBody Map<String, Long> body) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long semesterId = body.get("semesterId");
        if (semesterId == null) {
            throw new InvalidInputException("Semester ID is required");
        }
        enrollManagementService.confirm(semesterId);
        return ResponseHelper.success("Confirmed successfully", null);
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancel(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        enrollManagementService.cancel(id);
        return ResponseHelper.success("Cancelled successfully", null);
    }


}
