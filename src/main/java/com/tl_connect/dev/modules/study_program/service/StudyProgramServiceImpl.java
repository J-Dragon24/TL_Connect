package com.tl_connect.dev.modules.study_program.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.study_program.projection.StudyProgramRow;
import com.tl_connect.dev.modules.study_program.dto.StudyProgramListItemDTO;
import com.tl_connect.dev.modules.study_program.dto.StudyProgramSubjectDTO;
import com.tl_connect.dev.modules.study_program.entity.StudyProgram;
import com.tl_connect.dev.modules.enroll.projection.SubjectForEnrollRow;
import com.tl_connect.dev.modules.study_program.dto.MajorDTO;
import com.tl_connect.dev.modules.study_program.dto.SemesterSubjectsDTO;
import com.tl_connect.dev.modules.study_program.dto.StudyProgramAdmDTO;
import com.tl_connect.dev.modules.study_program.dto.StudyProgramDTO;
import com.tl_connect.dev.modules.subject.dto.SubjectPrerequisiteGroupDTO;
import com.tl_connect.dev.modules.subject.dto.SubjectPrerequisiteGroupItemDTO;
import com.tl_connect.dev.modules.subject.entity.SubjectPrerequisiteGroup;
import com.tl_connect.dev.modules.subject.projection.SubjectPrerequisiteGroupItemRow;
import com.tl_connect.dev.modules.subject.repository.SubjectPreGroupItemRepository;
import com.tl_connect.dev.modules.subject.repository.SubjectPreGroupRepository;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.enums.TrainingType;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.modules.study_program.projection.StudyProgramHeaderView;
import com.tl_connect.dev.modules.study_program.projection.StudyProgramSubjectRow;
import com.tl_connect.dev.modules.study_program.repository.StudyProgramRepository;
import com.tl_connect.dev.modules.study_program.projection.StudyProgramAdmRow;
import com.tl_connect.dev.modules.study_program.service.interfaces.StudyProgramService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyProgramServiceImpl implements StudyProgramService {

        private final StudyProgramRepository studyProgramRepository;
        private final SubjectPreGroupRepository subjectPreGroupRepository;
        private final SubjectPreGroupItemRepository subjectPreGroupItemRepository;

        public PagedResponse<StudyProgramAdmDTO> getAllStudyProgram(Pageable pageable, Integer startYear, String facultyCode) {
                if(facultyCode == null || facultyCode.isBlank()) {
                        facultyCode = null;
                }
                Page<StudyProgramAdmRow> page = studyProgramRepository.findByStartYearAndFacultyCode(startYear, facultyCode, pageable);
                return new PagedResponse<>(
                        page.getContent().stream().map(this::toDTO).toList(),
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages(),
                        page.isFirst(),
                        page.isLast());
        }

        public StudyProgramDTO getDetailedStudyProgram(Long studyProgramId) {
                StudyProgramHeaderView header = studyProgramRepository.findStudyProgramHeaderById(studyProgramId)
                                .orElseThrow(() -> new NotFoundException("Study program not found"));

                List<StudyProgramSubjectRow> studyProgramSubjects = studyProgramRepository
                                .findSubjectsByProgramId(header.getId());

                List<Long> subjectIds = studyProgramSubjects.stream()
                                .map(StudyProgramSubjectRow::getSubjectId)
                                .collect(Collectors.toList());
                
                List<SubjectPrerequisiteGroup> subjectPrerequisiteGroups = subjectPreGroupRepository.findBySubjectIdIn(subjectIds);

                List<Long> groupIds = subjectPrerequisiteGroups.stream()
                                .map(SubjectPrerequisiteGroup::getId)
                                .collect(Collectors.toList());

                List<SubjectPrerequisiteGroupItemRow> subjectPrerequisiteGroupItems = subjectPreGroupItemRepository.findByGroupIdIn(groupIds);

                return mapStudyProgram(header, studyProgramSubjects, subjectPrerequisiteGroups, subjectPrerequisiteGroupItems);
        }

        public List<StudyProgramListItemDTO> getBasicInfoStudyProgram(Long studentId) {
                List<StudyProgramRow> studyPrograms = studyProgramRepository.findAllStudyProgram(studentId);
                return studyPrograms.stream().map(
                                studyProgram -> StudyProgramListItemDTO.builder()
                                                .studentCode(studyProgram.getStudentCode())
                                                .studyProgramCode(studyProgram.getStudyProgramCode())
                                                .studyProgramName(studyProgram.getStudyProgramName())
                                                .totalCredits(studyProgram.getTotalCredits())
                                                .isPrimary(studyProgram.getIsPrimary())
                                                .startYear(studyProgram.getStartYear())
                                                .build())
                                .collect(Collectors.toList());
        }

        public StudyProgramDTO getStudyProgram(String studyProgramCode, Long studentId) {
                StudyProgramHeaderView header = studyProgramRepository.findStudyProgramHeaderByStudentId(studyProgramCode, studentId)
                                .orElseThrow(() -> new NotFoundException("Study program not found"));

                List<StudyProgramSubjectRow> studyProgramSubjects = studyProgramRepository
                                .findSubjectsByProgramId(header.getId());

                List<Long> subjectIds = studyProgramSubjects.stream()
                                .map(StudyProgramSubjectRow::getSubjectId)
                                .collect(Collectors.toList());
                
                List<SubjectPrerequisiteGroup> subjectPrerequisiteGroups = subjectIds.isEmpty() ? List.of() : subjectPreGroupRepository.findBySubjectIdIn(subjectIds);

                List<Long> groupIds = subjectPrerequisiteGroups.stream()
                                .map(SubjectPrerequisiteGroup::getId)
                                .collect(Collectors.toList());

                List<SubjectPrerequisiteGroupItemRow> subjectPrerequisiteGroupItems = groupIds.isEmpty() ? List.of() : subjectPreGroupItemRepository.findByGroupIdIn(groupIds);

                return mapStudyProgram(header, studyProgramSubjects, subjectPrerequisiteGroups, subjectPrerequisiteGroupItems);
        }


        public StudyProgramHeaderView findByStudyProgramCodeAndStudentId(String studyProgramCode, Long studentId){
                return studyProgramRepository
                                .findByStudyProgramCodeAndStudentId(studyProgramCode, studentId)
                                .orElseThrow(() -> new NotFoundException("Study program not found"));
        }

        public List<SubjectForEnrollRow> findSubjectsByStudyProgramId(Long studyProgramId) {
                return studyProgramRepository.findSubjectsByStudyProgramId(studyProgramId);
        }

        public StudyProgram findById(Long id) {
                return studyProgramRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Study program not found"));
        }

        public StudyProgram findByMajorIdAndTrainingTypeAndStartYear(Long majorId, TrainingType trainingType, Integer startYear){
                return studyProgramRepository.findByMajorIdAndTrainingTypeAndStartYear(majorId, trainingType, startYear)
                        .orElseThrow(() -> new NotFoundException("Study program not found"));
        }

        private StudyProgramDTO mapStudyProgram(StudyProgramHeaderView header,
                        List<StudyProgramSubjectRow> studyProgramSubjects,
                        List<SubjectPrerequisiteGroup> subjectPrerequisiteGroups,
                        List<SubjectPrerequisiteGroupItemRow> subjectPrerequisiteGroupItems) {

                MajorDTO major = MajorDTO.builder()
                                .majorCode(header.getMajorCode())
                                .majorName(header.getMajorName())
                                .faculty(header.getFaculty())
                                .build();

                Map<Long, List<SubjectPrerequisiteGroupItemRow>> itemsByGroupId = subjectPrerequisiteGroupItems.stream()
                        .collect(Collectors.groupingBy(SubjectPrerequisiteGroupItemRow::getGroupId));

                Map<Long, List<SubjectPrerequisiteGroup>> groupsBySubjectId = subjectPrerequisiteGroups.stream()
                        .collect(Collectors.groupingBy(SubjectPrerequisiteGroup::getSubjectId));

                Map<Long, List<StudyProgramSubjectRow>> groupedBySemester = studyProgramSubjects.stream()
                        .collect(Collectors.groupingBy(StudyProgramSubjectRow::getSemesterId));


                List<SemesterSubjectsDTO> semesterSubjects = groupedBySemester.entrySet().stream()
                                .map(entry -> buildSemester(entry.getValue(), groupsBySubjectId, itemsByGroupId))
                                .sorted(Comparator.comparing(SemesterSubjectsDTO::getSemesterStartDate))
                                .toList();

                StudyProgramDTO studyProgram = StudyProgramDTO.builder()
                                .studyProgramName(header.getStudyProgramName())
                                .studyProgramCode(header.getStudyProgramCode())
                                .yearStart(header.getStartYear())
                                .totalCredits(header.getTotalCredits())
                                .major(major)
                                .semesters(semesterSubjects)
                                .build();
                return studyProgram;
        }


        private SemesterSubjectsDTO buildSemester(
                List<StudyProgramSubjectRow> rows,
                Map<Long, List<SubjectPrerequisiteGroup>> groupsBySubjectId,
                Map<Long, List<SubjectPrerequisiteGroupItemRow>> itemsByGroupId
        ) {
                StudyProgramSubjectRow first = rows.get(0);

                Map<Long, StudyProgramSubjectRow> uniqueSubjects = rows.stream()
                        .collect(Collectors.toMap(
                                StudyProgramSubjectRow::getSubjectId,
                                Function.identity(),
                                (a, b) -> a
                        ));

                List<StudyProgramSubjectDTO> subjects = uniqueSubjects.values().stream()
                        .map(s -> mapSubject(s, groupsBySubjectId, itemsByGroupId))
                        .toList();

                return SemesterSubjectsDTO.builder()
                        .semesterId(first.getSemesterId())
                        .semesterName(first.getSemesterName())
                        .semesterStartDate(first.getSemesterStartDate())
                        .semesterEndDate(first.getSemesterEndDate())
                        .subjects(subjects)
                        .build();
        }
        
        private StudyProgramSubjectDTO mapSubject(
                StudyProgramSubjectRow s,
                Map<Long, List<SubjectPrerequisiteGroup>> groupsBySubjectId,
                Map<Long, List<SubjectPrerequisiteGroupItemRow>> itemsByGroupId
        ) {
                List<SubjectPrerequisiteGroup> groups =
                        groupsBySubjectId.getOrDefault(s.getSubjectId(), List.of());

                List<SubjectPrerequisiteGroupDTO> prerequisites = groups.stream()
                        .map(group -> mapGroup(group, itemsByGroupId))
                        .toList();

                return StudyProgramSubjectDTO.builder()
                        .id(s.getId())
                        .subjectCode(s.getSubjectCode())
                        .subjectName(s.getSubjectName())
                        .credits(s.getCredits())
                        .isRequired(s.getIsRequired())
                        .electiveGroup(s.getElectiveGroup())
                        .lectureHours(s.getLectureHours())
                        .practiceHours(s.getPracticeHours())
                        .faculty(s.getFaculty())
                        .department(s.getDepartment())
                        .subjectPrerequisite(prerequisites)
                        .build();
        }

        private SubjectPrerequisiteGroupDTO mapGroup(
        SubjectPrerequisiteGroup group,
        Map<Long, List<SubjectPrerequisiteGroupItemRow>> itemsByGroupId
        ) {
                List<SubjectPrerequisiteGroupItemRow> items =
                        itemsByGroupId.getOrDefault(group.getId(), List.of());

                List<SubjectPrerequisiteGroupItemDTO> itemsDTO = items.stream()
                        .map(item -> SubjectPrerequisiteGroupItemDTO.builder()
                                .subjectCode(item.getSubjectCode())
                                .subjectName(item.getSubjectName())
                                .build())
                        .toList();

                return SubjectPrerequisiteGroupDTO.builder()
                        .minSubjectsRequired(group.getMinSubjectsRequired())
                        .description(group.getDescription())
                        .items(itemsDTO)
                        .build();
        }

        private StudyProgramAdmDTO toDTO(StudyProgramAdmRow row) {
                return StudyProgramAdmDTO.builder()
                                .id(row.getId())
                                .studyProgramCode(row.getStudyProgramCode())
                                .studyProgramName(row.getStudyProgramName())
                                .majorCode(row.getMajorCode())
                                .startYear(row.getStartYear())
                                .totalCredits(row.getTotalCredits())
                                .trainingType(row.getTrainingType())
                                .build();
        }
}
