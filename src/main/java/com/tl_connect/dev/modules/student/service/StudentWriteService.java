import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.core.common.dto.ImportResultDTO;
import com.tl_connect.dev.core.common.enums.EducationMode;
import com.tl_connect.dev.core.common.enums.StudentStatus;
import com.tl_connect.dev.core.common.enums.Gender;
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
    private final FileParseHelper fileParseHelper;

    public ImportResultDTO importFile(MultipartFile file) throws IOException {
        List<StudentImportDTO> rows = fileParseHelper.parse(file, StudentImportDTO.class);

        List<String> errors = new ArrayList<>();
        int successCount = 0;

        Map<String, Long> majorMap = majorRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Major::getMajorCode, Major::getId));

        Map<String, Long> studentClassMap = studentClassRepository.findAll()
                .stream()
                .collect(Collectors.toMap(StudentClass::getClassCode, StudentClass::getId));

        for (StudentImportDTO row : rows) {
            try {
                validateRow(row, majorMap, studentClassMap);
                insertOneStudent(row, majorMap, studentClassMap);
                successCount++;
            } catch (Exception e) {
                errors.add("StudentCode " + row.getStudentCode() + " : " + e.getMessage());
            }
        }

        if (errors.isEmpty()) {
            return ImportResultDTO.builder()
                    .total(rows.size())
                    .success(successCount)
                    .failed(errors.size())
                    .errors(errors)
                    .build();
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
            Map<String, Long> studentClassMap) {

        if (row.getStudentCode() == null)
            throw new RuntimeException("StudentCode is null");

        if (studentRepository.existsByStudentCode(row.getStudentCode()))
            throw new RuntimeException("Student already exists");

        if (!majorMap.containsKey(row.getMajorCode()))
            throw new RuntimeException("Major not found: " + row.getMajorCode());

        if (!studentClassMap.containsKey(row.getStudentClassCode()))
            throw new RuntimeException("Class not found: " + row.getStudentClassCode());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertOneStudent(StudentImportDTO row,
            Map<String, Long> majorMap,
            Map<String, Long> classMap) {

        Long majorId = majorMap.get(row.getMajorCode());
        Long classId = classMap.get(row.getStudentClassCode());

        Student student = studentRepository.save(
                Student.builder()
                        .studentCode(row.getStudentCode())
                        .fullName(row.getFullName())
                        .gender(row.getGender())
                        .dateOfBirth(row.getDateOfBirth())
                        .studentClassId(classId)
                        .status(StudentStatus.ACTIVE)
                        .build());

        studentContactRepository.save(
                StudentContact.builder()
                        .studentId(student.getId())
                        .phoneNumber(row.getContact().getPhoneNumber())
                        .address(row.getContact().getAddress())
                        .emailPersonal(row.getContact().getEmail())
                        .build());

        emergencyContactRepository.save(
                EmergencyContact.builder()
                        .studentId(student.getId())
                        .fullName(row.getEmergencyContact().getName())
                        .phoneNumber(row.getEmergencyContact().getPhoneNumber())
                        .address(row.getEmergencyContact().getAddress())
                        .relationship(row.getEmergencyContact().getRelationship())
                        .build());

        identityCardRepository.save(
                IdentityCard.builder()
                        .studentId(student.getId())
                        .cardNumber(row.getIdentityCard().getCardNumber())
                        .cardType(row.getIdentityCard().getCardType())
                        .issuedDate(row.getIdentityCard().getIssuedDate())
                        .issuedPlace(row.getIdentityCard().getIssuedPlace())
                        .build());

        StudentMajor studentMajor = studentMajorRepository.save(
                StudentMajor.builder()
                        .studentId(student.getId())
                        .majorId(majorId)
                        .studyProgramId(row.getStudyProgramId())
                        .status(StudentStatus.ACTIVE)
                        .isPrimary(true)
                        .startYear(row.getStartYear())
                        .endYear(row.getEndYear())
                        .build());

        academicInfoRepository.save(
                AcademicInfo.builder()
                        .studentMajorId(studentMajor.getId())
                        .cohort(row.getAcademicInfo().getCohort())
                        .position(row.getAcademicInfo().getPosition())
                        .educationMode(row.getAcademicInfo().getEducationMode())
                        .build());
    }
}
