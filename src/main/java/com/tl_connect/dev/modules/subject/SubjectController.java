package com.tl_connect.dev.modules.subject;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.modules.subject.dto.CreateSubjectDTO;
import com.tl_connect.dev.modules.subject.dto.SubjectDTO;
import com.tl_connect.dev.modules.subject.dto.UpdateSubjectDTO;
import com.tl_connect.dev.modules.subject.entity.Subject;
import com.tl_connect.dev.modules.subject.service.interfaces.SubjectService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.ultility.ResponseHelper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllSubjects(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        PagedResponse<Subject> result = subjectService.getAllSubjects(pageable);
        return ResponseHelper.success("Get all subjects successfully", result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSubjectById(@PathVariable Long id) {
        SubjectDTO subjectDTO = subjectService.getSubjectById(id);
        return ResponseHelper.success("Get subject by id successfully", subjectDTO);
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
