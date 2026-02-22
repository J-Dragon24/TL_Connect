package com.tl_connect.dev.modules.exam;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.exam.dto.ExamScheduleDTO;
import com.tl_connect.dev.modules.exam.dto.ExamScheduleDetailDTO;
import com.tl_connect.dev.modules.exam.projection.ExamScheduleView;
import com.tl_connect.dev.modules.schedule.SemesterRepository;
import com.tl_connect.dev.modules.study_program.entity.Semester;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamService {
        private final ExamRepository examRepository;
        private final SemesterRepository semesterRepository;

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
                                                .examStatus(examSchedule.getExamStatus())
                                                .build())
                                .toList();
                return ExamScheduleDTO.builder()
                                .semesterName(semester.getSemesterName())
                                .examSchedules(examScheduleDetails)
                                .build();
        }
}
