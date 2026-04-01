package com.tl_connect.dev.modules.exam.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.exam.ExamService;
import com.tl_connect.dev.modules.exam.dto.CreateExamScheduleDTO;
import com.tl_connect.dev.modules.exam.dto.ExamScheduleBasicInfoDTO;
import com.tl_connect.dev.modules.exam.dto.UpdateExamScheduleDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/exam")
@RequiredArgsConstructor
public class ExamAdminController {
    private final ExamService examService;

    @GetMapping
    public ResponseEntity<?> getExamSchedule(@RequestParam(name = "semesterId", required = true) Long semesterId, @RequestParam(name = "facultyId", required = false) Long facultyId,
    @PageableDefault(page = 0, size = 10) Pageable pageable) {

        PagedResponse<ExamScheduleBasicInfoDTO> result = examService.getExamSchedule(semesterId, pageable, facultyId);

        return ResponseHelper.success("Get exam schedule successfully", result);
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createExamSchedule(@RequestBody CreateExamScheduleDTO createExamScheduleDTO) {
        Long id = examService.createExamSchedule(createExamScheduleDTO);
        return ResponseHelper.success("Create exam schedule successfully", id);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateExamSchedule(@PathVariable(name = "id") Long id, @RequestBody UpdateExamScheduleDTO updateExamScheduleDTO) {
        examService.updateExamSchedule(id, updateExamScheduleDTO);
        return ResponseHelper.success("Update exam schedule successfully", null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteExamSchedule(@PathVariable(name = "id") Long id) {
        examService.deleteExamSchedule(id);
        return ResponseHelper.success("Delete exam schedule successfully", null);
    }
}
