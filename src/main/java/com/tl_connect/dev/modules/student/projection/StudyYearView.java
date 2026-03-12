package com.tl_connect.dev.modules.student.projection;

import java.time.LocalDate;

public interface StudyYearView {
    LocalDate getStartYear();
    LocalDate getEndYear();
}
