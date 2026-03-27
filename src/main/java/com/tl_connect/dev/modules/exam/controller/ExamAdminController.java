package com.tl_connect.dev.modules.exam.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.exam.ExamService;
import com.tl_connect.dev.modules.exam.dto.ExamScheduleBasicInfoDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/exam")
@RequiredArgsConstructor
public class ExamAdminController {
    private final ExamService examService;

    @GetMapping
    public ResponseEntity<?> getExamSchedule(@RequestParam(name = "semesterId", required = true) Long semesterId, @RequestParam(name = "facultyId") Long facultyId,
    @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        PagedResponse<ExamScheduleBasicInfoDTO> result = examService.getExamSchedule(semesterId, pageable, facultyId);

        return ResponseHelper.success("Get exam schedule successfully", result);
    }
}
