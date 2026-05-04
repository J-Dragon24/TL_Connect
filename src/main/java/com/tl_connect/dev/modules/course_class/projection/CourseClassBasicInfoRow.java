package com.tl_connect.dev.modules.course_class.projection;

public interface CourseClassBasicInfoRow {
    Long getId();
    String getClassCode();
    String getClassName();
    Integer getCapacity();
    Integer getEnrolledCount();
    String getLecturerCode();
    String getSubjectCode();
    String getSemesterCode();
    Boolean getIsActive();
}
