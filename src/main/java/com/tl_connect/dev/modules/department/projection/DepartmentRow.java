package com.tl_connect.dev.modules.department.projection;

public interface DepartmentRow {
    Long getId();
    String getDepartmentCode();
    String getDepartmentName();
    String getFacultyCode();
    Boolean getIsActive();
}
