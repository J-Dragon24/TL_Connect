package com.tl_connect.dev.modules.subject.projection;

public interface PrerequisiteRow {
    Long getSubjectId();
    String getSubjectCode();
    Long getGroupId();
    Integer getMinSubjectsRequired();
    Long getPrerequisiteSubjectId();
}
