package com.tl_connect.dev.modules.course_class;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.course_class.dto.CourseClassBasicInfoDTO;
import com.tl_connect.dev.modules.course_class.dto.CourseClassDTO;
import com.tl_connect.dev.modules.course_class.dto.CreateCourseClassDTO;
import com.tl_connect.dev.modules.course_class.dto.UpdateCourseClassDTO;
import com.tl_connect.dev.modules.course_class.projection.CourseClassBasicInfoRow;
import com.tl_connect.dev.modules.course_class.projection.CourseClassRow;
import com.tl_connect.dev.modules.lecturer.entity.Lecturer;
import com.tl_connect.dev.modules.lecturer.repository.LecturerRepository;
import com.tl_connect.dev.modules.semester.Semester;
import com.tl_connect.dev.modules.semester.SemesterRepository;
import com.tl_connect.dev.modules.semester.dto.SemesterDTO;
import com.tl_connect.dev.modules.subject.entity.Subject;
import com.tl_connect.dev.modules.subject.repository.SubjectRepository;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.exception.BadRequestException;
import com.tl_connect.dev.shared.common.exception.ConflictException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseClassService {
    private final CourseClassRepository courseClassRepository;
    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;
    private final LecturerRepository lecturerRepository;

    public PagedResponse<CourseClassBasicInfoDTO> getAll(Pageable pageable, String facultyCode, String semesterCode) {
        if (facultyCode == null || facultyCode.isBlank()) {
            facultyCode = null;
        }
        if (semesterCode == null || semesterCode.isBlank()) {
            semesterCode = null;
        }
        Page<CourseClassBasicInfoRow> page = courseClassRepository.findAllCourseClass(pageable, facultyCode, semesterCode);
        return new PagedResponse<>(
                page.getContent().stream().map(this::toDTO).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }

    public CourseClassDTO getDetailById(Long id) {
        CourseClassRow courseClass = courseClassRepository.findDetailById(id)
                .orElseThrow(() -> new NotFoundException("Course class not found"));
        return CourseClassDTO.builder()
                .id(courseClass.getId())
                .lecturerCode(courseClass.getLecturerCode())
                .lecturerName(courseClass.getLecturerName())
                .subjectCode(courseClass.getSubjectCode())
                .subjectName(courseClass.getSubjectName())
                .semester(SemesterDTO.builder()
                        .semesterCode(courseClass.getSemesterCode())
                        .semesterName(courseClass.getSemesterName())
                        .academicYears(courseClass.getAcademicYears())
                        .semesterNumber(courseClass.getSemesterNumber())
                        .startDate(courseClass.getStartDate())
                        .endDate(courseClass.getEndDate())
                        .build())
                .classCode(courseClass.getClassCode())
                .className(courseClass.getClassName())
                .capacity(courseClass.getCapacity())
                .enrolledCount(courseClass.getEnrolledCount())
                .isActive(courseClass.getIsActive())
                .build();
    }

    @Transactional
    public Long create(CreateCourseClassDTO dto) {

        if (courseClassRepository.existsByClassCode(dto.getClassCode())) {
            throw new ConflictException("Class code already exists");
        }

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new NotFoundException("Subject not found"));

        Semester semester = semesterRepository.findById(dto.getSemesterId())
                .orElseThrow(() -> new NotFoundException("Semester not found"));

        Lecturer lecturer = null;
        if (dto.getLecturerId() != null) {
            lecturer = lecturerRepository.findById(dto.getLecturerId())
                    .orElseThrow(() -> new NotFoundException("Lecturer not found"));
        }

        CourseClass entity = CourseClass.create(lecturer != null ? lecturer.getId() : null, subject.getId(), semester.getId(), dto.getClassCode(), dto.getClassName(), dto.getCapacity());

        try {
            courseClassRepository.save(entity);
            return entity.getId();
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Failed to create course class");
        }
    }

    @Transactional
    public void update(Long id, UpdateCourseClassDTO dto) {

        CourseClass entity = courseClassRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course class not found"));

        // check duplicate code
        if (dto.getClassCode() != null
                && !entity.getClassCode().equals(dto.getClassCode())
                && courseClassRepository.existsByClassCode(dto.getClassCode())) {
            throw new ConflictException("Class code already exists");
        }

        if (dto.getSubjectId() != null) {
            subjectRepository.findById(dto.getSubjectId())
                    .orElseThrow(() -> new NotFoundException("Subject not found"));
        }

        if (dto.getSemesterId() != null) {
            semesterRepository.findById(dto.getSemesterId())
                    .orElseThrow(() -> new NotFoundException("Semester not found"));
        }

        if (dto.getLecturerId() != null) {
            lecturerRepository.findById(dto.getLecturerId())
                    .orElseThrow(() -> new NotFoundException("Lecturer not found"));
        }

        entity.update(dto.getLecturerId(), dto.getSubjectId(), dto.getSemesterId(), dto.getClassCode(), dto.getClassName(), dto.getCapacity());

        try {
            courseClassRepository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Failed to update course class");
        }
    }

    @Transactional
    public void delete(Long id) {
        CourseClass entity = courseClassRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course class not found"));

        if(!entity.getIsActive()) {
            throw new BadRequestException("Course class is already inactive");
        }
        entity.deactivate();
        try {
            courseClassRepository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Failed to delete course class");
        }
    }
 

    private CourseClassBasicInfoDTO toDTO(CourseClassBasicInfoRow courseClass) {
        return CourseClassBasicInfoDTO.builder()
                .id(courseClass.getId())
                .lecturerCode(courseClass.getLecturerCode())
                .subjectCode(courseClass.getSubjectCode())
                .semesterCode(courseClass.getSemesterCode())
                .classCode(courseClass.getClassCode())
                .className(courseClass.getClassName())
                .capacity(courseClass.getCapacity())
                .enrolledCount(courseClass.getEnrolledCount())
                .isActive(courseClass.getIsActive())
                .build();
    }
}
