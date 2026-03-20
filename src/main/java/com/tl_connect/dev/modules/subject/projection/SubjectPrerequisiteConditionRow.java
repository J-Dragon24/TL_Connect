package com.tl_connect.dev.modules.subject.projection;

public interface SubjectPrerequisiteConditionRow {
    Long getId();
    Integer getMinSubjectsRequired();
    String getDescription();
    Integer getPassedCount();
    Integer getTotal();
}
