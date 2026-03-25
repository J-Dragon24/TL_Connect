package com.tl_connect.dev.modules.subject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.exception.BadRequestException;
import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.subject.dto.CreateSubjectDTO;
import com.tl_connect.dev.modules.subject.dto.EnrollmentConditionDTO;
import com.tl_connect.dev.modules.subject.dto.PreGroupUpdateDTO;
import com.tl_connect.dev.modules.subject.dto.UpdateSubjectDTO;
import com.tl_connect.dev.modules.subject.dto.StudyDTOInterface;
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
import com.tl_connect.dev.modules.subject.projection.SubjectPrerequisiteGroupItemRow;
import com.tl_connect.dev.modules.subject.repository.SubjectEnrollmentConditionRepository;
import com.tl_connect.dev.modules.faculty.FacultyRepository;
import com.tl_connect.dev.modules.department.DepartmentRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SubjectPreGroupRepository subjectPreGroupRepository;
    private final SubjectPreGroupItemRepository subjectPreGroupItemRepository;
    private final SubjectEnrollmentConditionRepository subjectEnrollmentConditionRepository;
    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;
    private final Validator validator;

    public PagedResponse<Subject> getAllSubjects(Pageable pageable) {
        Page<Subject> subjects = subjectRepository.findAll(pageable);
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
        Set<ConstraintViolation<CreateSubjectDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }
        if (subjectRepository.existsBySubjectCode(dto.getSubjectCode())) {
            throw new InvalidInputException("Subject code already exists");
        }

        Subject subject = new Subject();
        mapToEntity(dto, subject);

        try {
            subjectRepository.save(subject);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Failed to create subject: " + e.getMessage());
        }
        return subject.getId();
    }

    @Transactional
    public void update(Long id, UpdateSubjectDTO dto) {
        Set<ConstraintViolation<UpdateSubjectDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }
        
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Subject not found"));

        if (dto.getSubjectCode() != null && !subject.getSubjectCode().equals(dto.getSubjectCode()) && subjectRepository.existsBySubjectCode(dto.getSubjectCode())) {
            throw new InvalidInputException("Subject code already exists");
        }

        mapToEntity(dto, subject);

        try {
            subjectRepository.save(subject);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Failed to update subject");
        }

        if(dto.getPrerequisiteGroups() != null){
            subjectPreGroupRepository.deleteBySubjectId(id);

            for (PreGroupUpdateDTO prerequisiteGroup : dto.getPrerequisiteGroups()) {

                SubjectPrerequisiteGroup subjectPrerequisiteGroup = new SubjectPrerequisiteGroup();
                subjectPrerequisiteGroup.setSubjectId(id);

                if(prerequisiteGroup.getMinSubjectsRequired() != null){
                    subjectPrerequisiteGroup.setMinSubjectsRequired(prerequisiteGroup.getMinSubjectsRequired());
                }
                if(prerequisiteGroup.getDescription() != null){
                    subjectPrerequisiteGroup.setDescription(prerequisiteGroup.getDescription());
                }
                subjectPreGroupRepository.saveAndFlush(subjectPrerequisiteGroup);

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

                        SubjectPrerequisiteGroupItem subjectPrerequisiteGroupItem = SubjectPrerequisiteGroupItem.builder()
                                .id(new SubjectPrerequisiteGroupItem.SubjectPrerequisiteGroupItemId(subjectPrerequisiteGroup.getId(), subjectId))
                                .build();
                        prerequisiteGroupItems.add(subjectPrerequisiteGroupItem);
                    }
                    try {
                        subjectPreGroupItemRepository.saveAll(prerequisiteGroupItems);
                    } catch (DataIntegrityViolationException e) {
                        throw new BadRequestException("Failed to update subject: " + e.getMessage());
                    }
                }
            }
        }

        if(dto.getEnrollmentConditions() != null){
            subjectEnrollmentConditionRepository.deleteBySubjectId(id);
            for (EnrollmentConditionDTO enrollmentCondition : dto.getEnrollmentConditions()) {
                SubjectEnrollmentCondition subjectEnrollmentCondition = new SubjectEnrollmentCondition();

                subjectEnrollmentCondition.setSubjectId(id);

                if(enrollmentCondition.getConditionType() != null){
                    subjectEnrollmentCondition.setConditionType(enrollmentCondition.getConditionType());
                }

                if(enrollmentCondition.getConditionValue() != null){
                    subjectEnrollmentCondition.setConditionValue(enrollmentCondition.getConditionValue());
                }

                if(enrollmentCondition.getConditionOperator() != null){
                    subjectEnrollmentCondition.setConditionOperator(enrollmentCondition.getConditionOperator());
                }

                if(enrollmentCondition.getDescription() != null){
                    subjectEnrollmentCondition.setDescription(enrollmentCondition.getDescription());
                }

                try {
                    subjectEnrollmentConditionRepository.save(subjectEnrollmentCondition);
                } catch (DataIntegrityViolationException e) {
                    throw new BadRequestException("Failed to update subject: " + e.getMessage());
                }
            }
        }
    }

    private void mapToEntity(StudyDTOInterface dto, Subject subject) {
        if(dto.getSubjectCode() != null){
            subject.setSubjectCode(dto.getSubjectCode());
        }

        subject.setSubjectName(dto.getSubjectName());
        
        if(dto.getCredits() != null){
            subject.setCredits(dto.getCredits());
        }
        if(dto.getCoefficient() != null){
            subject.setCoefficient(dto.getCoefficient());
        }
        if(dto.getLectureHours() != null){
            subject.setLectureHours(dto.getLectureHours());
        }
        if(dto.getPracticeHours() != null){
            subject.setPracticeHours(dto.getPracticeHours());
        }
        if(dto.getFacultyId() != null && facultyRepository.existsById(dto.getFacultyId())){
            subject.setFacultyId(dto.getFacultyId());
        }
        if(dto.getDepartmentId() != null && departmentRepository.existsById(dto.getDepartmentId())){
            subject.setDepartmentId(dto.getDepartmentId());
        }
    }

    @Transactional
    public void delete(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Subject not found"));
        if(!subject.getIsActive()){
            throw new BadRequestException("Subject already deleted");
        }
        subject.setIsActive(false);
        try {
            subjectRepository.save(subject);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Failed to delete subject: " + e.getMessage());
        }
    }
    
}
