package com.tl_connect.dev.modules.academic_result;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.academic_result.dto.AcademicResultDTO;
import com.tl_connect.dev.modules.academic_result.dto.SemesterResultDTO;
import com.tl_connect.dev.modules.academic_result.dto.SemesterSummaryDTO;
import com.tl_connect.dev.modules.academic_result.dto.SubjectResultDTO;
import com.tl_connect.dev.modules.academic_result.projection.SemesterSummaryView;
import com.tl_connect.dev.modules.academic_result.projection.SubjectResultRow;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AcademicResultService {
        private final AcademicResultRepository resultRepository;

        public AcademicResultDTO getSubjectResult(Long studentId, String trainingProgram) {
                List<SubjectResultRow> subjectResultsRows = resultRepository.findSubjectResult(studentId);
                List<SemesterSummaryView> semesterSummaries = resultRepository.findSemesterSummary(studentId);

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
                                                .score10(s.getScore10())
                                                .score4(s.getScore4())
                                                .letterGrade(s.getLetterGrade())
                                                .isPass(s.getIsPass())
                                                .build();
                        }).collect(Collectors.toList());
                        SemesterSummaryDTO semesterSummaryDTO = SemesterSummaryDTO.builder()
                                        .creditsRegistered(semesterSummary.getCreditsRegistered())
                                        .creditsPassed(semesterSummary.getCreditsPassed())
                                        .semesterGpa(semesterSummary.getSemesterGpa())
                                        .conductScore(semesterSummary.getConductScore())
                                        .cumulativeGpa(semesterSummary.getCumulativeGpa())
                                        .build();
                        return SemesterResultDTO.builder()
                                        .semester(semester)
                                        .subjectResults(subjectResultDTOs)
                                        .semesterSummary(semesterSummaryDTO)
                                        .build();
                }).collect(Collectors.toList());

                return AcademicResultDTO.builder()
                                .trainingProgram(trainingProgram)
                                .semesterResults(semesterResults)
                                .build();
        }
}
