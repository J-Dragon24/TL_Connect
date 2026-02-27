package com.tl_connect.dev.modules.study_program;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.core.common.exception.UnauthorizeException;
import com.tl_connect.dev.core.common.types.JwtUserInfo;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.study_program.dto.StudyProgramDTO;
import com.tl_connect.dev.modules.study_program.dto.StudyProgramListItemDTO;

import org.springframework.security.core.Authentication;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/study-programs")
@RequiredArgsConstructor
public class StudyProgramController {

    private final StudyProgramService studyProgramService;

    @GetMapping
    public ResponseEntity<?> getAllStudyProgram(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();

        List<StudyProgramListItemDTO> result = studyProgramService.getAllStudyProgram(studentId);
        return ResponseHelper.success("Study programs retrieved successfully", result);
    }

    @GetMapping("/{studyProgramCode}")
    public ResponseEntity<?> getStudyProgram(@PathVariable("studyProgramCode") String studyProgramCode) {
        StudyProgramDTO result = studyProgramService.getStudyProgram(studyProgramCode);
        return ResponseHelper.success("Study program retrieved successfully", result);
    }
}
