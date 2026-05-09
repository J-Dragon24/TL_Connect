package com.tl_connect.dev.modules.exam;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.exam.dto.CreateExamScheduleDTO;
import com.tl_connect.dev.modules.exam.dto.ExamScheduleBasicInfoDTO;
import com.tl_connect.dev.modules.exam.dto.ExamScheduleDTO;
import com.tl_connect.dev.modules.exam.dto.ExamScheduleDetailDTO;
import com.tl_connect.dev.modules.exam.dto.UpdateExamScheduleDTO;
import com.tl_connect.dev.modules.exam.entity.ExamSchedule;
import com.tl_connect.dev.modules.exam.projection.ExamScheduleAdminRow;
import com.tl_connect.dev.modules.exam.projection.ExamScheduleView;
import com.tl_connect.dev.modules.faculty.FacultyRepository;
import com.tl_connect.dev.modules.semester.Semester;
import com.tl_connect.dev.modules.semester.SemesterRepository;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ConflictException;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;
    private final SemesterRepository semesterRepository;
    private final FacultyRepository facultyRepository;

    public ExamScheduleDTO getExamSchedule(Long studentId, String semesterCode) {
            Semester semester = semesterRepository.findBySemesterCode(semesterCode)
                            .orElseThrow(() -> new NotFoundException("Semester not found"));

            List<ExamScheduleView> examSchedules = examRepository.findExamSchedule(studentId, semester.getId());

            List<ExamScheduleDetailDTO> examScheduleDetails = examSchedules.stream()
                            .map(examSchedule -> ExamScheduleDetailDTO.builder()
                                            .subjectCode(examSchedule.getSubjectCode())
                                            .subjectName(examSchedule.getSubjectName())
                                            .classCode(examSchedule.getClassCode())
                                            .examDate(examSchedule.getExamDate())
                                            .startTime(examSchedule.getStartTime())
                                            .endTime(examSchedule.getEndTime())
                                            .examRoom(examSchedule.getExamRoom())
                                            .examLocation(examSchedule.getExamLocation())
                                            .examFormat(examSchedule.getExamFormat())
                                            .examType(examSchedule.getExamType())
                                            .examAttempt(examSchedule.getExamAttempt())
                                            .attendanceStatus(examSchedule.getAttendanceStatus())
                                            .build())
                            .toList();
            return ExamScheduleDTO.builder()
                            .semesterName(semester.getSemesterName())
                            .examSchedules(examScheduleDetails)
                            .build();
    }

    public PagedResponse<ExamScheduleBasicInfoDTO> getExamSchedule(Long semesterId, Pageable pageable, Long facultyId) {
            
            if(!semesterRepository.existsById(semesterId)){
                throw new NotFoundException("Semester not found");
            }

            Page<ExamScheduleAdminRow> page;
            if(facultyId != null){
                if(!facultyRepository.existsById(facultyId)){
                    throw new NotFoundException("Faculty not found");
                }
                page = examRepository.findAllExamScheduleByFacultyId(semesterId, facultyId, pageable);
            }else{
                page = examRepository.findAllExamSchedule(semesterId, pageable);
            }
            return new PagedResponse<>(
                    page.getContent().stream().map(this::toDTO).toList(),
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages(),
                    page.isFirst(),
                    page.isLast());
    }

    @Transactional
    public Long createExamSchedule(CreateExamScheduleDTO createExamScheduleDTO){

        if (examRepository.existsConflict(
                createExamScheduleDTO.getExamDate(),
                createExamScheduleDTO.getSemesterId(),
                createExamScheduleDTO.getExamRoom(),
                createExamScheduleDTO.getStartTime(),
                createExamScheduleDTO.getEndTime()
        )) {
            throw new ConflictException("Exam schedule conflict");
        }

        ExamSchedule examSchedule = ExamSchedule.create(
        createExamScheduleDTO.getSubjectId(),
        createExamScheduleDTO.getSemesterId(),
        createExamScheduleDTO.getExamDate(),
        createExamScheduleDTO.getStartTime(),
        createExamScheduleDTO.getEndTime(),
        createExamScheduleDTO.getExamRoom(),
        createExamScheduleDTO.getExamLocation(),
        createExamScheduleDTO.getExamFormat(),
        createExamScheduleDTO.getExamType(),
        createExamScheduleDTO.getNote()
        );

        try{
            examRepository.save(examSchedule);
        }catch(DataIntegrityViolationException e){
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Invalid exam schedule data: " + e.getMessage());
        }
        return examSchedule.getId();
    }

    @Transactional
    public void updateExamSchedule(Long id, UpdateExamScheduleDTO updateExamScheduleDTO){
        
        ExamSchedule examSchedule = examRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Exam schedule not found"));

        examSchedule.update(
            updateExamScheduleDTO.getSubjectId(),
            updateExamScheduleDTO.getSemesterId(),
            updateExamScheduleDTO.getExamDate(),
            updateExamScheduleDTO.getStartTime(),
            updateExamScheduleDTO.getEndTime(),
            updateExamScheduleDTO.getExamRoom(),
            updateExamScheduleDTO.getExamLocation(),
            updateExamScheduleDTO.getExamFormat(),
            updateExamScheduleDTO.getExamType(),
            updateExamScheduleDTO.getNote()
        );
        
        if(examRepository.existsConflictExcludingId(examSchedule.getExamDate(), examSchedule.getSemesterId(), examSchedule.getExamRoom(), examSchedule.getStartTime(), examSchedule.getEndTime(), id)){
            throw new ConflictException("Exam schedule conflict");
        }

        try{
            examRepository.save(examSchedule);
        }catch(DataIntegrityViolationException e){
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Invalid exam schedule data: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteExamSchedule(Long id){
        ExamSchedule examSchedule = examRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Exam schedule not found"));
        try{
            examRepository.delete(examSchedule);
        }catch(DataIntegrityViolationException e){
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Invalid exam schedule data: " + e.getMessage());
        }
    }
    

    private ExamScheduleBasicInfoDTO toDTO(ExamScheduleAdminRow examSchedule) {
            return ExamScheduleBasicInfoDTO.builder()
                            .id(examSchedule.getId())
                            .subjectCode(examSchedule.getSubjectCode())
                            .subjectName(examSchedule.getSubjectName())
                            .examDate(examSchedule.getExamDate())
                            .startTime(examSchedule.getStartTime())
                            .endTime(examSchedule.getEndTime())
                            .examRoom(examSchedule.getExamRoom())
                            .examLocation(examSchedule.getExamLocation())
                            .examFormat(examSchedule.getExamFormat())
                            .examType(examSchedule.getExamType())
                            .build();
    }
}
