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
import com.tl_connect.dev.modules.study_program.StudyProgramRepository;
import com.tl_connect.dev.modules.study_program.StudyProgramService;
import com.tl_connect.dev.modules.study_program.dto.StudyProgramDTO;
import com.tl_connect.dev.modules.study_program.projection.SubjectPrerequisiteRow;
import com.tl_connect.dev.modules.study_program.projection.StudyProgramHeaderView;
import com.tl_connect.dev.modules.study_program.projection.StudyProgramSubjectRow;

@ExtendWith(MockitoExtension.class)
class StudyProgramServiceTest {

        @Mock
        private StudyProgramRepository studyProgramRepository;

        @InjectMocks
        private StudyProgramService studyProgramService;

        private Long studentId;
        private Long programId;

        @BeforeEach
        void setUp() {
                studentId = 1L;
                programId = 10L;
        }

        @Test
        void getStudyProgram_Success() {
                // Arrange
                StudyProgramHeaderView header = mock(StudyProgramHeaderView.class);
                when(header.getId()).thenReturn(programId);
                when(header.getStudyProgramName()).thenReturn("CS Program");
                when(header.getYearStart()).thenReturn(2021);
                when(header.getMajorCode()).thenReturn("CS");
                when(header.getMajorName()).thenReturn("Computer Science");
                when(header.getFaculty()).thenReturn("Engineering");

                when(studyProgramRepository.findStudyProgramHeaderByStudentId(studentId))
                                .thenReturn(Optional.of(header));

                StudyProgramSubjectRow subjectRow = mock(StudyProgramSubjectRow.class);
                when(subjectRow.getSemesterId()).thenReturn(1L);
                when(subjectRow.getSemesterName()).thenReturn("Semester 1");
                when(subjectRow.getSemesterStartDate()).thenReturn(LocalDate.of(2021, 9, 1));
                when(subjectRow.getSemesterEndDate()).thenReturn(LocalDate.of(2022, 1, 31));
                when(subjectRow.getSubjectId()).thenReturn(100L);
                when(subjectRow.getSubjectCode()).thenReturn("CS101");
                when(subjectRow.getSubjectName()).thenReturn("Intro to CS");
                when(subjectRow.getCredits()).thenReturn(3);
                when(subjectRow.getIsRequired()).thenReturn(true);

                when(studyProgramRepository.findSubjectsByProgramId(programId))
                                .thenReturn(List.of(subjectRow));

                SubjectPrerequisiteRow prereqRow = mock(SubjectPrerequisiteRow.class);
                when(prereqRow.getSubjectId()).thenReturn(100L);
                when(prereqRow.getPrerequisiteSubjectCode()).thenReturn("MATH101");
                when(prereqRow.getPrerequisiteSubjectName()).thenReturn("Calculus 1");

                when(studyProgramRepository.findSubjectPrerequisitesByProgramId(programId))
                                .thenReturn(List.of(prereqRow));

                // Act
                StudyProgramDTO result = studyProgramService.getStudyProgram(studentId);

                // Assert
                assertNotNull(result);
                assertEquals("CS Program", result.getStudyProgramName());
                assertEquals(1, result.getSemesters().size());
                assertEquals("Semester 1", result.getSemesters().get(0).getSemesterName());
                assertEquals(1, result.getSemesters().get(0).getSubjects().size());
                assertEquals("CS101", result.getSemesters().get(0).getSubjects().get(0).getSubjectCode());
                assertEquals(1, result.getSemesters().get(0).getSubjects().get(0).getSubjectPrerequisite().size());
                assertEquals("MATH101",
                                result.getSemesters().get(0).getSubjects().get(0).getSubjectPrerequisite().get(0)
                                                .getSubjectCode());

                verify(studyProgramRepository).findStudyProgramHeaderByStudentId(studentId);
                verify(studyProgramRepository).findSubjectsByProgramId(programId);
                verify(studyProgramRepository).findSubjectPrerequisitesByProgramId(programId);
        }

        @Test
        void getStudyProgram_HeaderNotFound_ShouldThrowNotFoundException() {
                // Arrange
                when(studyProgramRepository.findStudyProgramHeaderByStudentId(studentId))
                                .thenReturn(Optional.empty());

                // Act & Assert
                NotFoundException exception = assertThrows(NotFoundException.class,
                                () -> studyProgramService.getStudyProgram(studentId));
                assertTrue(exception.getMessage().contains("Study program not found for student"));
        }

        @Test
        void getStudyProgram_SubjectsNotFound_ShouldThrowNotFoundException() {
                // Arrange
                StudyProgramHeaderView header = mock(StudyProgramHeaderView.class);
                when(header.getId()).thenReturn(programId);
                when(studyProgramRepository.findStudyProgramHeaderByStudentId(studentId))
                                .thenReturn(Optional.of(header));
                when(studyProgramRepository.findSubjectsByProgramId(programId))
                                .thenReturn(List.of());

                // Act & Assert
                NotFoundException exception = assertThrows(NotFoundException.class,
                                () -> studyProgramService.getStudyProgram(studentId));
                assertTrue(exception.getMessage().contains("Study program not found for program"));
        }

        @Test
        void getStudyProgram_PrerequisitesNotFound_ShouldThrowNotFoundException() {
                // Arrange
                StudyProgramHeaderView header = mock(StudyProgramHeaderView.class);
                when(header.getId()).thenReturn(programId);
                when(studyProgramRepository.findStudyProgramHeaderByStudentId(studentId))
                                .thenReturn(Optional.of(header));
                when(studyProgramRepository.findSubjectsByProgramId(programId))
                                .thenReturn(List.of(mock(StudyProgramSubjectRow.class)));
                when(studyProgramRepository.findSubjectPrerequisitesByProgramId(programId))
                                .thenReturn(List.of());

                // Act & Assert
                NotFoundException exception = assertThrows(NotFoundException.class,
                                () -> studyProgramService.getStudyProgram(studentId));
                assertTrue(exception.getMessage().contains("Subject prerequisites not found"));
        }
}
