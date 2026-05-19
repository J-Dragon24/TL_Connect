package com.tl_connect.dev.modules.academic_result.service.interfaces;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.modules.academic_result.dto.CreateStudentSubjectResultDTO;
import com.tl_connect.dev.modules.academic_result.dto.UpdateStudentSubjectResultDTO;
import com.tl_connect.dev.shared.common.dto.ImportResultDTO;

public interface AcademicResultModifyService {

    Long createStudentSubjectResult(CreateStudentSubjectResultDTO dto);

    ImportResultDTO importFile(MultipartFile file) throws IOException;

    void updateStudentSubjectResult(Long id, UpdateStudentSubjectResultDTO dto);

    void deleteStudentSubjectResult(Long id);
}
