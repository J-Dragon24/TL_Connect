package com.tl_connect.dev.student;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tl_connect.dev.common.enums.EducationMode;
import com.tl_connect.dev.common.enums.Gender;
import com.tl_connect.dev.common.enums.IdCardType;
import com.tl_connect.dev.common.exception.NotFoundException;
import com.tl_connect.dev.student.dto.StudentInfoDTO;
import com.tl_connect.dev.student.projection.StudentInfoView;
import com.tl_connect.dev.student_class.StudentClassRepository;
import com.tl_connect.dev.student_class.dto.StudentClassInfoDTO;
import com.tl_connect.dev.student_class.projection.ClassHeaderView;
import com.tl_connect.dev.student_class.projection.StudentInClassRow;

@ExtendWith(MockitoExtension.class)
class StudentInfoServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentClassRepository studentClassRepository;

    @InjectMocks
    private StudentInfoService studentInfoService;

    private Long studentId;
    private StudentInfoView studentInfoView;

    @BeforeEach
    void setUp() {
        studentId = 1L;
        studentInfoView = mock(StudentInfoView.class);
    }

    @Test
    void getStudentInfo_WhenStudentExists_ShouldReturnStudentInfoDTO() {
        // Arrange
        when(studentRepository.findStudentInfoById(studentId)).thenReturn(studentInfoView);
        when(studentInfoView.getStudentCode()).thenReturn("ST001");
        when(studentInfoView.getFullName()).thenReturn("John Doe");
        when(studentInfoView.getDateOfBirth()).thenReturn(LocalDate.of(2000, 1, 1));
        when(studentInfoView.getGender()).thenReturn(Gender.NAM);
        when(studentInfoView.getClassCode()).thenReturn("CL001");
        when(studentInfoView.getAcademicAdvisor()).thenReturn("Advisor Name");
        when(studentInfoView.getMajorCode()).thenReturn("M01");
        when(studentInfoView.getMajorName()).thenReturn("Computer Science");
        when(studentInfoView.getFaculty()).thenReturn("Engineering");
        when(studentInfoView.getIdCardNumber()).thenReturn("123456789");
        when(studentInfoView.getIdCardType()).thenReturn(IdCardType.CCCD);
        when(studentInfoView.getIssuedDate()).thenReturn(LocalDate.of(2015, 1, 1));
        when(studentInfoView.getIssuedPlace()).thenReturn("Hanoi");
        when(studentInfoView.getPhoneNumber()).thenReturn("0123456789");
        when(studentInfoView.getEmail()).thenReturn("john.doe@example.com");
        when(studentInfoView.getAdress()).thenReturn("123 Street");
        when(studentInfoView.getCohort()).thenReturn("K65");
        when(studentInfoView.getPosition()).thenReturn("Student");
        when(studentInfoView.getEducationMode()).thenReturn(EducationMode.CHINH_QUY);
        when(studentInfoView.getEmergencyContactName()).thenReturn("Emergency Jane");
        when(studentInfoView.getEmergencyContactPhoneNumber()).thenReturn("0987654321");
        when(studentInfoView.getEmergencyContactAdress()).thenReturn("456 Street");

        // Act
        StudentInfoDTO result = studentInfoService.getStudentInfo(studentId);

        // Assert
        assertNotNull(result);
        assertEquals("ST001", result.getStudentCode());
        assertEquals("John Doe", result.getFullName());
        assertEquals("Computer Science", result.getMajor().getMajorName());
        assertEquals("123456789", result.getIdentityCard().getCardNumber());
        assertEquals("john.doe@example.com", result.getContact().getEmail());
        verify(studentRepository).findStudentInfoById(studentId);
    }

    @Test
    void getStudentInfo_WhenStudentDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        when(studentRepository.findStudentInfoById(studentId)).thenReturn(null);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> studentInfoService.getStudentInfo(studentId));
        assertEquals("Student not found with id: " + studentId, exception.getMessage());
        verify(studentRepository).findStudentInfoById(studentId);
    }

    @Test
    void getStudentClassInfo_WhenClassExists_ShouldReturnStudentClassInfoDTO() {
        // Arrange
        ClassHeaderView header = mock(ClassHeaderView.class);
        Long classId = 10L;
        when(studentRepository.findClassHeaderById(studentId)).thenReturn(header);
        when(header.getClassId()).thenReturn(classId);
        when(header.getClassCode()).thenReturn("CL001");
        when(header.getLecturerCode()).thenReturn("L001");
        when(header.getAcademicAdvisor()).thenReturn("Advisor Name");
        when(header.getPhoneNumber()).thenReturn("0123456789");
        when(header.getEmail()).thenReturn("advisor@example.com");

        StudentInClassRow row = mock(StudentInClassRow.class);
        when(row.getStudentCode()).thenReturn("ST001");
        when(row.getFullName()).thenReturn("John Doe");
        when(row.getGender()).thenReturn(Gender.NAM);

        when(studentClassRepository.findStudentsByClassId(classId)).thenReturn(List.of(row));

        // Act
        StudentClassInfoDTO result = studentInfoService.getStudentClassInfo(studentId);

        // Assert
        assertNotNull(result);
        assertEquals("CL001", result.getClassCode());
        assertEquals("Advisor Name", result.getAcademicAdvisor().getFullName());
        assertEquals(1, result.getStudents().size());
        assertEquals("ST001", result.getStudents().get(0).getStudentCode());
        verify(studentRepository).findClassHeaderById(studentId);
        verify(studentClassRepository).findStudentsByClassId(classId);
    }

    @Test
    void getStudentClassInfo_WhenClassDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        when(studentRepository.findClassHeaderById(studentId)).thenReturn(null);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> studentInfoService.getStudentClassInfo(studentId));
        assertEquals("Student class not found for student id: " + studentId, exception.getMessage());
        verify(studentRepository).findClassHeaderById(studentId);
    }
}
