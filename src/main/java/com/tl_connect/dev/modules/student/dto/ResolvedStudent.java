package com.tl_connect.dev.modules.student.dto;

import com.tl_connect.dev.modules.major.entity.StudentMajor;
import com.tl_connect.dev.modules.student.entity.AcademicInfo;
import com.tl_connect.dev.modules.student.entity.EmergencyContact;
import com.tl_connect.dev.modules.student.entity.IdentityCard;
import com.tl_connect.dev.modules.student.entity.Student;
import com.tl_connect.dev.modules.student.entity.StudentContact;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResolvedStudent {
    private Student student;
    private StudentContact contact;
    private EmergencyContact emergency;
    private IdentityCard identity;
    private StudentMajor studentMajor;
    private AcademicInfo academicInfo;
}
