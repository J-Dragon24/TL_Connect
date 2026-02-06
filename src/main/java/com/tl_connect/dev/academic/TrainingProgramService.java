package com.tl_connect.dev.academic;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.academic.dto.SemesterSubjectsDTO;
import com.tl_connect.dev.academic.dto.TrainingProgramSubjectDTO;
import com.tl_connect.dev.academic.projection.TrainingProgramHeaderView;
import com.tl_connect.dev.academic.projection.TrainingProgramSubjectView;
import com.tl_connect.dev.common.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainingProgramService {

    private final TrainingProgramRepository trainingProgramRepository;

    public List<SemesterSubjectsDTO> getTrainingProgramSubjects(Long programId) {
        List<TrainingProgramSubjectView> subjects = trainingProgramRepository.findSubjectsByProgramId(programId);

        // Group by semester
        Map<Long, List<TrainingProgramSubjectView>> groupedBySemester = subjects.stream()
                .collect(Collectors.groupingBy(TrainingProgramSubjectView::getSemesterId));

        return groupedBySemester.entrySet().stream()
                .map(entry -> {
                    Long semesterId = entry.getKey();
                    List<TrainingProgramSubjectView> semesterSubjects = entry.getValue();
                    String semesterName = semesterSubjects.get(0).getSemesterName();

                    List<TrainingProgramSubjectDTO> subjectDTOs = semesterSubjects.stream()
                            .map(s -> TrainingProgramSubjectDTO.builder()
                                    .subjectCode(s.getSubjectCode())
                                    .subjectName(s.getSubjectName())
                                    .credits(s.getCredits())
                                    .isRequired(s.getIsRequired())
                                    .electiveGroup(s.getElectiveGroup())
                                    .lectureHours(s.getLectureHours())
                                    .practiceHours(s.getPracticeHours())
                                    .build())
                            .collect(Collectors.toList());

                    return SemesterSubjectsDTO.builder()
                            .semesterId(semesterId)
                            .semesterName(semesterName)
                            .subjects(subjectDTOs)
                            .build();
                })
                .sorted((s1, s2) -> s1.getSemesterId().compareTo(s2.getSemesterId()))
                .collect(Collectors.toList());
    }

    public List<SemesterSubjectsDTO> getTrainingProgramSubjectsByStudentId(Long studentId) {
        TrainingProgramHeaderView header = trainingProgramRepository.findTrainingProgramHeaderByStudentId(studentId)
                .orElseThrow(
                        () -> new NotFoundException("Training program not found for student with id: " + studentId));
        return getTrainingProgramSubjects(header.getId());
    }
}
