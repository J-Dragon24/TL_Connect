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
        return new StudentInfoDTO(
                student.getStudentCode(),
                student.getFullName(),
                student.getDateOfBirth(),
                student.getGender(),
                student.getClassCode(),
                student.getAcademicAdvisor(),
                new MajorDTO(
                        student.getMajorCode(),
                        student.getMajorName(),
                        student.getFaculty()
                ),
                new IdentityCardDTO(
                        student.getIdCardNumber(),
                        student.getIdCardType(),
                        student.getIssuedDate(),
                        student.getIssuedPlace()
                ),
                new ContactDTO(
                        student.getPhoneNumber(),
                        student.getEmail(),
                        student.getAdress()
                ),
                new AcademicInfoDTO(
                        student.getCohort(),
                        student.getPosition(),
                        student.getEducationMode()
                ),
                new EmergencyContactDTO(
                        student.getEmergencyContactName(),
                        student.getEmergencyContactPhoneNumber(),
                        student.getEmergencyContactAdress()
                )
            );
    }

    @Transactional(readOnly = true)
    public StudentClassInfoDTO getStudentClassInfo(Long id) {
        ClassHeaderView header = studentRepository.findClassHeaderById(id);
        if (header == null) {
            throw new NotFoundException("Student class not found for student id: " + id);
        }
        Long classId = header.getClassId();

        List<StudentInClassRow> students = studentClassRepository.findStudentsByClassId(classId);
        return new StudentClassInfoDTO(
                header.getClassCode(),
                new LecturerDTO(
                        header.getLecturerCode(),
                        header.getAcademicAdvisor(),
                        header.getPhoneNumber(),
                        header.getEmail()),
                students.stream().map(student -> new StudentInClassDTO(
                        student.getStudentCode(),
                        student.getFullName(),
                        student.getGender())).toList());
    }
}
