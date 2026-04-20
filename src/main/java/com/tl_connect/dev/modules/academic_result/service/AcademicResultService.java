package com.tl_connect.dev.modules.academic_result.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.modules.academic_result.AcademicResultRepository;
import com.tl_connect.dev.modules.academic_result.dto.AcademicResultAdmDTO;
import com.tl_connect.dev.modules.academic_result.dto.AcademicResultByStudyProgramDTO;
import com.tl_connect.dev.modules.academic_result.dto.AcademicResultDTO;
import com.tl_connect.dev.modules.academic_result.dto.SemesterResultDTO;
import com.tl_connect.dev.modules.academic_result.dto.SemesterSummaryDTO;
import com.tl_connect.dev.modules.academic_result.dto.SubjectResultDTO;
import com.tl_connect.dev.modules.academic_result.projection.SemesterSummaryRow;
import com.tl_connect.dev.modules.academic_result.projection.SemesterSummaryView;
import com.tl_connect.dev.modules.academic_result.projection.SubjectResultAdmRow;
import com.tl_connect.dev.modules.academic_result.projection.SubjectResultRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AcademicResultService {
        private final AcademicResultRepository resultRepository;

        public PagedResponse<AcademicResultAdmDTO> getAllAcademicResult(Pageable pageable, String facultyCode) {
                Page<SubjectResultAdmRow> subjectResultsRows = resultRepository.findSubjectResult(pageable, facultyCode);

                List<Long> studentIds = subjectResultsRows.stream().map(SubjectResultAdmRow::getStudentId).collect(Collectors.toList());
                List<SemesterSummaryRow> semesterSummaries = resultRepository.findSemesterSummaryByStudentIds(studentIds);

                Map<Long, Map<String, Map<String, List<SubjectResultAdmRow>>>> grouped =
                        subjectResultsRows.stream()
                                .collect(Collectors.groupingBy(
                                        SubjectResultAdmRow::getStudentId,
                                        Collectors.groupingBy(
                                                SubjectResultAdmRow::getStudyProgramCode,
                                                Collectors.groupingBy(
                                                        SubjectResultAdmRow::getSemester
                                                )
                                        )
                                ));
                Map<String, SemesterSummaryRow> summaryMap = semesterSummaries.stream()
                .collect(Collectors.toMap(
                        s -> (String) (s.getStudentId() + "_" + s.getStudyProgramCode() + "_" + s.getSemester()),
                        s -> s
                ));

                List<AcademicResultAdmDTO> result = new ArrayList<>();

                for (var studentEntry : grouped.entrySet()) {
                        Long studentId = studentEntry.getKey();

                        AcademicResultAdmDTO student = new AcademicResultAdmDTO();
                        student.setStudentId(studentId);

                        List<AcademicResultByStudyProgramDTO> programs = new ArrayList<>();

                        for (var programEntry : studentEntry.getValue().entrySet()) {
                                String studyProgramCode = programEntry.getKey();

                                AcademicResultByStudyProgramDTO program = new AcademicResultByStudyProgramDTO();
                                program.setStudyProgramCode(studyProgramCode);

                                List<SemesterResultDTO> semesters = new ArrayList<>();

                                for (var semesterEntry : programEntry.getValue().entrySet()) {
                                        String semesterName = semesterEntry.getKey();

                                        SemesterResultDTO semester = new SemesterResultDTO();
                                        semester.setSemester(semesterName);

                                        List<SubjectResultAdmRow> subjectResults = semesterEntry.getValue();
                                        List<SubjectResultDTO> subjectResultDTOs = subjectResults.stream().map(s -> {
                                                return SubjectResultDTO.builder()
                                                                .subjectCode(s.getSubjectCode())
                                                                .subjectName(s.getSubjectName())
                                                                .credits(s.getCredits())
                                                                .attendanceScore(s.getAttendanceScore())
                                                                .midtermScore(s.getMidtermScore())
                                                                .finalScore(s.getFinalScore())
                                                                .score10(s.getScore10())
                                                                .score4(s.getScore4())
                                                                .letterGrade(s.getLetterGrade())
                                                                .isPass(s.getIsPass())
                                                                .build();
                                        }).collect(Collectors.toList());

                                        student.setStudentCode(subjectResults.get(0).getStudentCode());
                                        student.setStudentName(subjectResults.get(0).getStudentName());
                                        student.setStartYear(subjectResults.get(0).getStartYear());

                                        program.setStudyProgramName(subjectResults.get(0).getStudyProgramName());
                                        program.setMajorName(subjectResults.get(0).getMajorName());

                                        semester.setSubjectResults(subjectResultDTOs);

                                        String key = (String) (studentId + "_" + studyProgramCode + "_" + semesterName);
                                        SemesterSummaryRow summary = summaryMap.get(key);

                                        if (summary != null) {
                                                SemesterSummaryDTO semesterSummaryDTO = new SemesterSummaryDTO();
                                                semesterSummaryDTO.setCreditsRegistered(summary.getCreditsRegistered());
                                                semesterSummaryDTO.setCreditsPassed(summary.getCreditsPassed());
                                                semesterSummaryDTO.setSemesterGpa(summary.getSemesterGpa());
                                                semesterSummaryDTO.setConductScore(summary.getConductScore());
                                                semester.setSemesterSummary(semesterSummaryDTO);
                                        }

                                        semesters.add(semester);
                                }

                                program.setSemesterResults(semesters);
                                programs.add(program);
                                }

                        student.setStudyPrograms(programs);
                        
                        result.add(student);
                        }

                return new PagedResponse<>(
                result,
                subjectResultsRows.getNumber(),
                subjectResultsRows.getSize(),
                subjectResultsRows.getTotalElements(),
                subjectResultsRows.getTotalPages(),
                subjectResultsRows.isFirst(),
                subjectResultsRows.isLast());
        }

        public AcademicResultDTO getSubjectResult(Long studentId, String studyProgramCode) {
                List<SubjectResultRow> subjectResultsRows = resultRepository.findSubjectResultByStudentIdAndStudyProgramCode(studentId,
                                studyProgramCode);
                List<SemesterSummaryView> semesterSummaries = resultRepository.findSemesterSummaryByStudentIdAndStudyProgramCode(studentId,
                                studyProgramCode);

                System.out.println("Semester Summaries: " + semesterSummaries);

                Map<String, List<SubjectResultRow>> groupedBySemester = subjectResultsRows.stream()
                                .collect(Collectors.groupingBy(SubjectResultRow::getSemester));

                Map<String, SemesterSummaryView> map = new HashMap<>();
                semesterSummaries.forEach(s -> map.put(s.getSemester(), s));

                List<SemesterResultDTO> semesterResults = groupedBySemester.entrySet().stream().map(entry -> {
                        String semester = entry.getKey();
                        List<SubjectResultRow> subjectResults = entry.getValue();
                        SemesterSummaryView semesterSummary = map.get(semester);
                        List<SubjectResultDTO> subjectResultDTOs = subjectResults.stream().map(s -> {
                                return SubjectResultDTO.builder()
                                                .subjectCode(s.getSubjectCode())
                                                .subjectName(s.getSubjectName())
                                                .credits(s.getCredits())
                                                .attendanceScore(s.getAttendanceScore())
                                                .midtermScore(s.getMidtermScore())
                                                .finalScore(s.getFinalScore())
                                                .score10(s.getScore10())
                                                .score4(s.getScore4())
                                                .letterGrade(s.getLetterGrade())
                                                .isPass(s.getIsPass())
                                                .build();
                        }).collect(Collectors.toList());
                        SemesterSummaryDTO semesterSummaryDTO = null;
                        if (semesterSummary != null) {
                                semesterSummaryDTO = SemesterSummaryDTO.builder()
                                        .creditsRegistered(semesterSummary.getCreditsRegistered())
                                        .creditsPassed(semesterSummary.getCreditsPassed())
                                        .semesterGpa(semesterSummary.getSemesterGpa())
                                        .conductScore(semesterSummary.getConductScore())
                                        .build();
                        }
                        return SemesterResultDTO.builder()
                                        .semester(semester)
                                        .subjectResults(subjectResultDTOs)
                                        .semesterSummary(semesterSummaryDTO)
                                        .build();
                }).collect(Collectors.toList());

                return AcademicResultDTO.builder()
                                .studyProgram(studyProgramCode)
                                .semesterResults(semesterResults)
                                .build();
        }
}
