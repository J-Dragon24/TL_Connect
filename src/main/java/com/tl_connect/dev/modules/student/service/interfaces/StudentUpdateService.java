package com.tl_connect.dev.modules.student.service.interfaces;

import com.tl_connect.dev.modules.student.dto.UpdateBasicInfoDTO;
import com.tl_connect.dev.modules.student.dto.UpdateStudentAcademicDTO;

public interface StudentUpdateService {

    void updateBasicInfo(Long studentId, UpdateBasicInfoDTO dto);

    void updateAcademicInfo(Long studentId, UpdateStudentAcademicDTO dto);
}
