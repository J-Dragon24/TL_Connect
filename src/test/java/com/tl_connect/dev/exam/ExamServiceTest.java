package com.tl_connect.dev.exam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tl_connect.dev.academic.entity.Semester;
import com.tl_connect.dev.common.enums.AttendanceStatus;
import com.tl_connect.dev.common.enums.ExamStatus;
import com.tl_connect.dev.common.exception.NotFoundException;
import com.tl_connect.dev.exam.dto.ExamScheduleDTO;
import com.tl_connect.dev.exam.projection.ExamScheduleView;
import com.tl_connect.dev.schedule.SemesterRepository;

@ExtendWith(MockitoExtension.class)
class ExamServiceTest {

    @Mock
    private ExamRepository examRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @InjectMocks
    private ExamService examService;

    @Test
    @DisplayName("getExamSchedule: Semester Not Found -> Throws NotFoundException")
    void getExamSchedule_SemesterNotFound() {
        // Arrange
        Long studentId = 1L;
        String semesterName = "2023-Spring";
        when(semesterRepository.findSemesterByName(semesterName)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> examService.getExamSchedule(studentId, semesterName));
        verify(examRepository, never()).findExamSchedule(anyLong(), anyLong());
    }

    @Test
    @DisplayName("getExamSchedule: Success -> Returns DTO with schedules")
    void getExamSchedule_Success() {
        // Arrange
        Long studentId = 1L;
        String semesterName = "2023-Spring";
        Semester semester = new Semester();
        semester.setId(10L);
        semester.setSemesterName(semesterName);

        ExamScheduleView view = mock(ExamScheduleView.class);
        when(view.getSubjectCode()).thenReturn("CS101");
        when(view.getSubjectName()).thenReturn("Computer Science");
        when(view.getClassCode()).thenReturn("CS101-01");
        when(view.getExamDate()).thenReturn(LocalDate.of(2023, 5, 10));
        when(view.getStartTime()).thenReturn(LocalTime.of(9, 0));
        when(view.getEndTime()).thenReturn(LocalTime.of(11, 0));
        when(view.getExamRoom()).thenReturn("Room 101");
        when(view.getExamLocation()).thenReturn("Building A");
        when(view.getExamFormat()).thenReturn("Written");
        when(view.getExamType()).thenReturn("Final");
        when(view.getExamAttempt()).thenReturn(1);
        when(view.getAttendanceStatus()).thenReturn(AttendanceStatus.ATTENDED);
        when(view.getExamStatus()).thenReturn(ExamStatus.DONE);

        when(semesterRepository.findSemesterByName(semesterName)).thenReturn(Optional.of(semester));
        when(examRepository.findExamSchedule(studentId, semester.getId())).thenReturn(Optional.of(List.of(view)));

        // Act
        ExamScheduleDTO result = examService.getExamSchedule(studentId, semesterName);

        // Assert
        assertNotNull(result);
        assertEquals(semesterName, result.getSemesterName());
        assertEquals(1, result.getExamSchedules().size());

        var dto = result.getExamSchedules().get(0);
        assertEquals("CS101", dto.getSubjectCode());
        assertEquals("Computer Science", dto.getSubjectName());
        assertEquals("Room 101", dto.getExamRoom());
        assertEquals("Final", dto.getExamType());
    }

    @Test
    @DisplayName("getExamSchedule: No Exams -> Returns DTO with empty list")
    void getExamSchedule_NoExams() {
        // Arrange
        Long studentId = 1L;
        String semesterName = "2023-Spring";
        Semester semester = new Semester();
        semester.setId(10L);
        semester.setSemesterName(semesterName);

        when(semesterRepository.findSemesterByName(semesterName)).thenReturn(Optional.of(semester));
        when(examRepository.findExamSchedule(studentId, semester.getId())).thenReturn(Optional.empty()); // Or
                                                                                                         // Collections.emptyList()
                                                                                                         // wrapped

        // Act
        ExamScheduleDTO result = examService.getExamSchedule(studentId, semesterName);

        // Assert
        assertNotNull(result);
        assertEquals(semesterName, result.getSemesterName());
        assertTrue(result.getExamSchedules().isEmpty());
    }
}
