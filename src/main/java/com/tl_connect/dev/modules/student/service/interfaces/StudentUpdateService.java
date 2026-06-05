package com.tl_connect.dev.modules.student.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.modules.student.dto.SelfUpdateRequestDTO;
import com.tl_connect.dev.modules.student.dto.UpdateBasicInfoDTO;
import com.tl_connect.dev.modules.student.dto.UpdateStudentAcademicDTO;

public interface StudentUpdateService {

    void updateBasicInfo(Long studentId, UpdateBasicInfoDTO dto);

    void updateAcademicInfo(Long studentId, UpdateStudentAcademicDTO dto);

    void updateSelfInfo(Long studentId, SelfUpdateRequestDTO dto);

    void updateAvatar(Long studentId, MultipartFile file);
}
