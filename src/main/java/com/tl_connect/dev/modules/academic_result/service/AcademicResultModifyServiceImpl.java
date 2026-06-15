package com.tl_connect.dev.modules.academic_result.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.modules.academic_result.dto.CreateStudentSubjectResultDTO;
import com.tl_connect.dev.modules.academic_result.dto.ImportAcademicResultDTO;
import com.tl_connect.dev.modules.academic_result.dto.UpdateStudentSubjectResultDTO;
import com.tl_connect.dev.modules.academic_result.entity.StudentSubjectResult;
import com.tl_connect.dev.modules.academic_result.repository.StudentSemesterSummaryRepository;
import com.tl_connect.dev.modules.academic_result.repository.StudentSubjectResultRepository;
import com.tl_connect.dev.modules.academic_result.service.interfaces.AcademicResultModifyService;
import com.tl_connect.dev.modules.semester.Semester;
import com.tl_connect.dev.modules.semester.service.interfaces.SemesterService;
import com.tl_connect.dev.modules.student.entity.Student;
import com.tl_connect.dev.modules.student.service.interfaces.StudentService;
import com.tl_connect.dev.modules.subject.entity.Subject;
import com.tl_connect.dev.modules.subject.service.interfaces.SubjectService;
import com.tl_connect.dev.shared.common.dto.ImportResultDTO;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.shared.ultility.FileProcess.FileParseHelper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AcademicResultModifyServiceImpl implements AcademicResultModifyService{

    private final StudentSubjectResultRepository subjectResultRepository;
    private final SubjectService subjectService;
    private final StudentService studentService;
    private final FileParseHelper fileParseHelper;
    private final SemesterService semesterService;
    private final StudentSemesterSummaryRepository semesterSummaryRepository;
    private final AcademicResultPersistService academicResultPersistService;
    private final Validator validator;

    @Transactional
    public Long createStudentSubjectResult(CreateStudentSubjectResultDTO dto) {

        StudentSubjectResult entity = StudentSubjectResult.create(
                dto.getStudentId(),
                dto.getSubjectId(),
                dto.getSemesterId(),
                dto.getCredits(),
                dto.getAttendanceScore(),
                dto.getMidtermScore(),
                dto.getFinalScore(),
                dto.getScore10(),
                dto.getScore4(),
                dto.getLetterGrade(),
                dto.getIsPass());

        try {
            return subjectResultRepository.save(entity).getId();
        } catch (DataIntegrityViolationException e) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR, "Error when create student subject result");
        }
    }

    @Transactional
    public ImportResultDTO importFile(MultipartFile file) throws IOException {
        List<ImportAcademicResultDTO> rows = fileParseHelper.parse(file, ImportAcademicResultDTO.class);
        List<StudentSubjectResult> toSave = new ArrayList<>();

        if (rows.isEmpty()) {
            throw new InvalidInputException("File is empty");
        }

        Set<String> studentCodes = rows.stream()
                .map(ImportAcademicResultDTO::getStudentCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Student> studentMap = studentService.findByStudentCodeIn(studentCodes)
                .stream()
                .collect(Collectors.toMap(Student::getStudentCode, s -> s));

        Set<String> subjectCodes = rows.stream()
                .map(ImportAcademicResultDTO::getSubjectCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Subject> subjectMap = subjectService.findBySubjectCodeIn(subjectCodes)
                .stream()
                .collect(Collectors.toMap(Subject::getSubjectCode, s -> s));

        Set<String> semesterCodes = rows.stream()
                .map(ImportAcademicResultDTO::getSemesterCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Long> semesterMap = semesterService.findBySemesterCodeIn(semesterCodes)
                .stream()
                .collect(Collectors.toMap(Semester::getSemesterCode, Semester::getId));
        if (studentMap.size() != studentCodes.size()) {
            Set<String> notFoundCodes = new HashSet<>(studentCodes);
            notFoundCodes.removeAll(studentMap.keySet());
            throw new NotFoundException("Student code not found: " + String.join(", ", notFoundCodes));
        }



        Set<String> failedCodes = new HashSet<>();
        List<StudentSubjectResult> validList = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (ImportAcademicResultDTO row : rows) {
            try {

                String key = row.getStudentCode() + "|" + row.getSubjectCode() + "|" + row.getSemesterCode();
                if (!seen.add(key)) {
                    log.warn("Duplicate row in file: {}", key);
                    failedCodes.add(row.getStudentCode());
                    continue;
                }
                validateRow(row);

                Student student = studentMap.get(row.getStudentCode());
                Subject subject = subjectMap.get(row.getSubjectCode());
                Long semesterId = semesterMap.get(row.getSemesterCode());

                validList.add(StudentSubjectResult.create(
                        student.getId(),
                        subject.getId(),
                        semesterId,
                        subject.getCredits(),
                        row.getAttendanceScore(),
                        row.getMidtermScore(),
                        row.getFinalScore(),
                        row.getScore10(),
                        row.getScore4(),
                        row.getLetterGrade(),
                        row.getIsPass()));

            } catch (Exception e) {
                log.warn("Invalid row: {}", row.getStudentCode(), e);
                failedCodes.add(row.getStudentCode());
            }
        }

        int successCount = 0;
        int batchSize = 50;

        for (int i = 0; i < validList.size(); i += batchSize) {
            List<StudentSubjectResult> chunk = validList.subList(i, Math.min(i + batchSize, validList.size()));
            try {
                academicResultPersistService.persistChunk(chunk);
                successCount += chunk.size();
            } catch (Exception e) {
                log.error("Batch persist failed for chunk [{}-{}]: {}", i, i + chunk.size() - 1, e.getMessage());
                chunk.forEach(rs -> failedCodes.add(rs.getStudentId().toString()));
            }
        }

        return ImportResultDTO.builder()
                .total(rows.size())
                .success(successCount)
                .failed(failedCodes.size())
                .build();
    }

    @Transactional
    public void updateStudentSubjectResult(Long id, UpdateStudentSubjectResultDTO dto) {
        StudentSubjectResult entity = subjectResultRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student subject result not found"));

        entity.update(dto.getSemesterId(), dto.getAttendanceScore(), dto.getMidtermScore(), dto.getFinalScore(),
                dto.getScore10(), dto.getScore4(), dto.getLetterGrade(), dto.getIsPass());

        subjectResultRepository.save(entity);
    }

    @Transactional
    public void deleteStudentSubjectResult(Long id) {
        StudentSubjectResult entity = subjectResultRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student subject result not found"));
        subjectResultRepository.delete(entity);
    }

    private void validateRow(ImportAcademicResultDTO row) {
        Set<ConstraintViolation<ImportAcademicResultDTO>> violations = validator.validate(row);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .findFirst()
                    .orElse("Invalid input");
            throw new InvalidInputException(message);
        }
    }

    @Transactional
    public void calcStudentSemesterSummary(Long semesterId) {
        try {
            semesterSummaryRepository.recalcStudentSemesterSummary(semesterId);
        } catch (Exception e) {
            log.error("Error when recalc student semester summary", e);
            throw new ErrorException(ResponseStatus.DATABASE_ERROR, "Error when recalc student semester summary");
        }
    }

}
