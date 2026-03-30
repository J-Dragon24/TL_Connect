package com.tl_connect.dev.modules.study_program.controller;


import org.springframework.data.domain.PageRequest;
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
import com.tl_connect.dev.modules.study_program.dto.CreateStudyProgramDTO;
import com.tl_connect.dev.modules.study_program.dto.CreateStudyProgramSubDTO;
import com.tl_connect.dev.modules.study_program.dto.StudyProgramAdmDTO;
import com.tl_connect.dev.modules.study_program.dto.StudyProgramDTO;
import com.tl_connect.dev.modules.study_program.dto.UpdateStudyProgramDTO;
import com.tl_connect.dev.modules.study_program.dto.UpdateStudyProgramSubDTO;
import com.tl_connect.dev.modules.study_program.service.StudyProgramModifyService;
import com.tl_connect.dev.modules.study_program.service.StudyProgramService;
import com.tl_connect.dev.modules.study_program.service.StudyProgramSubjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/study-programs")
@RequiredArgsConstructor
public class StudyProgramAdminController {
    
    private final StudyProgramModifyService studyProgramModifyService;
    private final StudyProgramService studyProgramService;
    private final StudyProgramSubjectService studyProgramSubjectService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllStudyProgram(@PageableDefault(page = 0, size = 10) Pageable pageable, @RequestParam(required = true) Integer startYear){
        PagedResponse<StudyProgramAdmDTO> result = studyProgramService.getAllStudyProgram(pageable, startYear);
        return ResponseHelper.success("Study programs retrieved successfully", result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudyProgram(@PathVariable Long id){
        StudyProgramDTO result = studyProgramService.getDetailedStudyProgram(id);
        return ResponseHelper.success("Study program retrieved successfully", result);
    }


    @PostMapping
    public ResponseEntity<?> createStudyProgram(@RequestBody CreateStudyProgramDTO createStudyProgramDTO){
        Long id = studyProgramModifyService.createStudyProgram(createStudyProgramDTO);
        return ResponseHelper.success("Study program created successfully", id);
    }

    @PostMapping("update/{id}")
    public ResponseEntity<?> updateStudyProgram(@PathVariable Long id, @RequestBody UpdateStudyProgramDTO updateStudyProgramDTO){
        studyProgramModifyService.updateStudyProgram(id, updateStudyProgramDTO);
        return ResponseHelper.success("Study program updated successfully", null);
    }

    @PostMapping("delete/{id}")
    public ResponseEntity<?> deleteStudyProgram(@PathVariable Long id){
        studyProgramModifyService.deleteStudyProgram(id);
        return ResponseHelper.success("Study program deleted successfully", null);
    }

    @PostMapping("{id}/subjects/create")
    public ResponseEntity<?> addSubjectToStudyProgram(@PathVariable Long id, @RequestBody CreateStudyProgramSubDTO createStudyProgramSubDTO){
        studyProgramSubjectService.createStudyProgramSubject(id, createStudyProgramSubDTO);
        return ResponseHelper.success("Subject added to study program successfully", null);
    }

    @PostMapping("subjects/update/{id}")
    public ResponseEntity<?> updateSubjectToStudyProgram(@PathVariable Long id, @RequestBody UpdateStudyProgramSubDTO updateStudyProgramSubDTO){
        studyProgramSubjectService.updateStudyProgramSubject(id, updateStudyProgramSubDTO);
        return ResponseHelper.success("Subject updated to study program successfully", null);
    }

    @PostMapping("subjects/delete/{id}")
    public ResponseEntity<?> removeSubjectFromStudyProgram(@PathVariable Long id){
        studyProgramSubjectService.deleteStudyProgramSubject(id);
        return ResponseHelper.success("Subject removed from study program successfully", null);
    }
}
