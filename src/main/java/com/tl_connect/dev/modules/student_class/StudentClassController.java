package com.tl_connect.dev.modules.student_class;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.tl_connect.dev.modules.student_class.dto.CreateStudentClassDTO;
import com.tl_connect.dev.modules.student_class.dto.UpdateStudentClassDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student-class")
@RequiredArgsConstructor
public class StudentClassController {

    private final StudentClassService studentClassService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<?> result = studentClassService.getAll(pageable);
        return ResponseHelper.success("Get all student classes successfully", result);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody CreateStudentClassDTO dto) {
        Long id = studentClassService.create(dto);
        return ResponseHelper.success("Create student class successfully", id);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateStudentClassDTO dto) {
        studentClassService.update(id, dto);
        return ResponseHelper.success("Update student class successfully", null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        studentClassService.delete(id);
        return ResponseHelper.success("Delete student class successfully", null);
    }
}
