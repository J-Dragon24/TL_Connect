package com.tl_connect.dev.modules.course_class.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.course_class.CourseClass;
import com.tl_connect.dev.modules.course_class.CourseClassRepository;
import com.tl_connect.dev.modules.course_class.dto.CourseClassBasicInfoDTO;
import com.tl_connect.dev.modules.course_class.dto.CourseClassDTO;
import com.tl_connect.dev.modules.course_class.dto.CreateCourseClassDTO;
import com.tl_connect.dev.modules.course_class.dto.UpdateCourseClassDTO;
import com.tl_connect.dev.modules.course_class.projection.CourseClassBasicInfoRow;
import com.tl_connect.dev.modules.course_class.projection.CourseClassRow;
import com.tl_connect.dev.modules.course_class.service.interfaces.CourseClassService;
import com.tl_connect.dev.modules.enroll.entity.StudentCourseClass;
import com.tl_connect.dev.modules.enroll.projection.CourseClassForEnrollRow;
import com.tl_connect.dev.modules.enroll.projection.DetailsForCheckEnrollRow;
import com.tl_connect.dev.modules.enroll.service.interfaces.StudentCourseClassService;
import com.tl_connect.dev.modules.lecturer.entity.Lecturer;
import com.tl_connect.dev.modules.lecturer.service.interfaces.LecturerService;
import com.tl_connect.dev.modules.semester.Semester;
import com.tl_connect.dev.modules.semester.dto.SemesterDTO;
import com.tl_connect.dev.modules.semester.service.interfaces.SemesterService;
import com.tl_connect.dev.modules.subject.entity.Subject;
import com.tl_connect.dev.modules.subject.service.interfaces.SubjectService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ConflictException;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.shared.common.ultility.CacheHelper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseClassServiceImpl implements CourseClassService {
    private final CourseClassRepository courseClassRepository;
    private final SubjectService subjectService;
    private final SemesterService semesterService;
    private final LecturerService lecturerService;
    private final StudentCourseClassService studentCourseClassService;
    private final CacheHelper cacheHelper;

    public PagedResponse<CourseClassBasicInfoDTO> getAll(Pageable pageable, String facultyCode, String semesterCode) {
        if (facultyCode == null || facultyCode.isBlank()) {
            facultyCode = null;
        }
        if (semesterCode == null || semesterCode.isBlank()) {
            semesterCode = null;
        }
        Page<CourseClassBasicInfoRow> page = courseClassRepository.findAllCourseClass(pageable, facultyCode,
                semesterCode);
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

        Subject subject = subjectService.findById(dto.getSubjectId());

        Semester semester = semesterService.findByIdAndIsActive(dto.getSemesterId());

        Lecturer lecturer = null;
        if (dto.getLecturerId() != null) {
            lecturer = lecturerService.findById(dto.getLecturerId());
        }

        CourseClass entity = CourseClass.create(lecturer != null ? lecturer.getId() : null, subject.getId(),
                semester.getId(), dto.getClassCode(), dto.getClassName(), dto.getCapacity());

        try {
            courseClassRepository.save(entity);
            return entity.getId();
        } catch (DataIntegrityViolationException ex) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR, "Failed to create course class");
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
            subjectService.findById(dto.getSubjectId());
        }

        if (dto.getSemesterId() != null) {
            semesterService.findByIdAndIsActive(dto.getSemesterId());
        }

        if (dto.getLecturerId() != null) {
            lecturerService.findById(dto.getLecturerId());
        }

        entity.update(dto.getLecturerId(), dto.getSubjectId(), dto.getSemesterId(), dto.getClassCode(),
                dto.getClassName(), dto.getCapacity());

        try {
            courseClassRepository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR, "Failed to update course class");
        }
    }

    @Transactional
    public void delete(Long id) {
        CourseClass entity = courseClassRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course class not found"));

        if (!entity.getIsActive()) {
            throw new ConflictException("Course class is already inactive");
        }
        entity.deactivate();
        try {
            courseClassRepository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR, "Failed to delete course class");
        }

        List<StudentCourseClass> studentCourseClasses = studentCourseClassService.findByCourseClassId(id);

        studentCourseClasses.forEach(scc -> {
            cacheHelper.evictAfterCommit(() -> cacheHelper.evict("schedule:student:" + scc.getStudentId() + ":" + entity.getSemesterId()));
        });
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

    @Override
    public List<CourseClassForEnrollRow> findCourseClassForEnrollment(Long subjectId, Long semesterId) {
        return courseClassRepository.findCourseClassForEnrollment(subjectId, semesterId);
    }

    @Override
    public CourseClass findById(Long id) {
        return courseClassRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course class not found"));
    }

    @Override
    public List<DetailsForCheckEnrollRow> findDetailForEnrollmentById(Long courseClassId) {
        return courseClassRepository.findDetailForEnrollmentById(courseClassId);
    }

    @Override
    public List<Long> findIdsByStudentIdAndSemesterId(Long studentId, LocalDate now) {
        return courseClassRepository.findIdsByStudentIdAndSemesterId(studentId, now);
    }
}
