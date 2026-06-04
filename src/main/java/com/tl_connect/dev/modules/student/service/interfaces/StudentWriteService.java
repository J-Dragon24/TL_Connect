package com.tl_connect.dev.modules.student.service.interfaces;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.modules.student.dto.ResolvedStudent;
import com.tl_connect.dev.modules.student.dto.StudentImportDTO;
import com.tl_connect.dev.shared.common.dto.ImportResultDTO;

public interface StudentWriteService {

    Long createStudent(StudentImportDTO dto);

    ImportResultDTO importFile(MultipartFile file) throws IOException;

    void saveBatch(List<ResolvedStudent> batch);

    void saveSingle(ResolvedStudent rs);
}
