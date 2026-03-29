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
import com.tl_connect.dev.core.common.enums.StudentMajorStatus;
import com.tl_connect.dev.core.common.enums.StudentStatus;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.student.dto.ResolvedStudent;
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
import com.tl_connect.dev.modules.study_program.entity.StudyProgram;
import com.tl_connect.dev.modules.study_program.repository.StudyProgramRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import com.tl_connect.dev.core.common.exception.ConflictException;
import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.exception.BadRequestException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        
        @PersistenceContext
        private EntityManager entityManager;

        @Transactional
        public Long createStudent(StudentImportDTO dto) {

                Set<ConstraintViolation<StudentImportDTO>> violations = validator.validate(dto);
                if (!violations.isEmpty()) {
                        String message = violations.stream()
                                        .map(ConstraintViolation::getMessage)
                                        .collect(Collectors.joining(", "));
                        throw new InvalidInputException(message);
                }

                Major major = majorRepository.findByMajorCode(dto.getMajorCode())
                        .orElseThrow(() -> new NotFoundException("Major not found"));
                
                StudentClass clazz = studentClassRepository.findByClassCode(dto.getStudentClassCode())
                        .orElseThrow(() -> new NotFoundException("Student class not found"));

                if (!clazz.getMajorId().equals(major.getId())) {
                    throw new InvalidInputException("Class does not belong to major");
                }
                
                Long studyProgramId = studyProgramRepository.findByMajorIdAndTrainingTypeAndStartYear(
                        major.getId(), dto.getTrainingType(), dto.getStartYear())
                        .map(StudyProgram::getId)
                        .orElseThrow(() -> new NotFoundException("Study program not found"));
                try {
                        return saveStudentEntities(dto, major.getId(), clazz.getId(), studyProgramId);
                } catch (DataIntegrityViolationException e) {
                        throw new BadRequestException("Failed to create student: " + e.getMessage());
                }
        }

        @Transactional
        public ImportResultDTO importFile(MultipartFile file) throws IOException {
                List<StudentImportDTO> rows = fileParseHelper.parse(file, StudentImportDTO.class);

                Set<String> failedCodes = new HashSet<>();
                List<ResolvedStudent> validList = new ArrayList<>();
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

                Set<Integer> startYears = rows.stream()
                        .map(StudentImportDTO::getStartYear)
                        .collect(Collectors.toSet());

                Map<String, Long> studyProgramMap = studyProgramRepository.findByStartYearIn(startYears)
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
                                        log.error("Duplicate studentCode in file: {}", row.getStudentCode());
                                        failedCodes.add(row.getStudentCode());
                                        continue;
                                }

                                validateRow(row, majorMap, studentClassMap, studyProgramMap, existingCodes);

                                ResolvedStudent resolved = resolve(row, majorMap, studentClassMap, studyProgramMap);
                                
                                validList.add(resolved);
                        } catch (Exception e) {
                                log.error("Error validating row: {}", row.getStudentCode(), e);
                                failedCodes.add(row.getStudentCode());
                        }
                }

                int batchSize = 50;
                
                for(int i = 0; i < validList.size(); i+= batchSize){
                    List<ResolvedStudent> batch = validList.subList(i, Math.min(i + batchSize, validList.size()));
                    try {
                        saveBatch(batch);
                        successCount += batch.size();
                    } catch (DataIntegrityViolationException e) {
                        for (ResolvedStudent rs : batch) {
                            try {
                                saveSingle(rs);
                                successCount++;
                            } catch (DataIntegrityViolationException ex) {
                                log.error("Error saving single student: {}", rs.getStudent().getStudentCode(), ex);
                                failedCodes.add(rs.getStudent().getStudentCode());
                            }
                        }
                    }
                }

                return ImportResultDTO.builder()
                        .total(rows.size())
                        .success(successCount)
                        .failed(failedCodes.size())
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
                        throw new ConflictException("Student already exists");

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

        private ResolvedStudent resolve(StudentImportDTO dto,
                Map<String, Long> majorMap,
                Map<String, Long> classMap,
                Map<String, Long> studyProgramMap) {
                
                Long majorId = majorMap.get(dto.getMajorCode());
                Long classId = classMap.get(dto.getStudentClassCode());

                String key = majorId + "|" +
                        dto.getTrainingType() + "|" +
                        dto.getStartYear();
                
                Long studyProgramId = studyProgramMap.get(key);
                
                Student student = Student.builder()
                                .studentCode(dto.getStudentCode())
                                .fullName(dto.getFullName())
                                .gender(dto.getGender())
                                .dateOfBirth(dto.getDateOfBirth())
                                .studentClassId(classId)
                                .status(StudentStatus.ACTIVE)
                                .build();

                StudentContact contact = StudentContact.builder()
                                .phoneNumber(dto.getContact().getPhoneNumber())
                                .address(dto.getContact().getAddress())
                                .emailPersonal(dto.getContact().getEmail())
                                .build();

                EmergencyContact emergencyContact = EmergencyContact.builder()
                                .fullName(dto.getEmergencyContact().getName())
                                .phoneNumber(dto.getEmergencyContact().getPhoneNumber())
                                .address(dto.getEmergencyContact().getAddress())
                                .relationship(dto.getEmergencyContact().getRelationship())
                                .build();

                IdentityCard identityCard = IdentityCard.builder()
                                .cardNumber(dto.getIdentityCard().getCardNumber())
                                .cardType(dto.getIdentityCard().getCardType())
                                .issuedDate(dto.getIdentityCard().getIssuedDate())
                                .issuedPlace(dto.getIdentityCard().getIssuedPlace())
                                .build();

                StudentMajor studentMajor = StudentMajor.builder()
                                .majorId(majorId)
                                .studyProgramId(studyProgramId)
                                .isPrimary(true)
                                .startYear(dto.getStartYear())
                                .endYear(dto.getEndYear())
                                .status(StudentMajorStatus.STUDYING)
                                .build();

                AcademicInfo academicInfo = AcademicInfo.builder()
                                .cohort(dto.getAcademicInfo().getCohort())
                                .position(dto.getAcademicInfo().getPosition())
                                .build();

                return ResolvedStudent.builder()
                                .student(student)
                                .contact(contact)
                                .emergency(emergencyContact)
                                .identity(identityCard)
                                .studentMajor(studentMajor)
                                .academicInfo(academicInfo)
                                .build();
        }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void saveBatch(List<ResolvedStudent> batch) {
                List<Student> students = batch.stream()
                .map(ResolvedStudent::getStudent)
                .toList();

                studentRepository.saveAll(students);
                studentRepository.flush();

                for (int i = 0; i < batch.size(); i++) {
                        Long studentId = students.get(i).getId();

                        batch.get(i).getContact().setStudentId(studentId);
                        batch.get(i).getEmergency().setStudentId(studentId);
                        batch.get(i).getIdentity().setStudentId(studentId);
                        batch.get(i).getStudentMajor().setStudentId(studentId);
                }

                studentContactRepository.saveAll(
                        batch.stream().map(ResolvedStudent::getContact).toList());

                emergencyContactRepository.saveAll(
                        batch.stream().map(ResolvedStudent::getEmergency).toList());

                identityCardRepository.saveAll(
                        batch.stream().map(ResolvedStudent::getIdentity).toList());

                List<StudentMajor> majors = batch.stream()
                        .map(ResolvedStudent::getStudentMajor)
                        .toList();
                studentMajorRepository.saveAll(majors);
                studentMajorRepository.flush();

                for (int i = 0; i < batch.size(); i++) {
                        batch.get(i).getAcademicInfo().setStudentMajorId(majors.get(i).getId());
                }

                academicInfoRepository.saveAll(
                        batch.stream().map(ResolvedStudent::getAcademicInfo).toList());

                entityManager.flush();
                entityManager.clear();
        }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void saveSingle(ResolvedStudent rs) {

                Student student = studentRepository.save(rs.getStudent());

                rs.getContact().setStudentId(student.getId());
                rs.getEmergency().setStudentId(student.getId());
                rs.getIdentity().setStudentId(student.getId());
                rs.getStudentMajor().setStudentId(student.getId());

                studentContactRepository.save(rs.getContact());
                emergencyContactRepository.save(rs.getEmergency());
                identityCardRepository.save(rs.getIdentity());

                StudentMajor major = studentMajorRepository.save(rs.getStudentMajor());

                rs.getAcademicInfo().setStudentMajorId(major.getId());

                academicInfoRepository.save(rs.getAcademicInfo());

                entityManager.flush();
                entityManager.clear();
        }

        private Long saveStudentEntities(StudentImportDTO dto, Long majorId, Long classId, Long studyProgramId) { 
                Student student = studentRepository.save( Student.builder() 
                        .studentCode(dto.getStudentCode()) 
                        .fullName(dto.getFullName()) 
                        .gender(dto.getGender()) 
                        .dateOfBirth(dto.getDateOfBirth()) 
                        .studentClassId(classId) 
                        .status(StudentStatus.ACTIVE) 
                        .build()); 
                
                studentContactRepository.save( StudentContact.builder() 
                        .studentId(student.getId()) 
                        .phoneNumber(dto.getContact().getPhoneNumber()) 
                        .address(dto.getContact().getAddress()) 
                        .emailPersonal(dto.getContact().getEmail()) 
                        .build()); 
                
                emergencyContactRepository.save( EmergencyContact.builder() 
                        .studentId(student.getId()) 
                        .fullName(dto.getEmergencyContact().getName()) 
                        .phoneNumber(dto.getEmergencyContact().getPhoneNumber()) 
                        .address(dto.getEmergencyContact().getAddress()) 
                        .relationship(dto.getEmergencyContact().getRelationship())
                        .build()); 
                
                identityCardRepository.save( IdentityCard.builder() 
                        .studentId(student.getId()) 
                        .cardNumber(dto.getIdentityCard().getCardNumber()) 
                        .cardType(dto.getIdentityCard().getCardType()) 
                        .issuedDate(dto.getIdentityCard().getIssuedDate()) 
                        .issuedPlace(dto.getIdentityCard().getIssuedPlace()) 
                        .build()); 
                
                StudentMajor studentMajor = studentMajorRepository.save( StudentMajor.builder() 
                        .studentId(student.getId()) 
                        .majorId(majorId) 
                        .studyProgramId(studyProgramId) 
                        .isPrimary(true) 
                        .startYear(dto.getStartYear()) 
                        .endYear(dto.getEndYear()) 
                        .status(StudentMajorStatus.STUDYING) 
                        .build()); 
                
                academicInfoRepository.save( AcademicInfo.builder() 
                        .studentMajorId(studentMajor.getId()) 
                        .cohort(dto.getAcademicInfo().getCohort()) 
                        .position(dto.getAcademicInfo().getPosition()) 
                        .build()); 
                
                return student.getId();
        }
}
