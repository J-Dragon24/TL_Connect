package com.tl_connect.dev.modules.study_program.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.semester.Semester;
import com.tl_connect.dev.modules.semester.SemesterRepository;
import com.tl_connect.dev.modules.study_program.dto.CreateStudyProgramSubDTO;
import com.tl_connect.dev.modules.study_program.dto.UpdateStudyProgramSubDTO;
import com.tl_connect.dev.modules.study_program.entity.StudyProgram;
import com.tl_connect.dev.modules.study_program.entity.StudyProgramSubject;
import com.tl_connect.dev.modules.study_program.repository.StudyProgramRepository;
import com.tl_connect.dev.modules.study_program.repository.StudyProgramSubjectRepository;
import com.tl_connect.dev.modules.subject.repository.SubjectRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.core.common.exception.BadRequestException;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyProgramSubjectService {
    private final StudyProgramSubjectRepository studyProgramSubjectRepository;
    private final StudyProgramRepository studyProgramRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;
    private final Validator validator;

    @Transactional
    public Long createStudyProgramSubject(Long studyProgramId, CreateStudyProgramSubDTO createStudyProgramSubjectDTO){
        Set<ConstraintViolation<CreateStudyProgramSubDTO>> violations = validator.validate(createStudyProgramSubjectDTO);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }

        StudyProgram program = studyProgramRepository.findById(studyProgramId)
                .orElseThrow(() -> new NotFoundException("Study program not found"));
        Semester semester = semesterRepository.findById(createStudyProgramSubjectDTO.getSemesterId())
                .orElseThrow(() -> new NotFoundException("Semester not found"));
        if(!subjectRepository.existsById(createStudyProgramSubjectDTO.getSubjectId())) {
            throw new NotFoundException("Subject not found");
        }


        if (semester.getStartDate().getYear() < program.getStartYear()) {
            throw new BadRequestException("Semester does not belong to the study program");
        }

        StudyProgramSubject studyProgramSubject = StudyProgramSubject.builder()
                .studyProgramId(studyProgramId)
                .semesterId(createStudyProgramSubjectDTO.getSemesterId())
                .subjectId(createStudyProgramSubjectDTO.getSubjectId())
                .isRequired(createStudyProgramSubjectDTO.getIsRequired())
                .electiveGroup(createStudyProgramSubjectDTO.getElectiveGroup())
                .build();
        try {
            studyProgramSubjectRepository.save(studyProgramSubject);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Invalid study program subject data: " + e.getMessage());
        }
        return studyProgramSubject.getId();
    }

    @Transactional
    public void updateStudyProgramSubject(Long studyProgramSubjectId, UpdateStudyProgramSubDTO updateStudyProgramSubjectDTO){
        Set<ConstraintViolation<UpdateStudyProgramSubDTO>> violations = validator.validate(updateStudyProgramSubjectDTO);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }

        StudyProgramSubject studyProgramSubject = studyProgramSubjectRepository.findById(studyProgramSubjectId)
                .orElseThrow(() -> new NotFoundException("Study program subject not found"));
        StudyProgram program = studyProgramRepository.findById(studyProgramSubject.getStudyProgramId())
                .orElseThrow(() -> new NotFoundException("Study program not found"));
        Semester semester = semesterRepository.findById(updateStudyProgramSubjectDTO.getSemesterId())
                .orElseThrow(() -> new NotFoundException("Semester not found"));

        if (semester.getStartDate().getYear() < program.getStartYear()) {
            throw new BadRequestException("Semester does not belong to the study program");
        }

        Optional.ofNullable(updateStudyProgramSubjectDTO.getSemesterId()).ifPresent(studyProgramSubject::setSemesterId);
        Optional.ofNullable(updateStudyProgramSubjectDTO.getIsRequired()).ifPresent(studyProgramSubject::setIsRequired);
        Optional.ofNullable(updateStudyProgramSubjectDTO.getElectiveGroup()).ifPresent(studyProgramSubject::setElectiveGroup);

        try {
            studyProgramSubjectRepository.save(studyProgramSubject);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Invalid study program subject data: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteStudyProgramSubject(Long studyProgramSubjectId){
        StudyProgramSubject studyProgramSubject = studyProgramSubjectRepository.findById(studyProgramSubjectId)
                .orElseThrow(() -> new NotFoundException("Study program subject not found"));
        studyProgramSubjectRepository.delete(studyProgramSubject);
    }
}
