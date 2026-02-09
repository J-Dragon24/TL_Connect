package com.tl_connect.dev.student;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tl_connect.dev.common.types.JwtUserInfo;
import com.tl_connect.dev.common.dto.ResponseHelper;
import com.tl_connect.dev.common.exception.InvalidInputException;
import com.tl_connect.dev.student.dto.StudentInfoDTO;
import com.tl_connect.dev.student_class.dto.StudentClassInfoDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController {
    private final StudentInfoService studentService;

    @GetMapping("/me")
    public ResponseEntity<?> getStudentInfo(JwtUserInfo userInfo) {
        try {
            Long studentId = userInfo.userId();

            if (studentId == null) {
                throw new InvalidInputException("Student id is required");
            }
            StudentInfoDTO studentInfo = studentService.getStudentInfo(studentId);
            return ResponseHelper.success("Student info retrieved successfully", studentInfo);
        } catch (Exception e) {
            return ResponseHelper.internalError(e.getMessage());
        }
    }

    @GetMapping("/me/class")
    public ResponseEntity<?> getStudentClassInfo(JwtUserInfo userInfo) {
        try {
            Long studentId = userInfo.userId();

            if (studentId == null) {
                throw new InvalidInputException("Student id is required");
            }
            StudentClassInfoDTO studentClassInfo = studentService.getStudentClassInfo(studentId);
            return ResponseHelper.success("Student class info retrieved successfully", studentClassInfo);
        } catch (Exception e) {
            return ResponseHelper.internalError(e.getMessage());
        }

    }
}
