package com.tl_connect.dev.modules.student.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.major.entity.StudentMajor;
import com.tl_connect.dev.modules.major.repository.StudentMajorRepository;
import com.tl_connect.dev.modules.student.dto.ResolvedStudent;
import com.tl_connect.dev.modules.student.entity.Student;
import com.tl_connect.dev.modules.student.repository.AcademicInfoRepository;
import com.tl_connect.dev.modules.student.repository.EmergencyContactRepository;
import com.tl_connect.dev.modules.student.repository.IdentityCardRepository;
import com.tl_connect.dev.modules.student.repository.StudentContactRepository;
import com.tl_connect.dev.modules.student.repository.StudentRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

/**
 * Chứa write transaction của import pipeline trong bean riêng.
 *
 * <p>Tách ra khỏi {@link StudentWriteServiceImpl} để Spring AOP proxy hoạt động đúng
 * với {@code @Transactional} (tránh self-invocation problem).
 *
 * <p>Mỗi lần gọi {@code persistChunk} là 1 transaction độc lập:
 * <ul>
 *   <li>saveAll students → flush (lấy generated IDs)</li>
 *   <li>saveAll contacts / emergency / identity</li>
 *   <li>saveAll studentMajors → flush (lấy generated IDs)</li>
 *   <li>saveAll academicInfo</li>
 *   <li>flush + clear (giải phóng L1 cache)</li>
 * </ul>
 * Nếu chunk thất bại, toàn bộ chunk đó được rollback và các studentCode
 * tương ứng được ghi vào danh sách lỗi ở tầng gọi.
 */
@Service
@RequiredArgsConstructor
public class StudentImportPersistService {

    private final StudentRepository studentRepository;
    private final StudentContactRepository studentContactRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final IdentityCardRepository identityCardRepository;
    private final StudentMajorRepository studentMajorRepository;
    private final AcademicInfoRepository academicInfoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void persistChunk(List<ResolvedStudent> chunk) {

        // 1. Insert students → flush để lấy generated IDs
        List<Student> students = chunk.stream().map(ResolvedStudent::getStudent).toList();
        studentRepository.saveAll(students);
        studentRepository.flush();

        // 2. Set studentId cho các entity phụ thuộc
        for (int i = 0; i < chunk.size(); i++) {
            Long studentId = students.get(i).getId();
            chunk.get(i).getContact().setStudentId(studentId);
            chunk.get(i).getEmergency().setStudentId(studentId);
            chunk.get(i).getIdentity().setStudentId(studentId);
            chunk.get(i).getStudentMajor().setStudentId(studentId);
        }

        // 3. Batch insert contact / emergency / identity
        studentContactRepository.saveAll(chunk.stream().map(ResolvedStudent::getContact).toList());
        emergencyContactRepository.saveAll(chunk.stream().map(ResolvedStudent::getEmergency).toList());
        identityCardRepository.saveAll(chunk.stream().map(ResolvedStudent::getIdentity).toList());

        // 4. Insert student_major → flush để lấy IDs
        List<StudentMajor> majors = chunk.stream().map(ResolvedStudent::getStudentMajor).toList();
        studentMajorRepository.saveAll(majors);
        studentMajorRepository.flush();

        // 5. Set studentMajorId cho academic_info
        for (int i = 0; i < chunk.size(); i++) {
            chunk.get(i).getAcademicInfo().setStudentMajorId(majors.get(i).getId());
        }

        // 6. Batch insert academic_info
        academicInfoRepository.saveAll(chunk.stream().map(ResolvedStudent::getAcademicInfo).toList());

        // 7. Flush + clear: giải phóng L1 cache trước chunk tiếp theo
        entityManager.flush();
        entityManager.clear();
    }
}
