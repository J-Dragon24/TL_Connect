package com.tl_connect.dev.modules.training_program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.training_program.dto.MajorDTO;
import com.tl_connect.dev.modules.training_program.dto.SemesterSubjectsDTO;
import com.tl_connect.dev.modules.training_program.dto.SubjectDTO;
import com.tl_connect.dev.modules.training_program.dto.SubjectPrerequisiteDTO;
import com.tl_connect.dev.modules.training_program.dto.TrainingProgramDTO;
import com.tl_connect.dev.modules.training_program.projection.SubjectPrerequisiteRow;
import com.tl_connect.dev.modules.training_program.projection.TrainingProgramHeaderView;
import com.tl_connect.dev.modules.training_program.projection.TrainingProgramSubjectRow;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainingProgramService {

        private final TrainingProgramRepository trainingProgramRepository;

        public TrainingProgramDTO getTrainingProgram(Long studentId) {
                TrainingProgramHeaderView header = trainingProgramRepository
                                .findTrainingProgramHeaderByStudentId(studentId)
                                .orElseThrow(() -> new NotFoundException(
                                                "Training program not found for student with id: " + studentId));

                List<TrainingProgramSubjectRow> trainingProgramSubjects = trainingProgramRepository
                                .findSubjectsByProgramId(header.getId());

                List<SubjectPrerequisiteRow> subjectPrerequisitesRows = trainingProgramRepository
                                .findSubjectPrerequisitesByProgramId(header.getId());

                return mapTrainingProgram(header, trainingProgramSubjects, subjectPrerequisitesRows);
        }

        private TrainingProgramDTO mapTrainingProgram(TrainingProgramHeaderView header,
                        List<TrainingProgramSubjectRow> trainingProgramSubjects,
                        List<SubjectPrerequisiteRow> subjectPrerequisitesRows) {
                MajorDTO major = MajorDTO.builder()
                                .majorCode(header.getMajorCode())
                                .majorName(header.getMajorName())
                                .faculty(header.getFaculty())
                                .build();

                Map<Long, List<SubjectPrerequisiteDTO>> map = new HashMap<>();
                for (SubjectPrerequisiteRow row : subjectPrerequisitesRows) {
                        map.computeIfAbsent(row.getSubjectId(), k -> new ArrayList<>())
                                        .add(SubjectPrerequisiteDTO.builder()
                                                        .subjectCode(row.getPrerequisiteSubjectCode())
                                                        .subjectName(row.getPrerequisiteSubjectName())
                                                        .build());
                }

                Map<Long, List<TrainingProgramSubjectRow>> groupedBySemester = trainingProgramSubjects.stream()
                                .collect(Collectors.groupingBy(TrainingProgramSubjectRow::getSemesterId));

                List<SemesterSubjectsDTO> semesterSubjects = groupedBySemester.entrySet().stream()
                                .map(entry -> {
                                        List<TrainingProgramSubjectRow> rows = entry.getValue();
                                        TrainingProgramSubjectRow first = rows.get(0);

                                        List<SubjectDTO> subjects = rows.stream()
                                                        .map(s -> {
                                                                SubjectDTO.SubjectDTOBuilder builder = SubjectDTO
                                                                                .builder()
                                                                                .subjectCode(s.getSubjectCode())
                                                                                .subjectName(s.getSubjectName())
                                                                                .credits(s.getCredits())
                                                                                .isRequired(s.getIsRequired())
                                                                                .electiveGroup(s.getElectiveGroup())
                                                                                .lectureHours(s.getLectureHours())
                                                                                .practiceHours(s.getPracticeHours())
                                                                                .faculty(s.getFaculty())
                                                                                .department(s.getDepartment());

                                                                List<SubjectPrerequisiteDTO> prerequisites = map
                                                                                .get(s.getSubjectId());
                                                                builder.subjectPrerequisite(prerequisites);

                                                                return builder.build();
                                                        })
                                                        .collect(Collectors.toList());

                                        return SemesterSubjectsDTO.builder()
                                                        .semesterName(first.getSemesterName())
                                                        .semesterStartDate(first.getSemesterStartDate())
                                                        .semesterEndDate(first.getSemesterEndDate())
                                                        .subjects(subjects)
                                                        .build();
                                })
                                .sorted((s1, s2) -> s1.getSemesterStartDate().compareTo(s2.getSemesterStartDate()))
                                .collect(Collectors.toList());

                TrainingProgramDTO trainingProgram = TrainingProgramDTO.builder()
                                .trainingProgramName(header.getTrainingProgramName())
                                .yearStart(header.getYearStart())
                                .totalCredits(header.getTotalCredits())
                                .major(major)
                                .semesters(semesterSubjects)
                                .build();
                return trainingProgram;
        }
}
