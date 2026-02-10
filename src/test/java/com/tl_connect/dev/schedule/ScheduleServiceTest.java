package com.tl_connect.dev.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tl_connect.dev.academic.entity.Semester;
import com.tl_connect.dev.common.exception.NotFoundException;
import com.tl_connect.dev.schedule.dto.SemesterScheduleDTO;
import com.tl_connect.dev.schedule.dto.WeeklyScheduleDTO;
import com.tl_connect.dev.schedule.projection.ScheduleRow;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    @Test
    @DisplayName("getWeeklySchedule: Semester Not Found -> Throws NotFoundException")
    void getWeeklySchedule_SemesterNotFound() {
        // Arrange
        Long studentId = 1L;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 7);
        when(semesterRepository.findSemesterByDate(startDate)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> scheduleService.getWeeklySchedule(studentId, startDate, endDate));
    }

    @Test
    @DisplayName("getWeeklySchedule: Success -> Returns DTO with correct week calculation")
    void getWeeklySchedule_Success() {
        // Arrange
        Long studentId = 1L;
        LocalDate startDate = LocalDate.of(2023, 1, 8); // One week after start
        LocalDate endDate = LocalDate.of(2023, 1, 14);

        Semester semester = new Semester();
        semester.setId(10L);
        semester.setSemesterName("2023-Spring");
        semester.setStartDate(LocalDate.of(2023, 1, 1));

        when(semesterRepository.findSemesterByDate(startDate)).thenReturn(Optional.of(semester));

        ScheduleRow row = mock(ScheduleRow.class);
        when(row.getDayOfWeek()).thenReturn(2); // Tuesday
        when(row.getSubjectName()).thenReturn("Math");
        when(row.getStartTime()).thenReturn(LocalTime.of(10, 0));
        when(row.getEndTime()).thenReturn(LocalTime.of(11, 30));

        when(scheduleRepository.findScheduleByStudentId(studentId, semester.getId()))
                .thenReturn(Optional.of(List.of(row)));

        // Act
        WeeklyScheduleDTO result = scheduleService.getWeeklySchedule(studentId, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals("2023-Spring", result.getSemester());
        // Week calculation: 2023-01-08 is 7 days after 2023-01-01. (7 / 7) + 1 = 2.
        assertEquals(2, result.getWeek());
        assertEquals(1, result.getDailySchedules().size());

        var daily = result.getDailySchedules().get(0);
        assertEquals(1, daily.getCourseClasses().size());
        assertEquals("Math", daily.getCourseClasses().get(0).getSubjectName());
    }

    @Test
    @DisplayName("getWeeklySchedule: Date Before Semester Start -> IllegalArgumentException")
    void getWeeklySchedule_DateBeforeStart() {
        // Arrange
        Long studentId = 1L;
        LocalDate startDate = LocalDate.of(2022, 12, 31);
        LocalDate endDate = LocalDate.of(2023, 1, 7);

        Semester semester = new Semester();
        semester.setStartDate(LocalDate.of(2023, 1, 1));

        when(semesterRepository.findSemesterByDate(startDate)).thenReturn(Optional.of(semester));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> scheduleService.getWeeklySchedule(studentId, startDate, endDate));
    }

    @Test
    @DisplayName("getSemesterSchedule: Success -> Returns DTO")
    void getSemesterSchedule_Success() {
        // Arrange
        Long studentId = 1L;
        String semesterName = "2023-Spring";
        Semester semester = new Semester();
        semester.setId(10L);
        semester.setSemesterName(semesterName);

        when(semesterRepository.findSemesterByName(semesterName)).thenReturn(Optional.of(semester));

        ScheduleRow row = mock(ScheduleRow.class);
        when(row.getSubjectName()).thenReturn("Physics");

        when(scheduleRepository.findScheduleByStudentId(studentId, semester.getId()))
                .thenReturn(Optional.of(List.of(row)));

        // Act
        SemesterScheduleDTO result = scheduleService.getSemesterSchedule(studentId, semesterName);

        // Assert
        assertNotNull(result);
        assertEquals(semesterName, result.getSemester());
        assertEquals(1, result.getCourseClasses().size());
        assertEquals("Physics", result.getCourseClasses().get(0).getSubjectName());
    }
}
