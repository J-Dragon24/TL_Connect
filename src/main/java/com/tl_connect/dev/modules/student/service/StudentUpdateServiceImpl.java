package com.tl_connect.dev.modules.student.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.major.entity.Major;
import com.tl_connect.dev.modules.major.entity.StudentMajor;
import com.tl_connect.dev.modules.student.entity.AcademicInfo;
import com.tl_connect.dev.modules.student.entity.EmergencyContact;
import com.tl_connect.dev.modules.student.entity.IdentityCard;
import com.tl_connect.dev.modules.student.entity.Student;
import com.tl_connect.dev.modules.student.entity.StudentContact;
import com.tl_connect.dev.modules.student.repository.StudentRepository;
import com.tl_connect.dev.modules.student_class.entity.StudentClass;
import com.tl_connect.dev.modules.student_class.service.interfaces.StudentClassService;
import com.tl_connect.dev.modules.study_program.entity.StudyProgram;
import com.tl_connect.dev.modules.study_program.service.interfaces.StudyProgramService;
import com.tl_connect.dev.shared.common.enums.TrainingType;
import com.tl_connect.dev.shared.common.exception.ConflictException;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.modules.student.repository.AcademicInfoRepository;
import com.tl_connect.dev.modules.student.repository.StudentContactRepository;
import com.tl_connect.dev.modules.student.repository.EmergencyContactRepository;
import com.tl_connect.dev.modules.student.repository.IdentityCardRepository;
import com.tl_connect.dev.modules.major.service.interfaces.MajorService;
import com.tl_connect.dev.modules.student.dto.UpdateBasicInfoDTO;
import com.tl_connect.dev.modules.student.dto.UpdateStudentAcademicDTO;
import com.tl_connect.dev.modules.student.service.interfaces.StudentUpdateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentUpdateServiceImpl implements StudentUpdateService {

    private final StudentRepository studentRepository;
    private final MajorService majorService;
    private final StudentClassService studentClassService;
    private final AcademicInfoRepository academicInfoRepository;

    private final StudentContactRepository studentContactRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final IdentityCardRepository identityCardRepository;
    private final StudyProgramService studyProgramService;

    @Transactional
    public void updateBasicInfo(Long studentId, UpdateBasicInfoDTO dto) {
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

        StudentMajor studentMajor = majorService.findPrimaryByStudentId(studentId);

        AcademicInfo academicInfo = academicInfoRepository
                .findByStudentMajorId(studentMajor.getId())
                .orElseThrow(() -> new NotFoundException("Academic info not found"));

        Major newMajor = null;
        if (dto.getMajorCode() != null) {
            newMajor = majorService.findByMajorCode(dto.getMajorCode());
        }


        StudentClass newClass = null;
        if (dto.getStudentClassCode() != null) {
            newClass = studentClassService.findByClassCode(dto.getStudentClassCode());
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

        StudyProgram currentProgram = studyProgramService.findById(studentMajor.getStudyProgramId());

        TrainingType finalTrainingType = (dto.getTrainingType() != null)
            ? dto.getTrainingType()
            : currentProgram.getTrainingType();

        Integer finalStartYear = (dto.getStartYear() != null)
            ? dto.getStartYear()
            : currentProgram.getStartYear();

        if (dto.getTrainingType() != null || dto.getStartYear() != null || dto.getMajorCode() != null) {
            Long studyProgramId = studyProgramService.findByMajorIdAndTrainingTypeAndStartYear(finalMajorId,
                    finalTrainingType,
                    finalStartYear).getId();

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
