package com.tl_connect.dev.modules.student_class.service.interfaces;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.student_class.dto.CreateStudentClassDTO;
import com.tl_connect.dev.modules.student_class.dto.StudentClassInfoDTO;
import com.tl_connect.dev.modules.student_class.dto.UpdateStudentClassDTO;
import com.tl_connect.dev.modules.student_class.entity.StudentClass;
import com.tl_connect.dev.modules.student_class.projection.StudentClassRow;
import com.tl_connect.dev.modules.student_class.projection.StudentInClassRow;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

public interface StudentClassService {
    PagedResponse<StudentClassRow> getAll(Pageable pageable, String facultyCode);

    StudentClassInfoDTO getStudentClassInfo(Long id);

    Long create(CreateStudentClassDTO dto);

    void update(Long id, UpdateStudentClassDTO dto);

    void delete(Long id);

    List<StudentInClassRow> findStudentsByClassId(Long classId);

    StudentClass findByClassCode(String classCode);
}
