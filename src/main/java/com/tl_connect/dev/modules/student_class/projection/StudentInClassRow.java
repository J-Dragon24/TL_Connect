package com.tl_connect.dev.modules.student_class.projection;

import com.tl_connect.dev.core.common.enums.Gender;

public interface StudentInClassRow {
    String getStudentCode();
    String getPosition();
    String getFullName();
    Gender getGender();
}
