package com.tl_connect.dev.modules.student.service.interfaces;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.student.dto.StudentFullInfo;
import com.tl_connect.dev.modules.student.dto.StudentInfoDTO;
import com.tl_connect.dev.modules.student.dto.YearStudyDTO;
import com.tl_connect.dev.modules.chatbot.projection.AIContextView;
import com.tl_connect.dev.modules.student.dto.HealthInsDTO;
import com.tl_connect.dev.modules.student.entity.Student;
import com.tl_connect.dev.modules.student_class.dto.StudentClassInfoDTO;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

public interface StudentService {

    PagedResponse<StudentFullInfo> getAllStudents(Pageable pageable, String facultyCode);

    StudentInfoDTO getStudentInfo(Long id);

    StudentClassInfoDTO getStudentClassInfo(Long id);

    HealthInsDTO getHealthInsurance(Long id);

    YearStudyDTO getYearStudy(Long id);

    boolean existsStudent(Long studentId);

    List<Student> findByStudentCodeIn(Set<String> studentCodes);

    Student findById(Long studentId);

    Student findByStudentCode(String studentCode);

    Student save(Student student);

    List<AIContextView> findAIContextByStudentId(Long studentId);
}
