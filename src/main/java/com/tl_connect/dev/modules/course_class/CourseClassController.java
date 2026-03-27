package com.tl_connect.dev.modules.course_class;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.course_class.dto.CreateCourseClassDTO;
import com.tl_connect.dev.modules.course_class.dto.UpdateCourseClassDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/course-classes")
@RequiredArgsConstructor
public class CourseClassController {

    private final CourseClassService courseClassService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseHelper.success("Get all course classes successfully",courseClassService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Long id) {
        return ResponseHelper.success("Get detail course class successfully",courseClassService.getDetailById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody CreateCourseClassDTO dto) {
        return ResponseHelper.success("Create course class successfully",courseClassService.create(dto));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateCourseClassDTO dto) {
        courseClassService.update(id, dto);
        return ResponseHelper.success("Update course class successfully",null);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        courseClassService.delete(id);
        return ResponseHelper.success("Delete course class successfully",null);
    }
}
