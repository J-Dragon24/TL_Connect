package com.tl_connect.dev.modules.student.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.core.common.enums.TrainingType;
import com.tl_connect.dev.core.common.exception.ConflictException;
import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.major.entity.Major;
import com.tl_connect.dev.modules.major.entity.StudentMajor;
import com.tl_connect.dev.modules.major.repository.MajorRepository;
import com.tl_connect.dev.modules.student.dto.StudentImportDTO;
import com.tl_connect.dev.modules.student.entity.AcademicInfo;
import com.tl_connect.dev.modules.student.entity.EmergencyContact;
import com.tl_connect.dev.modules.student.entity.IdentityCard;
import com.tl_connect.dev.modules.student.entity.Student;
import com.tl_connect.dev.modules.student.entity.StudentContact;
import com.tl_connect.dev.modules.student.repository.StudentRepository;
import com.tl_connect.dev.modules.student_class.StudentClassRepository;
import com.tl_connect.dev.modules.student_class.entity.StudentClass;
import com.tl_connect.dev.modules.study_program.StudyProgramRepository;
import com.tl_connect.dev.modules.study_program.entity.StudyProgram;
import com.tl_connect.dev.modules.student.repository.AcademicInfoRepository;
import com.tl_connect.dev.modules.student.repository.StudentContactRepository;
import com.tl_connect.dev.modules.student.repository.EmergencyContactRepository;
import com.tl_connect.dev.modules.student.repository.IdentityCardRepository;
import com.tl_connect.dev.modules.major.repository.StudentMajorRepository;
import com.tl_connect.dev.modules.student.dto.UpdateBasicInfoDTO;
import com.tl_connect.dev.modules.student.dto.UpdateStudentAcademicDTO;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentUpdateService {

    private final StudentRepository studentRepository;
    private final MajorRepository majorRepository;
    private final StudentClassRepository studentClassRepository;
    private final AcademicInfoRepository academicInfoRepository;

    private final StudentContactRepository studentContactRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final IdentityCardRepository identityCardRepository;
    private final StudentMajorRepository studentMajorRepository;
    private final StudyProgramRepository studyProgramRepository;
    private final Validator validator;

    @Transactional
    public void updateBasicInfo(Long studentId, UpdateBasicInfoDTO dto) {
        Set<ConstraintViolation<UpdateBasicInfoDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        updateStudent(student, dto);

        updateContact(studentId, dto);

        updateEmergencyContact(studentId, dto);

        updateIdentityCard(studentId, dto);
    }

    private void updateStudent(Student student, UpdateBasicInfoDTO dto) {
        if (dto.getStudentCode() != null) {
            if (!student.getStudentCode().equals(dto.getStudentCode())
                    && studentRepository.existsByStudentCode(dto.getStudentCode())) {
                throw new ConflictException("Student code already exists");
            }
            student.setStudentCode(dto.getStudentCode());
        }

        if (dto.getFullName() != null) {
            student.setFullName(dto.getFullName());
        }

        if (dto.getGender() != null) {
            student.setGender(dto.getGender());
        }

        if (dto.getDateOfBirth() != null) {
            student.setDateOfBirth(dto.getDateOfBirth());
        }
    }

    private void updateContact(Long studentId, UpdateBasicInfoDTO dto) {
        if (dto.getPhoneNumber() == null &&
                dto.getAddress() == null &&
                dto.getEmail() == null)
            return;

        StudentContact contact = studentContactRepository
                .findByStudentId(studentId)
                .orElseThrow(() -> new NotFoundException("Contact not found"));

        if (dto.getPhoneNumber() != null) {
            contact.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getAddress() != null) {
            contact.setAddress(dto.getAddress());
        }
        if (dto.getEmail() != null) {
            contact.setEmailPersonal(dto.getEmail());
        }
    }

    private void updateEmergencyContact(Long studentId, UpdateBasicInfoDTO dto) {
        if (dto.getEmergencyContactName() == null &&
                dto.getEmergencyContactPhoneNumber() == null &&
                dto.getEmergencyContactAddress() == null &&
                dto.getEmergencyContactRelationship() == null)
            return;

        EmergencyContact emergency = emergencyContactRepository
                .findByStudentId(studentId)
                .orElseThrow(() -> new NotFoundException("Emergency contact not found"));

        if (dto.getEmergencyContactName() != null) {
            emergency.setFullName(dto.getEmergencyContactName());
        }
        if (dto.getEmergencyContactPhoneNumber() != null) {
            emergency.setPhoneNumber(dto.getEmergencyContactPhoneNumber());
        }
        if (dto.getEmergencyContactAddress() != null) {
            emergency.setAddress(dto.getEmergencyContactAddress());
        }
        if (dto.getEmergencyContactRelationship() != null) {
            emergency.setRelationship(dto.getEmergencyContactRelationship());
        }
    }

    private void updateIdentityCard(Long studentId, UpdateBasicInfoDTO dto) {
        if (dto.getCardNumber() == null &&
                dto.getCardType() == null &&
                dto.getIssuedDate() == null &&
                dto.getIssuedPlace() == null)
            return;

        IdentityCard identity = identityCardRepository
                .findByStudentId(studentId)
                .orElseThrow(() -> new NotFoundException("Identity card not found"));

        if (dto.getCardNumber() != null) {
            identity.setCardNumber(dto.getCardNumber());
        }
        if (dto.getCardType() != null) {
            identity.setCardType(dto.getCardType());
        }
        if (dto.getIssuedDate() != null) {
            identity.setIssuedDate(dto.getIssuedDate());
        }
        if (dto.getIssuedPlace() != null) {
            identity.setIssuedPlace(dto.getIssuedPlace());
        }
    }

    @Transactional
    public void updateAcademicInfo(Long studentId, UpdateStudentAcademicDTO dto) {

        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new NotFoundException("Student not found"));

        StudentMajor studentMajor = studentMajorRepository
                .findPrimaryByStudentId(studentId)
                .orElseThrow(() -> new NotFoundException("Student major not found"));

        AcademicInfo academicInfo = academicInfoRepository
                .findByStudentMajorId(studentMajor.getId())
                .orElseThrow(() -> new NotFoundException("Academic info not found"));

        Major newMajor = null;
        if (dto.getMajorCode() != null) {
            newMajor = majorRepository.findByMajorCode(dto.getMajorCode())
                    .orElseThrow(() -> new NotFoundException("Major not found"));
        }


        StudentClass newClass = null;
        if (dto.getStudentClassCode() != null) {
            newClass = studentClassRepository.findByClassCode(dto.getStudentClassCode())
                    .orElseThrow(() -> new NotFoundException("Student class not found"));
        }

        Long finalMajorId = (newMajor != null)
            ? newMajor.getId()
            : studentMajor.getMajorId();

        if (newClass != null) {
            if (!newClass.getMajorId().equals(finalMajorId)) {
                throw new InvalidInputException("Class does not belong to major");
            }
        }

        if (newClass != null) {
            student.setStudentClassId(newClass.getId());
        }

        if (newMajor != null) {
            studentMajor.setMajorId(newMajor.getId());
        }

        StudyProgram currentProgram = studyProgramRepository.findById(studentMajor.getStudyProgramId())
                .orElseThrow(() -> new NotFoundException("Study program not found"));

        TrainingType finalTrainingType = (dto.getTrainingType() != null)
            ? dto.getTrainingType()
            : currentProgram.getTrainingType();

        Integer finalStartYear = (dto.getStartYear() != null)
            ? dto.getStartYear()
            : currentProgram.getStartYear();

        if (dto.getTrainingType() != null || dto.getStartYear() != null || dto.getMajorCode() != null) {

            Long studyProgramId = studyProgramRepository
                    .findByMajorIdAndTrainingTypeAndStartYear(
                            finalMajorId,
                            finalTrainingType,
                            finalStartYear)
                    .map(StudyProgram::getId)
                    .orElseThrow(() -> new NotFoundException("Study program not found"));

            studentMajor.setStudyProgramId(studyProgramId);
        }

        if (dto.getEndYear() != null) {
            studentMajor.setEndYear(dto.getEndYear());
        }

        if (dto.getCohort() != null) {
            academicInfo.setCohort(dto.getCohort());
        }

        if (dto.getPosition() != null) {
            academicInfo.setPosition(dto.getPosition());
        }
    }
}
