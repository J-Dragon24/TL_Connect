package com.tl_connect.dev.modules.chat.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.chat.dto.SimpleProfileStudentDTO;
import com.tl_connect.dev.modules.chat.dto.StudentChatInfoDTO;
import com.tl_connect.dev.modules.chat.service.interfaces.UserInfoService;
import com.tl_connect.dev.modules.student.entity.Student;
import com.tl_connect.dev.modules.student.service.interfaces.StudentService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService{
    private final StudentService studentService;

    @Override
    public PagedResponse<SimpleProfileStudentDTO> getAllSimpleProfileStudents(String search, Pageable pageable) {
        Page<Student> students = studentService.getAllStudentsBySearch(search, pageable);

        return new PagedResponse<>(
                students.getContent().stream().map(this::toSimpleDTO).toList(),
                students.getNumber(),
                students.getSize(),
                students.getTotalElements(),
                students.getTotalPages(),
                students.isFirst(),
                students.isLast());
    }

    @Override
    public StudentChatInfoDTO getStudentChatInfo(String code) {
        return studentService.getStudentChatInfo(code);
    }


    private SimpleProfileStudentDTO toSimpleDTO(Student student) {
        return SimpleProfileStudentDTO.builder()
                .avatarUrl(student.getAvatarUrl())
                .studentCode(student.getStudentCode())
                .fullName(student.getFullName())
                .build();
    }

}
