package com.tl_connect.dev.modules.student_class.projection;

import com.tl_connect.dev.shared.common.enums.Gender;

public interface StudentInClassRow {
    String getStudentCode();
    String getPosition();
    String getFullName();
    Gender getGender();
}
