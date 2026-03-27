package com.tl_connect.dev.modules.exam;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.exam.dto.ExamScheduleBasicInfoDTO;
import com.tl_connect.dev.modules.exam.dto.ExamScheduleDTO;
import com.tl_connect.dev.modules.exam.dto.ExamScheduleDetailDTO;
import com.tl_connect.dev.modules.exam.projection.ExamScheduleAdminRow;
import com.tl_connect.dev.modules.exam.projection.ExamScheduleView;
import com.tl_connect.dev.modules.faculty.FacultyRepository;
import com.tl_connect.dev.modules.semester.Semester;
import com.tl_connect.dev.modules.semester.SemesterRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamService {
        private final ExamRepository examRepository;
        private final SemesterRepository semesterRepository;
        private final FacultyRepository facultyRepository;

        public ExamScheduleDTO getExamSchedule(Long studentId, String semesterName) {
                Semester semester = semesterRepository.findSemesterByName(semesterName)
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

        

        private ExamScheduleBasicInfoDTO toDTO(ExamScheduleAdminRow examSchedule) {
                return ExamScheduleBasicInfoDTO.builder()
                                .id(examSchedule.getId())
                                .subjectCode(examSchedule.getSubjectCode())
                                .classCode(examSchedule.getClassCode())
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
