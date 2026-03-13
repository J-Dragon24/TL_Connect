// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.List;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Propagation;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.multipart.MultipartFile;

// import com.tl_connect.dev.core.common.dto.ImportResultDTO;
// import com.tl_connect.dev.core.common.enums.EducationMode;
// import com.tl_connect.dev.core.common.enums.StudentStatus;
// import com.tl_connect.dev.modules.student.StudentRepository;
// import com.tl_connect.dev.modules.student.dto.StudentImportDTO;
// import com.tl_connect.dev.modules.student.entity.AcademicInfo;
// import com.tl_connect.dev.modules.student.entity.EmergencyContact;
// import com.tl_connect.dev.modules.student.entity.IdentityCard;
// import com.tl_connect.dev.modules.student.entity.Student;
// import com.tl_connect.dev.modules.student.entity.StudentContact;
// import com.tl_connect.dev.modules.study_program.entity.StudentMajor;
// import com.tl_connect.dev.core.common.ultility.importer.FileParseHelper;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class StudentCommandService {

//     private final StudentRepository studentRepository;
//     private final StudentCommandService self;
//     private final FileParseHelper fileParseHelper;

//     public ImportResultDTO importFile(MultipartFile file) throws IOException {
//         List<StudentImportDTO> rows = fileParseHelper.parse(file, row -> 
//             StudentImportDTO.builder()
//             .studentCode(row.)
//             .studentClassCode(null));

//         List<String> errors = new ArrayList<>();
//         int successCount = 0;

//         for (ImportStudentRow row : rows) {
//             try {
//                 self.insertOneStudent(row);
//                 successCount++;
//             } catch (Exception e) {
//                 errors.add(row.getStudentCode() + ": " + e.getMessage());
//             }
//         }

//         return ImportResultDTO.builder()
//             .total(rows.size())
//             .success(successCount)
//             .failed(errors.size())
//             .errors(errors)
//             .build();
//     }

//     @Transactional(propagation = Propagation.REQUIRES_NEW)
//     public void insertOneStudent(StudentImportDTO row) {
//         // 1. Student
//         Student student = studentRepository.save(Student.builder()
//             .studentCode(row.getStudentCode())
//             .fullName(row.getFullName())
//             .gender(Gender.valueOf(row.getGender()))
//             .dateOfBirth(parseDateTime(row.getDateOfBirth()))
//             .studentClassId(Long.parseLong(row.getStudentClassId()))
//             .status(StudentStatus.ACTIVE)
//             .build());

//         // 2. StudentContact
//         studentContactRepository.save(StudentContact.builder()
//             .studentId(student.getId())
//             .phoneNumber(row.getPhoneNumber())
//             .address(row.getAddress())
//             .emailPersonal(row.getEmailPersonal())
//             .build());

//         // 3. EmergencyContact
//         emergencyContactRepository.save(EmergencyContact.builder()
//             .studentId(student.getId())
//             .fullName(row.getEmergencyFullName())
//             .phoneNumber(row.getEmergencyPhone())
//             .address(row.getEmergencyAddress())
//             .relationship(row.getEmergencyRelationship())
//             .build());

//         // 4. IdentityCard
//         identityCardRepository.save(IdentityCard.builder()
//             .studentId(student.getId())
//             .cardNumber(row.getCardNumber())
//             .cardType(CardType.valueOf(row.getCardType()))
//             .issuedDate(parseDate(row.getIssuedDate()))
//             .issuedPlace(row.getIssuedPlace())
//             .build());

//         // 5. StudentMajor + AcademicInfo
//         StudentMajor major = studentMajorRepository.save(StudentMajor.builder()
//             .studentId(student.getId())
//             .majorId(Long.parseLong(row.getMajorId()))
//             .studyProgramId(Long.parseLong(row.getStudyProgramId()))
//             .status(MajorStatus.STUDYING)
//             .isPrimary(true)
//             .startYear(parseDate(row.getStartYear()))
//             .endYear(parseDate(row.getEndYear()))
//             .build());

//         academicInfoRepository.save(AcademicInfo.builder()
//             .studentMajorId(major.getId())
//             .cohort(row.getCohort())
//             .position(row.getPosition())
//             .educationMode(EducationMode.valueOf(row.getEducationMode()))
//             .build());
//     }
// }
