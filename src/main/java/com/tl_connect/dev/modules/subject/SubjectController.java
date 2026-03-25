package com.tl_connect.dev.modules.subject;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.subject.dto.CreateSubjectDTO;
import com.tl_connect.dev.modules.subject.dto.UpdateSubjectDTO;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllSubjects(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseHelper.success("Get all subjects successfully", subjectService.getAllSubjects(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSubjectById(@PathVariable Long id) {
        return ResponseHelper.success("Get subject by id successfully", subjectService.getSubjectById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSubject(@Valid @RequestBody CreateSubjectDTO dto) {
        Long id = subjectService.create(dto);
        return ResponseHelper.success("Create subject successfully", id);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateSubject(@PathVariable Long id, @Valid @RequestBody UpdateSubjectDTO dto) {
        subjectService.update(id, dto);
        return ResponseHelper.success("Update subject successfully", null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteSubject(@PathVariable Long id) {
        subjectService.delete(id);
        return ResponseHelper.success("Delete subject successfully", null);
    }
}
