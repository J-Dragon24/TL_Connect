package com.tl_connect.dev.modules.student.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.oauth.entity.OAuthUser;
import com.tl_connect.dev.modules.oauth.service.interfaces.OAuthService;
import com.tl_connect.dev.modules.student.entity.Student;
import com.tl_connect.dev.modules.student.repository.StudentRepository;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.enums.StudentStatus;
import com.tl_connect.dev.shared.common.enums.UserStatus;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.shared.common.ultility.CacheHelper;
import com.tl_connect.dev.modules.student.service.interfaces.StudentCacheService;
import com.tl_connect.dev.modules.student.service.interfaces.StudentDeleteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentDeleteServiceImpl implements StudentDeleteService {

    private final StudentRepository studentRepository;
    private final OAuthService oauthService;
    private final StudentCacheService studentCacheService;
    private final CacheHelper cacheHelper;

    @Transactional
    public void deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        if (StudentStatus.DELETED.equals(student.getStatus())) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Student already deleted");
        }

        student.setStatus(StudentStatus.DELETED);

        if (student.getOauthUserId() != null) {
            OAuthUser oauthUser = oauthService.findById(student.getOauthUserId());
            oauthUser.setStatus(UserStatus.BLOCKED);
        }

        cacheHelper.evictAfterCommit(() -> studentCacheService.evict(studentId));
    }
}
