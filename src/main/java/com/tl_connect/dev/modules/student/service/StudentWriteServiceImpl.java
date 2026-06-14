package com.tl_connect.dev.modules.student.service;

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

import com.tl_connect.dev.modules.major.entity.Major;
import com.tl_connect.dev.modules.major.entity.StudentMajor;
import com.tl_connect.dev.modules.major.repository.MajorRepository;
import com.tl_connect.dev.modules.major.repository.StudentMajorRepository;
import com.tl_connect.dev.modules.student.dto.ResolvedStudent;
import com.tl_connect.dev.modules.student.dto.StudentImportDTO;
import com.tl_connect.dev.modules.student.entity.AcademicInfo;
import com.tl_connect.dev.modules.student.entity.EmergencyContact;
import com.tl_connect.dev.modules.student.entity.IdentityCard;
import com.tl_connect.dev.modules.student.entity.Student;
import com.tl_connect.dev.modules.student.entity.StudentContact;
import com.tl_connect.dev.modules.student.repository.AcademicInfoRepository;
import com.tl_connect.dev.modules.student.repository.EmergencyContactRepository;
import com.tl_connect.dev.modules.student.repository.IdentityCardRepository;
import com.tl_connect.dev.modules.student.repository.StudentContactRepository;
import com.tl_connect.dev.modules.student.repository.StudentRepository;
import com.tl_connect.dev.modules.student.service.interfaces.StudentWriteService;
import com.tl_connect.dev.modules.student_class.StudentClassRepository;
import com.tl_connect.dev.modules.student_class.entity.StudentClass;
import com.tl_connect.dev.modules.study_program.entity.StudyProgram;
import com.tl_connect.dev.modules.study_program.repository.StudyProgramRepository;
import com.tl_connect.dev.shared.common.dto.ImportResultDTO;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.enums.StudentMajorStatus;
import com.tl_connect.dev.shared.common.enums.StudentStatus;
import com.tl_connect.dev.shared.common.exception.ConflictException;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.shared.ultility.FileProcess.FileParseHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentWriteServiceImpl implements StudentWriteService {

    private final StudentRepository studentRepository;
    private final StudentContactRepository studentContactRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final IdentityCardRepository identityCardRepository;
    private final StudentMajorRepository studentMajorRepository;
    private final MajorRepository majorRepository;
    private final StudentClassRepository studentClassRepository;
    private final AcademicInfoRepository academicInfoRepository;
    private final StudyProgramRepository studyProgramRepository;
    private final FileParseHelper fileParseHelper;
    private final StudentImportPersistService persistService;

    @Transactional
    public Long createStudent(StudentImportDTO dto) {
        Major major = majorRepository.findByMajorCode(dto.getMajorCode())
                .orElseThrow(() -> new NotFoundException("Major not found"));

        StudentClass clazz = studentClassRepository.findByClassCode(dto.getStudentClassCode())
                .orElseThrow(() -> new NotFoundException("Student class not found"));

        if (!clazz.getMajorId().equals(major.getId())) {
            throw new InvalidInputException("Class does not belong to major");
        }

        Long studyProgramId = studyProgramRepository
                .findByMajorIdAndTrainingTypeAndStartYear(major.getId(), dto.getTrainingType(), dto.getStartYear())
                .map(StudyProgram::getId)
                .orElseThrow(() -> new NotFoundException("Study program not found"));

        try {
            return saveStudentEntities(dto, major.getId(), clazz.getId(), studyProgramId);
        } catch (DataIntegrityViolationException e) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR, "Failed to create student: " + e.getMessage());
        }
    }

    public ImportResultDTO importFile(MultipartFile file) throws IOException {

        // ── Stage 1: Parse + Validate (no transaction) ──────────────────────
        List<StudentImportDTO> rows = fileParseHelper.parse(file, StudentImportDTO.class);

        // Batch-load all lookup data (5 queries total, no per-row queries)
        Set<String> majorCodes = rows.stream()
                .map(StudentImportDTO::getMajorCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<String> classCodes = rows.stream()
                .map(StudentImportDTO::getStudentClassCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Integer> startYears = rows.stream()
                .map(StudentImportDTO::getStartYear)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<String> importCodes = rows.stream()
                .map(StudentImportDTO::getStudentCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Long> majorMap = majorRepository.findByMajorCodeIn(majorCodes)
                .stream()
                .collect(Collectors.toMap(Major::getMajorCode, Major::getId));

        Map<String, Long> studentClassMap = studentClassRepository.findByClassCodeIn(classCodes)
                .stream()
                .collect(Collectors.toMap(StudentClass::getClassCode, StudentClass::getId));

        Map<String, Long> studyProgramMap = studyProgramRepository.findByStartYearIn(startYears)
                .stream()
                .collect(Collectors.toMap(
                        sp -> sp.getMajorId() + "|" + sp.getTrainingType() + "|" + sp.getStartYear(),
                        sp -> sp.getId()));

        Set<String> existingCodes = studentRepository.findExistingStudentCodes(importCodes);

        // Validate each row in-memory (no DB calls inside the loop)
        Set<String> failedCodes = new HashSet<>();
        List<ResolvedStudent> validList = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (StudentImportDTO row : rows) {
            try {
                if (!seen.add(row.getStudentCode())) {
                    log.warn("Duplicate studentCode in file: {}", row.getStudentCode());
                    failedCodes.add(row.getStudentCode());
                    continue;
                }
                validateRow(row, majorMap, studentClassMap, studyProgramMap, existingCodes);
                validList.add(resolve(row, majorMap, studentClassMap, studyProgramMap));
            } catch (Exception e) {
                log.warn("Validation failed for [{}]: {}", row.getStudentCode(), e.getMessage());
                failedCodes.add(row.getStudentCode());
            }
        }

        // ── Stage 2: Batch persist (1 TX per chunk) ─────────────────────────
        int successCount = 0;
        int batchSize = 50;

        for (int i = 0; i < validList.size(); i += batchSize) {
            List<ResolvedStudent> chunk = validList.subList(i, Math.min(i + batchSize, validList.size()));
            try {
                persistService.persistChunk(chunk);
                successCount += chunk.size();
            } catch (Exception e) {
                log.error("Batch persist failed for chunk [{}-{}]: {}",
                        i, i + chunk.size() - 1, e.getMessage());
                chunk.forEach(rs -> failedCodes.add(rs.getStudent().getStudentCode()));
            }
        }

        return ImportResultDTO.builder()
                .total(rows.size())
                .success(successCount)
                .failed(failedCodes.size())
                .build();
    }

    // -------------------------------------------------------------------------
    // Validation — pure in-memory, no DB calls
    // -------------------------------------------------------------------------

    private void validateRow(
            StudentImportDTO row,
            Map<String, Long> majorMap,
            Map<String, Long> studentClassMap,
            Map<String, Long> studyProgramMap,
            Set<String> existingCodes) {

        if (row.getStudentCode() == null || row.getStudentCode().isBlank())
            throw new NotFoundException("StudentCode is null");

        if (existingCodes.contains(row.getStudentCode()))
            throw new ConflictException("Student already exists");

        if (!majorMap.containsKey(row.getMajorCode()))
            throw new NotFoundException("Major not found: " + row.getMajorCode());

        if (!studentClassMap.containsKey(row.getStudentClassCode()))
            throw new NotFoundException("Class not found: " + row.getStudentClassCode());

        Long majorId = majorMap.get(row.getMajorCode());
        String key = majorId + "|" + row.getTrainingType() + "|" + row.getStartYear();

        if (!studyProgramMap.containsKey(key))
            throw new NotFoundException("Study program not found: " + key);
    }

    // -------------------------------------------------------------------------
    // Resolve — build entities in-memory, no DB calls
    // -------------------------------------------------------------------------

    private ResolvedStudent resolve(
            StudentImportDTO dto,
            Map<String, Long> majorMap,
            Map<String, Long> classMap,
            Map<String, Long> studyProgramMap) {

        Long majorId = majorMap.get(dto.getMajorCode());
        Long classId = classMap.get(dto.getStudentClassCode());
        Long studyProgramId = studyProgramMap.get(majorId + "|" + dto.getTrainingType() + "|" + dto.getStartYear());

        return ResolvedStudent.builder()
                .student(Student.builder()
                        .studentCode(dto.getStudentCode())
                        .fullName(dto.getFullName())
                        .gender(dto.getGender())
                        .dateOfBirth(dto.getDateOfBirth())
                        .studentClassId(classId)
                        .status(StudentStatus.ACTIVE)
                        .build())
                .contact(StudentContact.builder()
                        .phoneNumber(dto.getContact().getPhoneNumber())
                        .address(dto.getContact().getAddress())
                        .emailPersonal(dto.getContact().getEmail())
                        .build())
                .emergency(EmergencyContact.builder()
                        .fullName(dto.getEmergencyContact().getName())
                        .phoneNumber(dto.getEmergencyContact().getPhoneNumber())
                        .address(dto.getEmergencyContact().getAddress())
                        .relationship(dto.getEmergencyContact().getRelationship())
                        .build())
                .identity(IdentityCard.builder()
                        .cardNumber(dto.getIdentityCard().getCardNumber())
                        .cardType(dto.getIdentityCard().getCardType())
                        .issuedDate(dto.getIdentityCard().getIssuedDate())
                        .issuedPlace(dto.getIdentityCard().getIssuedPlace())
                        .build())
                .studentMajor(StudentMajor.builder()
                        .majorId(majorId)
                        .studyProgramId(studyProgramId)
                        .isPrimary(true)
                        .startYear(dto.getStartYear())
                        .endYear(dto.getEndYear())
                        .status(StudentMajorStatus.STUDYING)
                        .build())
                .academicInfo(AcademicInfo.builder()
                        .cohort(dto.getAcademicInfo().getCohort())
                        .position(dto.getAcademicInfo().getPosition())
                        .build())
                .build();
    }

    // -------------------------------------------------------------------------
    // Single-record persist (used only by createStudent endpoint)
    // -------------------------------------------------------------------------

    private Long saveStudentEntities(StudentImportDTO dto, Long majorId, Long classId, Long studyProgramId) {
        Student student = studentRepository.save(Student.builder()
                .studentCode(dto.getStudentCode())
                .fullName(dto.getFullName())
                .gender(dto.getGender())
                .dateOfBirth(dto.getDateOfBirth())
                .studentClassId(classId)
                .status(StudentStatus.ACTIVE)
                .build());

        studentContactRepository.save(StudentContact.builder()
                .studentId(student.getId())
                .phoneNumber(dto.getContact().getPhoneNumber())
                .address(dto.getContact().getAddress())
                .emailPersonal(dto.getContact().getEmail())
                .build());

        emergencyContactRepository.save(EmergencyContact.builder()
                .studentId(student.getId())
                .fullName(dto.getEmergencyContact().getName())
                .phoneNumber(dto.getEmergencyContact().getPhoneNumber())
                .address(dto.getEmergencyContact().getAddress())
                .relationship(dto.getEmergencyContact().getRelationship())
                .build());

        identityCardRepository.save(IdentityCard.builder()
                .studentId(student.getId())
                .cardNumber(dto.getIdentityCard().getCardNumber())
                .cardType(dto.getIdentityCard().getCardType())
                .issuedDate(dto.getIdentityCard().getIssuedDate())
                .issuedPlace(dto.getIdentityCard().getIssuedPlace())
                .build());

        StudentMajor studentMajor = studentMajorRepository.save(StudentMajor.builder()
                .studentId(student.getId())
                .majorId(majorId)
                .studyProgramId(studyProgramId)
                .isPrimary(true)
                .startYear(dto.getStartYear())
                .endYear(dto.getEndYear())
                .status(StudentMajorStatus.STUDYING)
                .build());

        academicInfoRepository.save(AcademicInfo.builder()
                .studentMajorId(studentMajor.getId())
                .cohort(dto.getAcademicInfo().getCohort())
                .position(dto.getAcademicInfo().getPosition())
                .build());

        return student.getId();
    }
}
