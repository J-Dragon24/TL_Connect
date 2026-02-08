package com.tl_connect.dev.student_class.projection;

import com.tl_connect.dev.common.enums.Gender;

public interface StudentInClassRow {
    String getStudentCode();
    String getFullName();
    Gender getGender();
}
