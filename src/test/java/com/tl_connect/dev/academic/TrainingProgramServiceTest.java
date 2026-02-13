package com.tl_connect.dev.academic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.training_program.TrainingProgramRepository;
import com.tl_connect.dev.modules.training_program.TrainingProgramService;
import com.tl_connect.dev.modules.training_program.dto.TrainingProgramDTO;
import com.tl_connect.dev.modules.training_program.projection.SubjectPrerequisiteRow;
import com.tl_connect.dev.modules.training_program.projection.TrainingProgramHeaderView;
import com.tl_connect.dev.modules.training_program.projection.TrainingProgramSubjectRow;

@ExtendWith(MockitoExtension.class)
class TrainingProgramServiceTest {

    @Mock
    private TrainingProgramRepository trainingProgramRepository;

    @InjectMocks
    private TrainingProgramService trainingProgramService;

    private Long studentId;
    private Long programId;

    @BeforeEach
    void setUp() {
        studentId = 1L;
        programId = 10L;
    }

    @Test
    void getTrainingProgram_Success() {
        // Arrange
        TrainingProgramHeaderView header = mock(TrainingProgramHeaderView.class);
        when(header.getId()).thenReturn(programId);
        when(header.getTrainingProgramName()).thenReturn("CS Program");
        when(header.getYearStart()).thenReturn(2021);
        when(header.getMajorCode()).thenReturn("CS");
        when(header.getMajorName()).thenReturn("Computer Science");
        when(header.getFaculty()).thenReturn("Engineering");

        when(trainingProgramRepository.findTrainingProgramHeaderByStudentId(studentId))
                .thenReturn(Optional.of(header));

        TrainingProgramSubjectRow subjectRow = mock(TrainingProgramSubjectRow.class);
        when(subjectRow.getSemesterId()).thenReturn(1L);
        when(subjectRow.getSemesterName()).thenReturn("Semester 1");
        when(subjectRow.getSemesterStartDate()).thenReturn(LocalDate.of(2021, 9, 1));
        when(subjectRow.getSemesterEndDate()).thenReturn(LocalDate.of(2022, 1, 31));
        when(subjectRow.getSubjectId()).thenReturn(100L);
        when(subjectRow.getSubjectCode()).thenReturn("CS101");
        when(subjectRow.getSubjectName()).thenReturn("Intro to CS");
        when(subjectRow.getCredits()).thenReturn(3);
        when(subjectRow.getIsRequired()).thenReturn(true);

        when(trainingProgramRepository.findSubjectsByProgramId(programId))
                .thenReturn(List.of(subjectRow));

        SubjectPrerequisiteRow prereqRow = mock(SubjectPrerequisiteRow.class);
        when(prereqRow.getSubjectId()).thenReturn(100L);
        when(prereqRow.getPrerequisiteSubjectCode()).thenReturn("MATH101");
        when(prereqRow.getPrerequisiteSubjectName()).thenReturn("Calculus 1");

        when(trainingProgramRepository.findSubjectPrerequisitesByProgramId(programId))
                .thenReturn(List.of(prereqRow));

        // Act
        TrainingProgramDTO result = trainingProgramService.getTrainingProgram(studentId);

        // Assert
        assertNotNull(result);
        assertEquals("CS Program", result.getTrainingProgramName());
        assertEquals(1, result.getSemesters().size());
        assertEquals("Semester 1", result.getSemesters().get(0).getSemesterName());
        assertEquals(1, result.getSemesters().get(0).getSubjects().size());
        assertEquals("CS101", result.getSemesters().get(0).getSubjects().get(0).getSubjectCode());
        assertEquals(1, result.getSemesters().get(0).getSubjects().get(0).getSubjectPrerequisite().size());
        assertEquals("MATH101",
                result.getSemesters().get(0).getSubjects().get(0).getSubjectPrerequisite().get(0).getSubjectCode());

        verify(trainingProgramRepository).findTrainingProgramHeaderByStudentId(studentId);
        verify(trainingProgramRepository).findSubjectsByProgramId(programId);
        verify(trainingProgramRepository).findSubjectPrerequisitesByProgramId(programId);
    }

    @Test
    void getTrainingProgram_HeaderNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(trainingProgramRepository.findTrainingProgramHeaderByStudentId(studentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> trainingProgramService.getTrainingProgram(studentId));
        assertTrue(exception.getMessage().contains("Training program not found for student"));
    }

    @Test
    void getTrainingProgram_SubjectsNotFound_ShouldThrowNotFoundException() {
        // Arrange
        TrainingProgramHeaderView header = mock(TrainingProgramHeaderView.class);
        when(header.getId()).thenReturn(programId);
        when(trainingProgramRepository.findTrainingProgramHeaderByStudentId(studentId))
                .thenReturn(Optional.of(header));
        when(trainingProgramRepository.findSubjectsByProgramId(programId))
                .thenReturn(List.of());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> trainingProgramService.getTrainingProgram(studentId));
        assertTrue(exception.getMessage().contains("Training program not found for program"));
    }

    @Test
    void getTrainingProgram_PrerequisitesNotFound_ShouldThrowNotFoundException() {
        // Arrange
        TrainingProgramHeaderView header = mock(TrainingProgramHeaderView.class);
        when(header.getId()).thenReturn(programId);
        when(trainingProgramRepository.findTrainingProgramHeaderByStudentId(studentId))
                .thenReturn(Optional.of(header));
        when(trainingProgramRepository.findSubjectsByProgramId(programId))
                .thenReturn(List.of(mock(TrainingProgramSubjectRow.class)));
        when(trainingProgramRepository.findSubjectPrerequisitesByProgramId(programId))
                .thenReturn(List.of());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> trainingProgramService.getTrainingProgram(studentId));
        assertTrue(exception.getMessage().contains("Subject prerequisites not found"));
    }
}
