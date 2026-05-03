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
import com.tl_connect.dev.shared.common.exception.BadRequestException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;

import java.util.Optional;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyProgramSubjectService {
    private final StudyProgramSubjectRepository studyProgramSubjectRepository;
    private final StudyProgramRepository studyProgramRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public Long createStudyProgramSubject(Long studyProgramId, CreateStudyProgramSubDTO createStudyProgramSubjectDTO){

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
                .build();

        if(createStudyProgramSubjectDTO.getElectiveGroup() != null) {
            studyProgramSubject.setElectiveGroup(createStudyProgramSubjectDTO.getElectiveGroup());
        }
        
        try {
            studyProgramSubjectRepository.save(studyProgramSubject);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Invalid study program subject data: " + e.getMessage());
        }
        return studyProgramSubject.getId();
    }

    @Transactional
    public void updateStudyProgramSubject(Long studyProgramSubjectId, UpdateStudyProgramSubDTO updateStudyProgramSubjectDTO){

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
