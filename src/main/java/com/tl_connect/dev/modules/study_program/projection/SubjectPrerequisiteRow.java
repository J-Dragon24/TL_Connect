package com.tl_connect.dev.modules.study_program.projection;

public interface SubjectPrerequisiteRow {
    Long getSubjectId();

    Long getPrerequisiteSubjectId();

    String getPrerequisiteSubjectCode();

    String getPrerequisiteSubjectName();
}
