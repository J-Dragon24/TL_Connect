package com.tl_connect.dev.modules.subject.projection;

public interface SubjectPrerequisiteConditionRow {
    Long getId();
    Integer getMinSubjectsRequired();
    Integer getPassedCount();
    Integer getTotal();
}
