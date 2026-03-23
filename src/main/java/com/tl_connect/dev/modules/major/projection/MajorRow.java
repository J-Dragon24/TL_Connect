package com.tl_connect.dev.modules.major.projection;

import java.time.LocalDateTime;

public interface MajorRow {
    Long getId();
    String getMajorName();
    String getMajorCode();
    String getFacultyCode();
    Boolean getIsActive();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}
