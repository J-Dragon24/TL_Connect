package com.tl_connect.dev.student;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.tl_connect.dev.common.exception.NotFoundException;
import com.tl_connect.dev.dto.res.LecturerDTO;
import com.tl_connect.dev.student.dto.AcademicInfoDTO;
import com.tl_connect.dev.student.dto.ContactDTO;
import com.tl_connect.dev.student.dto.EmergencyContactDTO;
import com.tl_connect.dev.student.dto.IdentityCardDTO;
import com.tl_connect.dev.student.dto.MajorDTO;
import com.tl_connect.dev.student.dto.StudentInfoDTO;
import com.tl_connect.dev.student.projection.StudentInfoView;
import com.tl_connect.dev.student_class.StudentClassRepository;
import com.tl_connect.dev.student_class.dto.StudentClassInfoDTO;
import com.tl_connect.dev.student_class.dto.StudentInClassDTO;
import com.tl_connect.dev.student_class.projection.ClassHeaderView;
import com.tl_connect.dev.student_class.projection.StudentInClassRow;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentInfoService {

    private final StudentRepository studentRepository;
    private final StudentClassRepository studentClassRepository;

    public StudentInfoDTO getStudentInfo(Long id) {
        StudentInfoView student = studentRepository.findStudentInfoById(id);
        if (student == null) {
            throw new NotFoundException("Student not found with id: " + id);
        }
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

    @Transactional(readOnly = true)
    public StudentClassInfoDTO getStudentClassInfo(Long id) {
        ClassHeaderView header = studentRepository.findClassHeaderById(id);
        if (header == null) {
            throw new NotFoundException("Student class not found for student id: " + id);
        }
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
}
