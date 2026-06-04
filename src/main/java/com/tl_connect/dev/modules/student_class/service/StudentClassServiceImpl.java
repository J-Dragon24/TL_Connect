package com.tl_connect.dev.modules.student_class.service;

import java.time.Year;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.lecturer.dto.LecturerDTO;
import com.tl_connect.dev.modules.major.entity.Major;
import com.tl_connect.dev.modules.major.repository.MajorRepository;
import com.tl_connect.dev.modules.student_class.StudentClassRepository;
import com.tl_connect.dev.modules.student_class.dto.CreateStudentClassDTO;
import com.tl_connect.dev.modules.student_class.dto.StudentClassInfoDTO;
import com.tl_connect.dev.modules.student_class.dto.StudentInClassDTO;
import com.tl_connect.dev.modules.student_class.dto.UpdateStudentClassDTO;
import com.tl_connect.dev.modules.student_class.entity.StudentClass;
import com.tl_connect.dev.modules.student_class.projection.ClassHeaderView;
import com.tl_connect.dev.modules.student_class.projection.StudentClassRow;
import com.tl_connect.dev.modules.student_class.projection.StudentInClassRow;
import com.tl_connect.dev.modules.student_class.service.interfaces.StudentClassService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.modules.student.repository.StudentRepository;
import com.tl_connect.dev.modules.lecturer.repository.AcademicAdvisorRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentClassServiceImpl implements StudentClassService {
    
    private final StudentClassRepository studentClassRepository;
    private final MajorRepository majorRepository;
    private final StudentRepository studentRepository;
    private final AcademicAdvisorRepository academicAdvisorRepository;
    
    public PagedResponse<StudentClassRow> getAll(Pageable pageable, String facultyCode) {
        if(facultyCode == null || facultyCode.isBlank()) {
            facultyCode = null;
        }
        Page<StudentClassRow> studentClasses = studentClassRepository.getAllWithStudentCount(pageable, facultyCode);
        return new PagedResponse<>(
                studentClasses.getContent(),
                studentClasses.getNumber(),
                studentClasses.getSize(),
                studentClasses.getTotalElements(),
                studentClasses.getTotalPages(),
                studentClasses.isFirst(),
                studentClasses.isLast());
    }

    public StudentClassInfoDTO getStudentClassInfo(Long id) {
            ClassHeaderView header = studentClassRepository.findClassHeaderByClassId(id)
                    .orElseThrow(() -> new NotFoundException("Student class not found for student id: " + id));
            Long classId = header.getClassId();

            List<StudentInClassRow> students = studentClassRepository.findStudentsByClassId(classId);
            return StudentClassInfoDTO.builder()
                            .classCode(header.getClassCode())
                            .majorName(header.getMajorName())
                            .startYear(header.getStartYear())
                            .academicAdvisor(LecturerDTO.builder()
                                            .lecturerCode(header.getLecturerCode())
                                            .fullName(header.getAcademicAdvisor())
                                            .phoneNumber(header.getPhoneNumber())
                                            .email(header.getEmail())
                                            .build())
                            .students(students.stream().map(student -> StudentInClassDTO.builder()
                                            .studentCode(student.getStudentCode())
                                            .fullName(student.getFullName())
                                            .gender(student.getGender())
                                            .build())
                                            .toList())
                            .build();
    }

    @Transactional
    public Long create(CreateStudentClassDTO dto) {
        if (studentClassRepository.existsByClassCode(dto.getClassCode())) {
            throw new InvalidInputException("Class code already exists");
        }

        Major major = majorRepository.findById(dto.getMajorId())
                .orElseThrow(() -> new NotFoundException("Major not found"));

        int currentYear = Year.now().getValue();
        if (dto.getStartYear() > currentYear) {
            throw new InvalidInputException("Start year must be in the past or present");
        }

        StudentClass sc = StudentClass.create(dto.getClassCode(), major.getId(), dto.getStartYear());

        try {
            studentClassRepository.save(sc);
        } catch (DataIntegrityViolationException e) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to create student class: " + e.getMessage());
        }
        return sc.getId();
    }

    @Transactional
    public void update(Long id, UpdateStudentClassDTO dto) {
        StudentClass sc = studentClassRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student class not found"));

        if (dto.getClassCode() != null && !dto.getClassCode().equals(sc.getClassCode())) {

            if (studentClassRepository.existsByClassCode(dto.getClassCode())) {
                throw new InvalidInputException("Class code already exists");
            }
        }

        if (dto.getMajorId() != null) {
            majorRepository.findById(dto.getMajorId())
                    .orElseThrow(() -> new NotFoundException("Major not found"));
        }

        sc.update(dto.getClassCode(), dto.getMajorId(), dto.getStartYear());

        try {
            studentClassRepository.save(sc);
        } catch (DataIntegrityViolationException e) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to update student class: " + e.getMessage());
        }
    }

    @Transactional
    public void delete(Long id) {
        StudentClass sc = studentClassRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student class not found"));

        boolean hasStudents = studentRepository.existsByStudentClassId(id);
        if (hasStudents) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Cannot delete class with students");
        }

        boolean hasAdvisor = academicAdvisorRepository.existsByStudentClassId(id);
        if (hasAdvisor) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Cannot delete class with advisor");
        }
        try {
            studentClassRepository.delete(sc);
        } catch (DataIntegrityViolationException e) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to delete student class: ");
        }
    }

    public List<StudentInClassRow> findStudentsByClassId(Long classId){
        return studentClassRepository.findStudentsByClassId(classId);
    }

    public StudentClass findByClassCode(String classCode){
        return studentClassRepository.findByClassCode(classCode)
            .orElseThrow(() -> new NotFoundException("Student class not found"));
    }
}
