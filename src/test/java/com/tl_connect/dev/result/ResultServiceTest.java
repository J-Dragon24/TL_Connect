package com.tl_connect.dev.result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tl_connect.dev.result.dto.AcademicResultDTO;
import com.tl_connect.dev.result.projection.SemesterSummaryView;
import com.tl_connect.dev.result.projection.SubjectResultRow;

@ExtendWith(MockitoExtension.class)
class ResultServiceTest {

    @Mock
    private ResultRepository resultRepository;

    @InjectMocks
    private ResultService resultService;

    @Test
    @DisplayName("getSubjectResult: Success -> Returns DTO with grouped results")
    void getSubjectResult_Success() {
        // Arrange
        Long studentId = 1L;
        String trainingProgram = "Software Engineering";
        String semester1 = "2023-Spring";
        String semester2 = "2023-Fall";

        // Mock SubjectResultRow
        SubjectResultRow row1 = mock(SubjectResultRow.class);
        when(row1.getSemester()).thenReturn(semester1);
        when(row1.getSubjectCode()).thenReturn("CS101");
        when(row1.getScore10()).thenReturn(9.5);
        when(row1.getCredits()).thenReturn(3);

        SubjectResultRow row2 = mock(SubjectResultRow.class);
        when(row2.getSemester()).thenReturn(semester2);
        when(row2.getSubjectCode()).thenReturn("CS102");
        when(row2.getScore10()).thenReturn(8.0);
        when(row2.getCredits()).thenReturn(4);

        when(resultRepository.findSubjectResult(studentId)).thenReturn(List.of(row1, row2));

        // Mock SemesterSummaryView
        SemesterSummaryView summary1 = mock(SemesterSummaryView.class);
        when(summary1.getSemester()).thenReturn(semester1);
        when(summary1.getSemesterGpa()).thenReturn(3.8);

        SemesterSummaryView summary2 = mock(SemesterSummaryView.class);
        when(summary2.getSemester()).thenReturn(semester2);
        when(summary2.getSemesterGpa()).thenReturn(3.5);

        when(resultRepository.findSemesterSummary(studentId)).thenReturn(List.of(summary1, summary2));

        // Act
        AcademicResultDTO result = resultService.getSubjectResult(studentId, trainingProgram);

        // Assert
        assertNotNull(result);
        assertEquals(trainingProgram, result.getTrainingProgram());
        assertEquals(2, result.getSemesterResults().size());

        // Verify correct semester data
        var sem1Result = result.getSemesterResults().stream().filter(s -> s.getSemester().equals(semester1)).findFirst()
                .orElseThrow();
        assertEquals(1, sem1Result.getSubjectResults().size());
        assertEquals("CS101", sem1Result.getSubjectResults().get(0).getSubjectCode());
        assertEquals(3.8, sem1Result.getSemesterSummary().getSemesterGpa());

        var sem2Result = result.getSemesterResults().stream().filter(s -> s.getSemester().equals(semester2)).findFirst()
                .orElseThrow();
        assertEquals(1, sem2Result.getSubjectResults().size());
        assertEquals(3.5, sem2Result.getSemesterSummary().getSemesterGpa());
    }

    @Test
    @DisplayName("getSubjectResult: No Data -> Returns Empty DTO")
    void getSubjectResult_NoData() {
        // Arrange
        Long studentId = 1L;
        String trainingProgram = "Software Engineering";
        when(resultRepository.findSubjectResult(studentId)).thenReturn(List.of());
        when(resultRepository.findSemesterSummary(studentId)).thenReturn(List.of());

        // Act
        AcademicResultDTO result = resultService.getSubjectResult(studentId, trainingProgram);

        // Assert
        assertNotNull(result);
        assertEquals(trainingProgram, result.getTrainingProgram());
        assertTrue(result.getSemesterResults().isEmpty());
    }
}
