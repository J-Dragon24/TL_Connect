package com.tl_connect.dev.modules.study_program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.study_program.projection.StudyProgramRow;
import com.tl_connect.dev.modules.study_program.dto.StudyProgramListItemDTO;
import com.tl_connect.dev.modules.study_program.dto.MajorDTO;
import com.tl_connect.dev.modules.study_program.dto.SemesterSubjectsDTO;
import com.tl_connect.dev.modules.study_program.dto.SubjectDTO;
import com.tl_connect.dev.modules.study_program.dto.SubjectPrerequisiteDTO;
import com.tl_connect.dev.modules.study_program.dto.StudyProgramDTO;
import com.tl_connect.dev.modules.study_program.projection.SubjectPrerequisiteRow;
import com.tl_connect.dev.modules.study_program.projection.StudyProgramHeaderView;
import com.tl_connect.dev.modules.study_program.projection.StudyProgramSubjectRow;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyProgramService {

        private final StudyProgramRepository studyProgramRepository;

        public List<StudyProgramListItemDTO> getAllStudyProgram(Long studentId) {
                List<StudyProgramRow> studyPrograms = studyProgramRepository.findAllStudyProgram(studentId);
                return studyPrograms.stream().map(
                                studyProgram -> StudyProgramListItemDTO.builder()
                                                .studentCode(studyProgram.getStudentCode())
                                                .studyProgramCode(studyProgram.getStudyProgramCode())
                                                .studyProgramName(studyProgram.getStudyProgramName())
                                                .isPrimary(studyProgram.getIsPrimary())
                                                .startYear(studyProgram.getStartYear())
                                                .build())
                                .collect(Collectors.toList());
        }

        public StudyProgramDTO getStudyProgram(String studyProgramCode) {
                StudyProgramHeaderView header = studyProgramRepository.findStudyProgramHeader(studyProgramCode)
                                .orElseThrow(() -> new NotFoundException("Study program not found for study program with code: "+ studyProgramCode));

                List<StudyProgramSubjectRow> studyProgramSubjects = studyProgramRepository
                                .findSubjectsByProgramId(header.getId());

                List<SubjectPrerequisiteRow> subjectPrerequisitesRows = studyProgramRepository
                                .findSubjectPrerequisitesByProgramId(header.getId());

                return mapStudyProgram(header, studyProgramSubjects, subjectPrerequisitesRows);
        }

        private StudyProgramDTO mapStudyProgram(StudyProgramHeaderView header,
                        List<StudyProgramSubjectRow> studyProgramSubjects,
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

                Map<Long, List<StudyProgramSubjectRow>> groupedBySemester = studyProgramSubjects.stream()
                                .collect(Collectors.groupingBy(StudyProgramSubjectRow::getSemesterId));

                List<SemesterSubjectsDTO> semesterSubjects = groupedBySemester.entrySet().stream()
                                .map(entry -> {
                                        List<StudyProgramSubjectRow> rows = entry.getValue();
                                        StudyProgramSubjectRow first = rows.get(0);

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

                StudyProgramDTO studyProgram = StudyProgramDTO.builder()
                                .studyProgramName(header.getStudyProgramName())
                                .yearStart(header.getStartYear())
                                .totalCredits(header.getTotalCredits())
                                .major(major)
                                .semesters(semesterSubjects)
                                .build();
                return studyProgram;
        }
}
