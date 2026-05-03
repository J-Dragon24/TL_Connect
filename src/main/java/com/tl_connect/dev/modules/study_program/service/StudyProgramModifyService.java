package com.tl_connect.dev.modules.study_program.service;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.major.repository.MajorRepository;
import com.tl_connect.dev.modules.study_program.dto.CreateStudyProgramDTO;
import com.tl_connect.dev.modules.study_program.dto.UpdateStudyProgramDTO;
import com.tl_connect.dev.modules.study_program.entity.StudyProgram;
import com.tl_connect.dev.modules.study_program.repository.StudyProgramRepository;
import com.tl_connect.dev.shared.common.exception.BadRequestException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyProgramModifyService {

    private final StudyProgramRepository studyProgramRepository;
    private final MajorRepository majorRepository;

    @Transactional
    public Long createStudyProgram(CreateStudyProgramDTO createStudyProgramDTO){


        majorRepository.findById(createStudyProgramDTO.getMajorId())
                .orElseThrow(() -> new NotFoundException("Major not found"));


        StudyProgram studyProgram = StudyProgram.create(createStudyProgramDTO.getStudyProgramCode(), createStudyProgramDTO.getStudyProgramName(), createStudyProgramDTO.getMajorId(), createStudyProgramDTO.getStartYear(), createStudyProgramDTO.getTrainingType(), createStudyProgramDTO.getTotalCredits());
        try{
            studyProgramRepository.save(studyProgram);
        }catch(DataIntegrityViolationException e){
            throw new BadRequestException("Invalid study program data: " + e.getMessage());
        }
        return studyProgram.getId();
    }

    @Transactional
    public void updateStudyProgram(Long id, UpdateStudyProgramDTO updateStudyProgramDTO){

        StudyProgram studyProgram = studyProgramRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Study program not found"));

        if (updateStudyProgramDTO.getMajorId() != null) {
            majorRepository.findById(updateStudyProgramDTO.getMajorId())
                    .orElseThrow(() -> new NotFoundException("Major not found"));
        }
        studyProgram.update(updateStudyProgramDTO.getStudyProgramCode(), updateStudyProgramDTO.getStudyProgramName(), updateStudyProgramDTO.getMajorId(), updateStudyProgramDTO.getStartYear(), updateStudyProgramDTO.getTrainingType(), updateStudyProgramDTO.getTotalCredits());
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
        if(!studyProgram.getIsActive()){
            throw new BadRequestException("Study program is already inactive");
        }
        studyProgram.deactivate();
        try {
            studyProgramRepository.save(studyProgram);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Invalid study program data: " + e.getMessage());
        }
    }
}
