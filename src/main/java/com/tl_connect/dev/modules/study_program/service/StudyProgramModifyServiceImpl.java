package com.tl_connect.dev.modules.study_program.service;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.major.repository.MajorRepository;
import com.tl_connect.dev.modules.study_program.dto.CreateStudyProgramDTO;
import com.tl_connect.dev.modules.study_program.dto.UpdateStudyProgramDTO;
import com.tl_connect.dev.modules.study_program.entity.StudyProgram;
import com.tl_connect.dev.modules.study_program.repository.StudyProgramRepository;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.shared.ultility.CacheHelper;
import com.tl_connect.dev.modules.study_program.service.interfaces.StudyProgramCacheService;
import com.tl_connect.dev.modules.study_program.service.interfaces.StudyProgramModifyService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyProgramModifyServiceImpl implements StudyProgramModifyService {

    private final StudyProgramRepository studyProgramRepository;
    private final MajorRepository majorRepository;
    private final CacheHelper cacheHelper;
    private final StudyProgramCacheService studyProgramCacheService;

    @Transactional
    public Long createStudyProgram(CreateStudyProgramDTO createStudyProgramDTO){

        majorRepository.findById(createStudyProgramDTO.getMajorId())
                .orElseThrow(() -> new NotFoundException("Major not found"));


        StudyProgram studyProgram = StudyProgram.create(createStudyProgramDTO.getStudyProgramCode(), createStudyProgramDTO.getStudyProgramName(), createStudyProgramDTO.getMajorId(), createStudyProgramDTO.getStartYear(), createStudyProgramDTO.getTrainingType(), createStudyProgramDTO.getTotalCredits());
        try{
            studyProgramRepository.save(studyProgram);
        }catch(DataIntegrityViolationException e){
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Invalid study program data");
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
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Invalid study program data");
        }
        cacheHelper.evictAfterCommit(() -> studyProgramCacheService.evict(studyProgram.getId()));
    }

    @Transactional
    public void deleteStudyProgram(Long id){
        StudyProgram studyProgram = studyProgramRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Study program not found"));
        if(!studyProgram.getIsActive()){
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Study program is already inactive");
        }
        studyProgram.deactivate();
        try {
            studyProgramRepository.save(studyProgram);
        } catch (DataIntegrityViolationException e) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Invalid study program data: " + e.getMessage());
        }
        cacheHelper.evictAfterCommit(() -> studyProgramCacheService.evict(studyProgram.getId()));
    }
}
