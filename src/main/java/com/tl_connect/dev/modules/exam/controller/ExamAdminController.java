package com.tl_connect.dev.modules.exam.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.modules.exam.dto.CreateExamScheduleDTO;
import com.tl_connect.dev.modules.exam.dto.ExamScheduleBasicInfoDTO;
import com.tl_connect.dev.modules.exam.dto.UpdateExamScheduleDTO;
import com.tl_connect.dev.modules.exam.service.ExamServiceImpl;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.types.JwtUserInfo;
import com.tl_connect.dev.shared.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/exam")
@RequiredArgsConstructor
public class ExamAdminController {
    private final ExamServiceImpl examService;

    @GetMapping
    public ResponseEntity<?> getExamSchedule(Authentication authentication,
        @RequestParam(name = "semesterId", required = true) Long semesterId, 
        @RequestParam(name = "facultyId", required = false) Long facultyId,
    @PageableDefault(page = 0, size = 10) Pageable pageable) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }

        PagedResponse<ExamScheduleBasicInfoDTO> result = examService.getExamSchedule(semesterId, pageable, facultyId);

        return ResponseHelper.success("Get exam schedule successfully", result);
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createExamSchedule(Authentication authentication, @RequestBody CreateExamScheduleDTO createExamScheduleDTO) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long id = examService.createExamSchedule(createExamScheduleDTO);
        return ResponseHelper.success("Create exam schedule successfully", id);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateExamSchedule(Authentication authentication, @PathVariable(name = "id") Long id, @RequestBody UpdateExamScheduleDTO updateExamScheduleDTO) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        examService.updateExamSchedule(id, updateExamScheduleDTO);
        return ResponseHelper.success("Update exam schedule successfully", null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteExamSchedule(Authentication authentication, @PathVariable(name = "id") Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        examService.deleteExamSchedule(id);
        return ResponseHelper.success("Delete exam schedule successfully", null);
    }
}
