package com.tl_connect.dev.modules.study_program.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.exception.BadRequestException;
import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.major.repository.MajorRepository;
import com.tl_connect.dev.modules.study_program.dto.CreateStudyProgramDTO;
import com.tl_connect.dev.modules.study_program.dto.UpdateStudyProgramDTO;
import com.tl_connect.dev.modules.study_program.entity.StudyProgram;
import com.tl_connect.dev.modules.study_program.repository.StudyProgramRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyProgramModifyService {

    private final StudyProgramRepository studyProgramRepository;
    private final MajorRepository majorRepository;
    private final Validator validator;

    @Transactional
    public Long createStudyProgram(CreateStudyProgramDTO createStudyProgramDTO){
        Set<ConstraintViolation<CreateStudyProgramDTO>> violations = validator.validate(createStudyProgramDTO);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }

        majorRepository.findById(createStudyProgramDTO.getMajorId())
                .orElseThrow(() -> new NotFoundException("Major not found"));


        StudyProgram studyProgram = StudyProgram.builder()
                .studyProgramCode(createStudyProgramDTO.getStudyProgramCode())
                .studyProgramName(createStudyProgramDTO.getStudyProgramName())
                .majorId(createStudyProgramDTO.getMajorId())
                .startYear(createStudyProgramDTO.getStartYear())
                .totalCredits(createStudyProgramDTO.getTotalCredits())
                .trainingType(createStudyProgramDTO.getTrainingType())
                .build();
        try{
            studyProgramRepository.save(studyProgram);
        }catch(DataIntegrityViolationException e){
            throw new BadRequestException("Invalid study program data: " + e.getMessage());
        }
        return studyProgram.getId();
    }

    @Transactional
    public void updateStudyProgram(Long id, UpdateStudyProgramDTO updateStudyProgramDTO){
        Set<ConstraintViolation<UpdateStudyProgramDTO>> violations = validator.validate(updateStudyProgramDTO);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }

        StudyProgram studyProgram = studyProgramRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Study program not found"));

        if (updateStudyProgramDTO.getStudyProgramCode() != null) {
            studyProgram.setStudyProgramCode(updateStudyProgramDTO.getStudyProgramCode());
        }
        if (updateStudyProgramDTO.getStudyProgramName() != null) {
            studyProgram.setStudyProgramName(updateStudyProgramDTO.getStudyProgramName());
        }
        if (updateStudyProgramDTO.getMajorId() != null) {
            majorRepository.findById(updateStudyProgramDTO.getMajorId())
                    .orElseThrow(() -> new NotFoundException("Major not found"));
            studyProgram.setMajorId(updateStudyProgramDTO.getMajorId());
        }
        if (updateStudyProgramDTO.getStartYear() != null) {
            studyProgram.setStartYear(updateStudyProgramDTO.getStartYear());
        }
        if (updateStudyProgramDTO.getTotalCredits() != null) {
            studyProgram.setTotalCredits(updateStudyProgramDTO.getTotalCredits());
        }
        if (updateStudyProgramDTO.getTrainingType() != null) {
            studyProgram.setTrainingType(updateStudyProgramDTO.getTrainingType());
        }

        try {
            studyProgramRepository.save(studyProgram);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Invalid study program data: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteStudyProgram(Long id){
        StudyProgram studyProgram = studyProgramRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Study program not found"));
        studyProgramRepository.delete(studyProgram);
    }
}
