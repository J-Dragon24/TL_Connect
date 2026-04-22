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

import com.tl_connect.dev.core.common.dto.ImportResultDTO;
import com.tl_connect.dev.core.common.exception.BadRequestException;
import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.core.common.ultility.importer.FileParseHelper;
import com.tl_connect.dev.modules.academic_result.StudentSubjectResultRepository;
import com.tl_connect.dev.modules.academic_result.dto.CreateStudentSubjectResultDTO;
import com.tl_connect.dev.modules.academic_result.dto.ImportAcademicResultDTO;
import com.tl_connect.dev.modules.academic_result.dto.UpdateStudentSubjectResultDTO;
import com.tl_connect.dev.modules.academic_result.entity.StudentSubjectResult;
import com.tl_connect.dev.modules.semester.Semester;
import com.tl_connect.dev.modules.semester.SemesterRepository;
import com.tl_connect.dev.modules.student.entity.Student;
import com.tl_connect.dev.modules.student.repository.StudentRepository;
import com.tl_connect.dev.modules.subject.entity.Subject;
import com.tl_connect.dev.modules.subject.repository.SubjectRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AcademicResultModifyService {

    private final StudentSubjectResultRepository subjectResultRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final FileParseHelper fileParseHelper;
    private final SemesterRepository semesterRepository;
    private final Validator validator;

    @Transactional
    public Long createStudentSubjectResult(CreateStudentSubjectResultDTO dto) {
        Set<ConstraintViolation<CreateStudentSubjectResultDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }

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
                dto.getIsPass()
        );

        try{
            return subjectResultRepository.save(entity).getId();
        }catch(DataIntegrityViolationException e){
            throw new BadRequestException("Error when create student subject result: " + e.getMessage());
        }
    }
    
    @Transactional
    public ImportResultDTO importFile(MultipartFile file) throws IOException {
        List<ImportAcademicResultDTO> rows = fileParseHelper.parse(file, ImportAcademicResultDTO.class);
        List<StudentSubjectResult> toSave = new ArrayList<>();


        if (rows.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        Set<String> studentCodes = rows.stream()
                .map(ImportAcademicResultDTO::getStudentCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Student> studentMap = studentRepository.findByStudentCodeIn(studentCodes)
                        .stream()
                        .collect(Collectors.toMap(Student::getStudentCode, s -> s));

        Set<String> subjectCodes = rows.stream()
                .map(ImportAcademicResultDTO::getSubjectCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Subject> subjectMap = subjectRepository.findBySubjectCodeIn(subjectCodes)
                        .stream()
                        .collect(Collectors.toMap(Subject::getSubjectCode, s -> s));

        Set<String> semesterCodes = rows.stream()
                .map(ImportAcademicResultDTO::getSemesterCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Long> semesterMap = semesterRepository.findBySemesterCodeIn(semesterCodes)
                        .stream()
                        .collect(Collectors.toMap(Semester::getSemesterCode, Semester::getId));
        if (studentMap.size() != studentCodes.size()) {
            Set<String> notFoundCodes = new HashSet<>(studentCodes);
            notFoundCodes.removeAll(studentMap.keySet());
            throw new BadRequestException("Không tìm thấy sinh viên với mã: " + String.join(", ", notFoundCodes));
        }

        int successCount = 0;
        int failedCount = 0;

        for (ImportAcademicResultDTO row : rows) {
            try {
                validateRow(row);

                Student student = studentMap.get(row.getStudentCode());
                Subject subject = subjectMap.get(row.getSubjectCode());
                Long semesterId = semesterMap.get(row.getSemesterCode());

                StudentSubjectResult entity = StudentSubjectResult.create(
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
                        row.getIsPass()
                );

                toSave.add(entity);

            } catch (Exception e) {
                log.warn("Invalid row: {}", row.getStudentCode(), e);
                failedCount++;
            }
        }

        try {
            subjectResultRepository.saveAll(toSave);
            successCount = toSave.size();
        } catch (DataIntegrityViolationException e) {
            for (StudentSubjectResult rs : toSave) {
                try {
                    subjectResultRepository.save(rs);
                    successCount++;
                } catch (DataIntegrityViolationException ex) {
                    log.warn("Invalid row: {}", rs.getStudentId(), ex);
                    failedCount++;
                }
            }
        }

        return ImportResultDTO.builder()
                .total(rows.size())
                .success(successCount)
                .failed(failedCount)
                .build();
    }

    @Transactional
    public void updateStudentSubjectResult(Long id, UpdateStudentSubjectResultDTO dto) {
        StudentSubjectResult entity = subjectResultRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student subject result not found"));
        
        entity.update(dto.getSemesterId(), dto.getAttendanceScore(), dto.getMidtermScore(), dto.getFinalScore(), dto.getScore10(), dto.getScore4(), dto.getLetterGrade(), dto.getIsPass());
        
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
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException(message);
        }
    }

}
