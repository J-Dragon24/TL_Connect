package com.tl_connect.dev.academic.projection;

public interface SubjectPrerequisiteRow {
    Long getSubjectId();
    Long getPrerequisiteSubjectId();
    String getPrerequisiteSubjectCode();
    String getPrerequisiteSubjectName();
}
