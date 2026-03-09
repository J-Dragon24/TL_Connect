package com.tl_connect.dev.modules.student;

import org.springframework.stereotype.Service;

import java.util.List;

import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.core.common.ultility.AuthHelper;
import com.tl_connect.dev.modules.student.dto.AcademicInfoDTO;
import com.tl_connect.dev.modules.student.dto.ContactDTO;
import com.tl_connect.dev.modules.student.dto.EmergencyContactDTO;
import com.tl_connect.dev.modules.student.dto.HealthInsDTO;
import com.tl_connect.dev.modules.student.dto.HealthInsDetailDTO;
import com.tl_connect.dev.modules.student.dto.IdentityCardDTO;
import com.tl_connect.dev.modules.student.dto.MajorDTO;
import com.tl_connect.dev.modules.student.dto.StudentInfoDTO;
import com.tl_connect.dev.modules.student.projection.HealthInsuranceView;
import com.tl_connect.dev.modules.student.projection.StudentInfoView;
import com.tl_connect.dev.modules.student_class.StudentClassRepository;
import com.tl_connect.dev.modules.student_class.dto.LecturerDTO;
import com.tl_connect.dev.modules.student_class.dto.StudentClassInfoDTO;
import com.tl_connect.dev.modules.student_class.dto.StudentInClassDTO;
import com.tl_connect.dev.modules.student_class.projection.ClassHeaderView;
import com.tl_connect.dev.modules.student_class.projection.StudentInClassRow;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentInfoService {

        private final StudentRepository studentRepository;
        private final StudentClassRepository studentClassRepository;
        private final AuthHelper authHelper;

        public StudentInfoDTO getStudentInfo(Long id) {
                StudentInfoView student = studentRepository.findStudentInfoById(id)
                        .orElseThrow(() -> new NotFoundException("Student not found with id: " + id));
                return StudentInfoDTO.builder()
                                .studentCode(student.getStudentCode())
                                .fullName(student.getFullName())
                                .dateOfBirth(student.getDateOfBirth())
                                .gender(student.getGender())
                                .classCode(student.getClassCode())
                                .academicAdvisor(student.getAcademicAdvisor())
                                .major(MajorDTO.builder()
                                                .majorCode(student.getMajorCode())
                                                .majorName(student.getMajorName())
                                                .faculty(student.getFaculty())
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
                                                .address(student.getAdress())
                                                .build())
                                .academicInfo(AcademicInfoDTO.builder()
                                                .cohort(student.getCohort())
                                                .position(student.getPosition())
                                                .educationMode(student.getEducationMode())
                                                .build())
                                .emergencyContact(EmergencyContactDTO.builder()
                                                .name(student.getEmergencyContactName())
                                                .phoneNumber(student.getEmergencyContactPhoneNumber())
                                                .address(student.getEmergencyContactAdress())
                                                .build())
                                .build();
        }

        public StudentClassInfoDTO getStudentClassInfo(Long id) {
                ClassHeaderView header = studentRepository.findClassHeaderById(id)
                        .orElseThrow(() -> new NotFoundException("Student class not found for student id: " + id));
                Long classId = header.getClassId();

                List<StudentInClassRow> students = studentClassRepository.findStudentsByClassId(classId);
                return StudentClassInfoDTO.builder()
                                .classCode(header.getClassCode())
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
}
