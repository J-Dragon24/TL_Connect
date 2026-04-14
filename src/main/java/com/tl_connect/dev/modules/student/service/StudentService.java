package com.tl_connect.dev.modules.student.service;

import org.springframework.stereotype.Service;

import java.util.List;

import com.tl_connect.dev.core.common.dto.PagedResponse;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.lecturer.dto.LecturerDTO;
import com.tl_connect.dev.modules.student.dto.AcademicInfoDTO;
import com.tl_connect.dev.modules.student.dto.ContactDTO;
import com.tl_connect.dev.modules.student.dto.EmergencyContactDTO;
import com.tl_connect.dev.modules.student.dto.HealthInsDTO;
import com.tl_connect.dev.modules.student.dto.HealthInsDetailDTO;
import com.tl_connect.dev.modules.student.dto.IdentityCardDTO;
import com.tl_connect.dev.modules.student.dto.MajorDTO;
import com.tl_connect.dev.modules.student.dto.StudentFullInfo;
import com.tl_connect.dev.modules.student.dto.StudentInfoDTO;
import com.tl_connect.dev.modules.student.dto.YearStudyDTO;
import com.tl_connect.dev.modules.student.projection.HealthInsuranceView;
import com.tl_connect.dev.modules.student.projection.StudentInfoView;
import com.tl_connect.dev.modules.student.projection.StudentRow;
import com.tl_connect.dev.modules.student.projection.StudyYearView;
import com.tl_connect.dev.modules.student.repository.StudentRepository;
import com.tl_connect.dev.modules.student_class.StudentClassRepository;
import com.tl_connect.dev.modules.student_class.dto.StudentClassInfoDTO;
import com.tl_connect.dev.modules.student_class.dto.StudentInClassDTO;
import com.tl_connect.dev.modules.student_class.projection.ClassHeaderView;
import com.tl_connect.dev.modules.student_class.projection.StudentInClassRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {

        private final StudentRepository studentRepository;
        private final StudentClassRepository studentClassRepository;

        public PagedResponse<StudentFullInfo> getAllStudents(Pageable pageable, String facultyCode) {
                if(facultyCode == null || facultyCode.isEmpty()) {
                        facultyCode = "";
                }
                Page<StudentRow> students = studentRepository.findAllStudent(pageable, facultyCode);

                return new PagedResponse<>(
                                students.getContent().stream().map(this::toFullInfo).toList(),
                                students.getNumber(),
                                students.getSize(),
                                students.getTotalElements(),
                                students.getTotalPages(),
                                students.isFirst(),
                                students.isLast());
        }

        public StudentInfoDTO getStudentInfo(Long id) {
                StudentInfoView student = studentRepository.findStudentInfoById(id)
                        .orElseThrow(() -> new NotFoundException("Student not found with id: " + id));
                return toDTO(student);
        }

        public StudentClassInfoDTO getStudentClassInfo(Long id) {
                ClassHeaderView header = studentRepository.findClassHeaderByStudentId(id)
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
                                                .position(student.getPosition())
                                                .fullName(student.getFullName())
                                                .gender(student.getGender())
                                                .build())
                                                .toList())
                                .build();
        }

        public HealthInsDTO getHealthInsurance(Long id) {
                HealthInsuranceView healthInsurance = studentRepository.findHealthInsuranceById(id)
                        .orElseThrow(() -> new NotFoundException("Health insurance not found for student id: " + id));
                HealthInsDetailDTO healthInsDetail = HealthInsDetailDTO.builder()
                                .insuranceNumber(healthInsurance.getInsuranceNumber())
                                .provider(healthInsurance.getProvider())
                                .status(healthInsurance.getStatus())
                                .validFrom(healthInsurance.getValidFrom())
                                .validTo(healthInsurance.getValidTo())
                                .registeredHospital(healthInsurance.getRegisteredHospital())
                                .build();
                return HealthInsDTO.builder()
                                .studentCode(healthInsurance.getStudentCode())
                                .fullName(healthInsurance.getFullName())
                                .dateOfBirth(healthInsurance.getDateOfBirth())
                                .phoneNumber(healthInsurance.getPhoneNumber())
                                .email(healthInsurance.getEmail())
                                .healthInsDetail(healthInsDetail)
                                .build();
        }

        public YearStudyDTO getYearStudy(Long id) {
                StudyYearView studyYear = studentRepository.findYearStudy(id)
                        .orElseThrow(() -> new NotFoundException("Study year not found for student id: " + id));
                return YearStudyDTO.builder()
                                .startYear(studyYear.getStartYear())
                                .endYear(studyYear.getEndYear())
                                .build();
        }

        private StudentInfoDTO toDTO(StudentInfoView student) {
                return StudentInfoDTO.builder()
                                .studentCode(student.getStudentCode())
                                .fullName(student.getFullName())
                                .dateOfBirth(student.getDateOfBirth())
                                .gender(student.getGender())
                                .classCode(student.getClassCode())
                                .academicAdvisor(student.getAcademicAdvisor())
                                .startYear(student.getStartYear())
                                .endYear(student.getEndYear())
                                .trainingType(student.getTrainingType())
                                .major(MajorDTO.builder()
                                                .majorCode(student.getMajorCode())
                                                .majorName(student.getMajorName())
                                                .facultyCode(student.getFaculty())
                                                .build())
                                .identityCard(IdentityCardDTO.builder()
                                                .cardNumber(student.getIdCardNumber())
                                                .cardType(student.getIdCardType())
                                                .issuedDate(student.getIssuedDate())
                                                .issuedPlace(student.getIssuedPlace())
                                                .build())
                                .contact(ContactDTO.builder()
                                                .phoneNumber(student.getPhoneNumber())
                                                .email(student.getEmail())
                                                .address(student.getAddress())
                                                .build())
                                .academicInfo(AcademicInfoDTO.builder()
                                                .cohort(student.getCohort())
                                                .position(student.getPosition())
                                                .build())
                                .emergencyContact(EmergencyContactDTO.builder()
                                                .name(student.getEmergencyContactName())
                                                .phoneNumber(student.getEmergencyContactPhoneNumber())
                                                .address(student.getEmergencyContactAddress())
                                                .relationship(student.getRelationship())
                                                .build())
                                .build();
        }

        private StudentFullInfo toFullInfo(StudentRow student) {
                return StudentFullInfo.builder()
                                .id(student.getId())
                                .studentCode(student.getStudentCode())
                                .fullName(student.getFullName())
                                .dateOfBirth(student.getDateOfBirth())
                                .gender(student.getGender())
                                .classCode(student.getClassCode())
                                .startYear(student.getStartYear())
                                .endYear(student.getEndYear())
                                .trainingType(student.getTrainingType())
                                .majorCode(student.getMajorCode())
                                .identityCard(IdentityCardDTO.builder()
                                                .cardNumber(student.getIdCardNumber())
                                                .cardType(student.getIdCardType())
                                                .issuedDate(student.getIssuedDate())
                                                .issuedPlace(student.getIssuedPlace())
                                                .build())
                                .contact(ContactDTO.builder()
                                                .phoneNumber(student.getPhoneNumber())
                                                .email(student.getEmail())
                                                .address(student.getAddress())
                                                .build())
                                .academicInfo(AcademicInfoDTO.builder()
                                                .cohort(student.getCohort())
                                                .position(student.getPosition())
                                                .build())
                                .emergencyContact(EmergencyContactDTO.builder()
                                                .name(student.getEmergencyContactName())
                                                .phoneNumber(student.getEmergencyContactPhoneNumber())
                                                .address(student.getEmergencyContactAddress())
                                                .relationship(student.getRelationship())
                                                .build())
                                .status(student.getStatus())
                                .build();
        }

}
