package com.tl_connect.dev.modules.student.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.core.common.dto.ImportResultDTO;
import com.tl_connect.dev.core.common.enums.StudentStatus;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.student.dto.StudentImportDTO;
import com.tl_connect.dev.modules.student.entity.AcademicInfo;
import com.tl_connect.dev.modules.student.entity.EmergencyContact;
import com.tl_connect.dev.modules.student.entity.IdentityCard;
import com.tl_connect.dev.modules.student.entity.Student;
import com.tl_connect.dev.modules.student.entity.StudentContact;
import com.tl_connect.dev.modules.student.repository.StudentContactRepository;
import com.tl_connect.dev.modules.student.repository.StudentRepository;
import com.tl_connect.dev.modules.student_class.StudentClassRepository;
import com.tl_connect.dev.modules.student_class.entity.StudentClass;
import com.tl_connect.dev.modules.study_program.StudyProgramRepository;
import com.tl_connect.dev.modules.study_program.entity.StudyProgram;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import com.tl_connect.dev.core.common.ultility.importer.FileParseHelper;
import com.tl_connect.dev.modules.student.repository.AcademicInfoRepository;
import com.tl_connect.dev.modules.student.repository.EmergencyContactRepository;
import com.tl_connect.dev.modules.student.repository.IdentityCardRepository;
import com.tl_connect.dev.modules.major.entity.Major;
import com.tl_connect.dev.modules.major.entity.StudentMajor;
import com.tl_connect.dev.modules.major.repository.MajorRepository;
import com.tl_connect.dev.modules.major.repository.StudentMajorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentWriteService {


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
        private final Validator validator;

        @Transactional
        public Long createStudent(StudentImportDTO dto) {
                if(studentRepository.existsByStudentCode(dto.getStudentCode())) {
                        throw new RuntimeException("Student code already exists");
                }

                Major major = majorRepository.findByMajorCode(dto.getMajorCode())
                        .orElseThrow(() -> new NotFoundException("Major not found"));
                
                Long classId = studentClassRepository.findByClassCode(dto.getStudentClassCode())
                        .map(StudentClass::getId)
                        .orElseThrow(() -> new NotFoundException("Student class not found"));
                
                Long studyProgramId = studyProgramRepository.findByMajorIdAndTrainingTypeAndStartYear(
                        major.getId(), dto.getTrainingType(), dto.getStartYear())
                        .map(StudyProgram::getId)
                        .orElseThrow(() -> new NotFoundException("Study program not found"));
                
                return saveStudentEntities(dto, major.getId(), classId, studyProgramId);
        }

        public ImportResultDTO importFile(MultipartFile file) throws IOException {
                List<StudentImportDTO> rows = fileParseHelper.parse(file, StudentImportDTO.class);

                List<String> errors = new ArrayList<>();
                int successCount = 0;

                Set<String> majorCodes = rows.stream()
                        .map(StudentImportDTO::getMajorCode)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
        
                Set<String> classCodes = rows.stream()
                        .map(StudentImportDTO::getStudentClassCode)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                Map<String, Long> majorMap = majorRepository.findByMajorCodeIn(majorCodes)
                        .stream()
                        .collect(Collectors.toMap(Major::getMajorCode, Major::getId));
        
                Map<String, Long> studentClassMap = studentClassRepository.findByClassCodeIn(classCodes)
                        .stream()
                        .collect(Collectors.toMap(StudentClass::getClassCode, StudentClass::getId));

                Map<String, Long> studyProgramMap = studyProgramRepository.findAll()
                        .stream()
                        .collect(Collectors.toMap(
                                sp -> sp.getMajorId() + "|" + sp.getTrainingType() + "|" + sp.getStartYear(),
                                sp -> sp.getId()));

                Set<String> importCodes = rows.stream()
                        .map(StudentImportDTO::getStudentCode)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                Set<String> existingCodes = studentRepository.findExistingStudentCodes(importCodes);

                Set<String> seen = new HashSet<>();

                for (StudentImportDTO row : rows) {
                        try {
                                if (!seen.add(row.getStudentCode())) {
                                errors.add("Duplicate studentCode in file: " + row.getStudentCode());
                                continue;
                                }
                                validateRow(row, majorMap, studentClassMap, studyProgramMap, existingCodes);
                                insertOneStudent(row, majorMap, studentClassMap, studyProgramMap);
                                successCount++;
                        } catch (DataIntegrityViolationException e) {
                                errors.add("StudentCode " + row.getStudentCode()
                                        + ": đã tồn tại (có thể do import đồng thời)");
                        } catch (Exception e) {
                                errors.add("StudentCode " + row.getStudentCode() + ": " + e.getMessage());
                        }
                }

                return ImportResultDTO.builder()
                        .total(rows.size())
                        .success(successCount)
                        .failed(errors.size())
                        .errors(errors)
                        .build();
        }

        private void validateRow(StudentImportDTO row,
                Map<String, Long> majorMap,
                Map<String, Long> studentClassMap,
                Map<String, Long> studyProgramMap,
                Set<String> existingCodes) {

                Set<ConstraintViolation<StudentImportDTO>> violations = validator.validate(row);
                if (!violations.isEmpty()) {
                        String message = violations.stream()
                                        .map(ConstraintViolation::getMessage)
                                        .collect(Collectors.joining(", "));
                        throw new IllegalArgumentException(message);
                }

                if (row.getStudentCode() == null || row.getStudentCode().isBlank())
                throw new NotFoundException("StudentCode is null");

                if (existingCodes.contains(row.getStudentCode()))
                throw new RuntimeException("Student already exists");

                if (!majorMap.containsKey(row.getMajorCode()))
                throw new NotFoundException("Major not found: " + row.getMajorCode());

                if (!studentClassMap.containsKey(row.getStudentClassCode()))
                throw new NotFoundException("Class not found: " + row.getStudentClassCode());

                Long majorId = majorMap.get(row.getMajorCode());

                String key = majorId + "|" +
                        row.getTrainingType() + "|" +
                        row.getStartYear();

                if (!studyProgramMap.containsKey(key))
                throw new NotFoundException("Study program not found: " + key);
        }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void insertOneStudent(StudentImportDTO row,
                Map<String, Long> majorMap,
                Map<String, Long> classMap,
                Map<String, Long> studyProgramMap) {

                Long majorId = majorMap.get(row.getMajorCode());
                Long classId = classMap.get(row.getStudentClassCode());

                String key = majorId + "|" +
                        row.getTrainingType() + "|" +
                        row.getStartYear();

                Long studyProgramId = studyProgramMap.get(key);

                saveStudentEntities(row, majorId, classId, studyProgramId);
        }

        private Long saveStudentEntities(StudentImportDTO dto, Long majorId, Long classId, Long studyProgramId) {
                Student student = studentRepository.save(
                        Student.builder()
                                .studentCode(dto.getStudentCode())
                                .fullName(dto.getFullName())
                                .gender(dto.getGender())
                                .dateOfBirth(dto.getDateOfBirth())
                                .studentClassId(classId)
                                .status(StudentStatus.ACTIVE)
                                .build());

                studentContactRepository.save(
                        StudentContact.builder()
                                .studentId(student.getId())
                                .phoneNumber(dto.getContact().getPhoneNumber())
                                .address(dto.getContact().getAddress())
                                .emailPersonal(dto.getContact().getEmail())
                                .build());

                emergencyContactRepository.save(
                        EmergencyContact.builder()
                                .studentId(student.getId())
                                .fullName(dto.getEmergencyContact().getName())
                                .phoneNumber(dto.getEmergencyContact().getPhoneNumber())
                                .address(dto.getEmergencyContact().getAddress())
                                .relationship(dto.getEmergencyContact().getRelationship())
                                .build());

                identityCardRepository.save(
                        IdentityCard.builder()
                                .studentId(student.getId())
                                .cardNumber(dto.getIdentityCard().getCardNumber())
                                .cardType(dto.getIdentityCard().getCardType())
                                .issuedDate(dto.getIdentityCard().getIssuedDate())
                                .issuedPlace(dto.getIdentityCard().getIssuedPlace())
                                .build());

                StudentMajor studentMajor = studentMajorRepository.save(
                        StudentMajor.builder()
                                .studentId(student.getId())
                                .majorId(majorId)
                                .studyProgramId(studyProgramId)
                                .isPrimary(true)
                                .startYear(dto.getStartYear())
                                .endYear(dto.getEndYear())
                                .build());

                academicInfoRepository.save(
                        AcademicInfo.builder()
                                .studentMajorId(studentMajor.getId())
                                .cohort(dto.getAcademicInfo().getCohort())
                                .position(dto.getAcademicInfo().getPosition())
                                .build());

                return student.getId();
        }
}
