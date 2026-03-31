package com.tl_connect.dev.modules.academic_result.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.academic_result.AcademicResultRepository;
import com.tl_connect.dev.modules.academic_result.dto.AcademicResultDTO;
import com.tl_connect.dev.modules.academic_result.dto.SemesterResultDTO;
import com.tl_connect.dev.modules.academic_result.dto.SemesterSummaryDTO;
import com.tl_connect.dev.modules.academic_result.dto.SubjectResultDTO;
import com.tl_connect.dev.modules.academic_result.projection.SemesterSummaryView;
import com.tl_connect.dev.modules.academic_result.projection.SubjectResultRow;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AcademicResultService {
        private final AcademicResultRepository resultRepository;

        public AcademicResultDTO getSubjectResult(Long studentId, String studyProgramCode) {
                List<SubjectResultRow> subjectResultsRows = resultRepository.findSubjectResult(studentId,
                                studyProgramCode);
                List<SemesterSummaryView> semesterSummaries = resultRepository.findSemesterSummary(studentId,
                                studyProgramCode);

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
