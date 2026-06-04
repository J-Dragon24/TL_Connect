package com.tl_connect.dev.modules.chat.service.interfaces;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.chat.dto.SimpleProfileStudentDTO;
import com.tl_connect.dev.modules.chat.dto.StudentChatInfoDTO;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

public interface UserInfoService {
    PagedResponse<SimpleProfileStudentDTO> getAllSimpleProfileStudents(String search, Pageable pageable);

    StudentChatInfoDTO getStudentChatInfo(String code);
}
