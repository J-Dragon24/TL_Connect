package com.tl_connect.dev.modules.academic_result.service;

import com.tl_connect.dev.modules.academic_result.entity.StudentSubjectResult;
import com.tl_connect.dev.modules.academic_result.repository.StudentSubjectResultRepository;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class AcademicResultPersistService {
    private final StudentSubjectResultRepository repository;

    @Transactional
    public void persistChunk(List<StudentSubjectResult> chunk) {
        repository.saveAll(chunk);
    }
}
