package com.tl_connect.dev.modules.course_class;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.modules.course_class.dto.CreateCourseClassDTO;
import com.tl_connect.dev.modules.course_class.dto.UpdateCourseClassDTO;
import com.tl_connect.dev.modules.course_class.service.interfaces.CourseClassService;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/course-classes")
@RequiredArgsConstructor
public class CourseClassController {

    private final CourseClassService courseClassService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll( @PageableDefault(page = 0, size = 10) Pageable pageable, @RequestParam(required = false, name = "khoa") String facultyCode, @RequestParam(required = false, name = "HocKy") String semesterCode) {
        return ResponseHelper.success("Get all course classes successfully",courseClassService.getAll(pageable, facultyCode, semesterCode));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Long id) {
        return ResponseHelper.success("Get detail course class successfully",courseClassService.getDetailById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody CreateCourseClassDTO dto) {
        return ResponseHelper.success("Create course class successfully",courseClassService.create(dto));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateCourseClassDTO dto) {
        courseClassService.update(id, dto);
        return ResponseHelper.success("Update course class successfully",null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        courseClassService.delete(id);
        return ResponseHelper.success("Delete course class successfully",null);
    }
}
