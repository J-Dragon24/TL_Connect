package com.tl_connect.dev.modules.enroll.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.modules.enroll.dto.CourseClassForEnrollDTO;
import com.tl_connect.dev.modules.enroll.dto.CourseClassRequest;
import com.tl_connect.dev.modules.enroll.dto.DropRequestDTO;
import com.tl_connect.dev.modules.enroll.dto.EnrollRequestDTO;
import com.tl_connect.dev.modules.enroll.dto.EnrollViewDTO;
import com.tl_connect.dev.modules.enroll.dto.EnrollmentHeaderDTO;
import com.tl_connect.dev.modules.enroll.service.interfaces.EnrollService;
import com.tl_connect.dev.modules.schedule.dto.ScheduleCourseClassDTO;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.types.JwtUserInfo;
import com.tl_connect.dev.shared.ultility.ResponseHelper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/student/enrollment")
@RequiredArgsConstructor
public class EnrollController {
    private final EnrollService enrollService;

    @GetMapping("/period")
    public ResponseEntity<?> getEnrollmentPeriods(Authentication authentication, @RequestParam String studyProgramCode) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        EnrollmentHeaderDTO enrollmentHeaderDTO = enrollService.getEnrollmentPeriods(studentId, studyProgramCode);
        return ResponseHelper.success("Available course classes retrieved successfully", enrollmentHeaderDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAvailableSubjects(Authentication authentication, @RequestParam String studyProgramCode) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        EnrollViewDTO subjects = enrollService.getAvailableSubjects(studentId, studyProgramCode);
        return ResponseHelper.success("Available course classes retrieved successfully", subjects);
    }

    @PostMapping("/course-classes")
    public ResponseEntity<?> getAvailableCourseClasses(Authentication authentication, @RequestBody @Valid CourseClassRequest request) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        List<CourseClassForEnrollDTO> courseClasses = enrollService.getAvailableCourseClasses(request.getSubjectId(), request.getSemesterId());
        return ResponseHelper.success("Available course classes retrieved successfully", courseClasses);
    }


    @PostMapping("/schedule")
    public ResponseEntity<?> getTempSchedule(Authentication authentication, @RequestBody Map<String, Long> request) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        Long semesterId = request.get("semesterId");
        if (semesterId == null) {
            throw new InvalidInputException("Semester ID is required");
        }
        List<ScheduleCourseClassDTO> courseClasses = enrollService.getTempSchedule(studentId, semesterId);
        return ResponseHelper.success("Available course classes retrieved successfully", courseClasses);
    }

    @PostMapping("/enroll")
    public ResponseEntity<?> enrollInCourseClass(Authentication authentication,
            @RequestBody @Valid EnrollRequestDTO request) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        enrollService.enroll(studentId, request.getCourseClassId(), request.getStudyProgramId());
        return ResponseHelper.success("Enrollment successful", null);
    }

    @PostMapping("/drop")
    public ResponseEntity<?> dropCourseClass(Authentication authentication,
            @RequestBody @Valid DropRequestDTO request) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        enrollService.drop(studentId, request.getCourseClassId());
        return ResponseHelper.success("Drop successful", null);
    }
}
