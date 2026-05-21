package com.tl_connect.dev.modules.subject.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.subject.dto.CreateSubjectDTO;
import com.tl_connect.dev.modules.subject.dto.EnrollmentConditionDTO;
import com.tl_connect.dev.modules.subject.dto.PreGroupCreateDTO;
import com.tl_connect.dev.modules.subject.dto.EnrollmentConditionCreateDTO;
import com.tl_connect.dev.modules.subject.dto.UpdateSubjectDTO;
import com.tl_connect.dev.modules.subject.dto.SubjectDTO;
import com.tl_connect.dev.modules.subject.dto.SubjectPrerequisiteGroupDTO;
import com.tl_connect.dev.modules.subject.dto.SubjectPrerequisiteGroupItemDTO;
import com.tl_connect.dev.modules.subject.entity.Subject;
import com.tl_connect.dev.modules.subject.entity.SubjectEnrollmentCondition;
import com.tl_connect.dev.modules.subject.entity.SubjectPrerequisiteGroup;
import com.tl_connect.dev.modules.subject.entity.SubjectPrerequisiteGroupItem;
import com.tl_connect.dev.modules.subject.repository.SubjectPreGroupRepository;
import com.tl_connect.dev.modules.subject.repository.SubjectPreGroupItemRepository;
import com.tl_connect.dev.modules.subject.repository.SubjectRepository;
import com.tl_connect.dev.modules.subject.service.interfaces.SubjectService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ConflictException;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.modules.subject.projection.PrerequisiteRow;
import com.tl_connect.dev.modules.subject.projection.SubjectPrerequisiteGroupItemRow;
import com.tl_connect.dev.modules.subject.repository.SubjectEnrollmentConditionRepository;
import com.tl_connect.dev.modules.faculty.FacultyRepository;
import com.tl_connect.dev.modules.department.DepartmentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService{

    private final SubjectRepository subjectRepository;
    private final SubjectPreGroupRepository subjectPreGroupRepository;
    private final SubjectPreGroupItemRepository subjectPreGroupItemRepository;
    private final SubjectEnrollmentConditionRepository subjectEnrollmentConditionRepository;
    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;

    public PagedResponse<Subject> getAllSubjects(Pageable pageable) {
        Page<Subject> subjects = subjectRepository.findAllSubjects(pageable);
        return new PagedResponse<>(
                subjects.getContent(),
                subjects.getNumber(),
                subjects.getSize(),
                subjects.getTotalElements(),
                subjects.getTotalPages(),
                subjects.isFirst(),
                subjects.isLast());
    }

    public SubjectDTO getSubjectById(Long id) {

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Subject not found"));
        
        List<SubjectPrerequisiteGroup> prerequisiteGroups = subjectPreGroupRepository.findBySubjectId(id);

        List<Long> groupIds = prerequisiteGroups.stream().map(SubjectPrerequisiteGroup::getId).collect(Collectors.toList());

        List<SubjectPrerequisiteGroupItemRow> prerequisiteGroupItems = subjectPreGroupItemRepository.findByGroupIdIn(groupIds);

        Map<Long, List<SubjectPrerequisiteGroupItemRow>> itemsByGroupId = prerequisiteGroupItems.stream()
            .collect(Collectors.groupingBy(SubjectPrerequisiteGroupItemRow::getGroupId));

        List<SubjectPrerequisiteGroupDTO> prerequisiteGroupDTOs = prerequisiteGroups.stream().map(group -> {
            List<SubjectPrerequisiteGroupItemRow> items = itemsByGroupId.getOrDefault(group.getId(), List.of());

            List<SubjectPrerequisiteGroupItemDTO> itemsDTO = items.stream()
                .map(item -> SubjectPrerequisiteGroupItemDTO.builder()
                        .subjectCode(item.getSubjectCode())
                        .subjectName(item.getSubjectName())
                        .build())
                .toList();

            return SubjectPrerequisiteGroupDTO.builder()
                        .id(group.getId())
                        .minSubjectsRequired(group.getMinSubjectsRequired())
                        .description(group.getDescription())
                        .items(itemsDTO)
                        .build();
        
        }).collect(Collectors.toList());
        
        List<SubjectEnrollmentCondition> enrollmentConditions = subjectEnrollmentConditionRepository.findBySubjectId(id);

        List<EnrollmentConditionDTO> enrollmentConditionDTOs = enrollmentConditions.stream().map(condition -> {
            return EnrollmentConditionDTO.builder()
                    .id(condition.getId())
                    .conditionType(condition.getConditionType())
                    .conditionValue(condition.getConditionValue())
                    .conditionOperator(condition.getConditionOperator())
                    .description(condition.getDescription())
                    .build();
        }).collect(Collectors.toList());

        return SubjectDTO.builder()
                .id(subject.getId())
                .subjectCode(subject.getSubjectCode())
                .subjectName(subject.getSubjectName())
                .credits(subject.getCredits())
                .coefficient(subject.getCoefficient())
                .lectureHours(subject.getLectureHours())
                .practiceHours(subject.getPracticeHours())
                .facultyId(subject.getFacultyId())
                .departmentId(subject.getDepartmentId())
                .prerequisiteGroups(prerequisiteGroupDTOs)
                .enrollmentConditions(enrollmentConditionDTOs)
                .build();
    }

    @Transactional
    public Long create(CreateSubjectDTO dto) {
        if (subjectRepository.existsBySubjectCode(dto.getSubjectCode())) {
            throw new InvalidInputException("Subject code already exists");
        }

        if(dto.getFacultyId() != null && !facultyRepository.existsById(dto.getFacultyId())){
            throw new InvalidInputException("Faculty not found");
        }
        if(dto.getDepartmentId() != null && !departmentRepository.existsById(dto.getDepartmentId())){
            throw new InvalidInputException("Department not found");
        }

        Subject subject = Subject.create(dto.getFacultyId(), dto.getDepartmentId(), dto.getSubjectCode(), dto.getSubjectName(), dto.getCredits(), dto.getCoefficient(), dto.getLectureHours(), dto.getPracticeHours());

        try {
            subjectRepository.save(subject);
        } catch (DataIntegrityViolationException e) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to create subject");
        }
        return subject.getId();
    }

    @Transactional
    public void update(Long id, UpdateSubjectDTO dto) {
        
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Subject not found"));

        if (dto.getSubjectCode() != null && !subject.getSubjectCode().equals(dto.getSubjectCode()) && subjectRepository.existsBySubjectCode(dto.getSubjectCode())) {
            throw new InvalidInputException("Subject code already exists");
        }

        if(dto.getFacultyId() != null && !facultyRepository.existsById(dto.getFacultyId())){
            throw new InvalidInputException("Faculty not found");
        }
        if(dto.getDepartmentId() != null && !departmentRepository.existsById(dto.getDepartmentId())){
            throw new InvalidInputException("Department not found");
        }

        subject.update(dto.getFacultyId(), dto.getDepartmentId(), dto.getSubjectCode(), dto.getSubjectName(), dto.getCredits(), dto.getCoefficient(), dto.getLectureHours(), dto.getPracticeHours());

        try {
            subjectRepository.save(subject);
        } catch (DataIntegrityViolationException e) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to update subject");
        }

        if(dto.getPrerequisiteGroups() != null){
            subjectPreGroupRepository.deleteBySubjectId(id);
            subjectPreGroupRepository.flush();

            for (PreGroupCreateDTO prerequisiteGroup : dto.getPrerequisiteGroups()) {

                SubjectPrerequisiteGroup subjectPrerequisiteGroup = SubjectPrerequisiteGroup.create(id, prerequisiteGroup.getMinSubjectsRequired(), prerequisiteGroup.getDescription());
                subjectPreGroupRepository.save(subjectPrerequisiteGroup);

                List<Long> ids = prerequisiteGroup.getPrerequisiteSubjectIds();

                if(ids != null && !ids.isEmpty()){
                    long count = subjectRepository.countByIdIn(ids);

                    if (count != ids.size()) {
                        throw new NotFoundException("Some prerequisite subjects not found");
                    }

                    if(prerequisiteGroup.getMinSubjectsRequired() != null && prerequisiteGroup.getMinSubjectsRequired() > ids.size()){
                        throw new InvalidInputException("minSubjectsRequired invalid");
                    }

                    List<SubjectPrerequisiteGroupItem> prerequisiteGroupItems = new ArrayList<>();
                    for (Long subjectId : ids) {

                        if(subjectId.equals(id)){
                            throw new InvalidInputException("Subject cannot be prerequisite of itself");
                        }

                        SubjectPrerequisiteGroupItem subjectPrerequisiteGroupItem = SubjectPrerequisiteGroupItem.create(subjectPrerequisiteGroup.getId(), subjectId);
                        prerequisiteGroupItems.add(subjectPrerequisiteGroupItem);
                    }
                    try {
                        subjectPreGroupItemRepository.saveAll(prerequisiteGroupItems);
                    } catch (DataIntegrityViolationException e) {
                        throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to update subject");
                    }
                }
            }
        }

        Set<String> types = new HashSet<>();

        if(dto.getEnrollmentConditions() != null){
            subjectEnrollmentConditionRepository.deleteBySubjectId(id);
            subjectEnrollmentConditionRepository.flush();
            for (EnrollmentConditionCreateDTO enrollmentCondition : dto.getEnrollmentConditions()) {
                if (!types.add(enrollmentCondition.getConditionType().name())) {
                    throw new InvalidInputException("Duplicate condition type: " + enrollmentCondition.getConditionType());
                }
                SubjectEnrollmentCondition subjectEnrollmentCondition = SubjectEnrollmentCondition.create(id, enrollmentCondition.getConditionType(), enrollmentCondition.getConditionValue(), enrollmentCondition.getConditionOperator(), enrollmentCondition.getDescription());
                try {
                    subjectEnrollmentConditionRepository.save(subjectEnrollmentCondition);
                } catch (DataIntegrityViolationException e) {
                    throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to update subject");
                }
            }
        }
    }



    @Transactional
    public void delete(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Subject not found"));
        if(!subject.getIsActive()){
            throw new ConflictException("Subject already deleted");
        }
        subject.deactivate();
        try {
            subjectRepository.save(subject);
        } catch (DataIntegrityViolationException e) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to delete subject");
        }
    }

    public List<Subject> findBySubjectCodeIn(Set<String> subjectCodes) {
        return subjectRepository.findBySubjectCodeIn(subjectCodes);
    }
    
    public Subject findById(Long id){
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(()-> new NotFoundException("Subject not found"));
        return subject;
    }

    public List<PrerequisiteRow> findAllPrerequisiteRows(){
        return subjectRepository.findAllPrerequisiteRows();
    }
}
