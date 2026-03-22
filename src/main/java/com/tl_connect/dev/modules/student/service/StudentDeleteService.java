package com.tl_connect.dev.modules.student.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.core.common.enums.StudentStatus;
import com.tl_connect.dev.core.common.enums.UserStatus;
import com.tl_connect.dev.core.common.exception.BadRequestException;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.oauth.OAuthUserRepository;
import com.tl_connect.dev.modules.oauth.entity.OAuthUser;
import com.tl_connect.dev.modules.student.entity.Student;
import com.tl_connect.dev.modules.student.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentDeleteService {

    private final StudentRepository studentRepository;
    private final OAuthUserRepository oauthUserRepository;

    @Transactional
    public void deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        if (StudentStatus.DELETED.equals(student.getStatus())) {
            throw new BadRequestException("Student already deleted");
        }

        student.setStatus(StudentStatus.DELETED);

        OAuthUser oauthUser = oauthUserRepository.findById(student.getOauthUserId())
                .orElseThrow(() -> new NotFoundException("OAuth user not found"));
        oauthUser.setStatus(UserStatus.BLOCKED);
    }
}
