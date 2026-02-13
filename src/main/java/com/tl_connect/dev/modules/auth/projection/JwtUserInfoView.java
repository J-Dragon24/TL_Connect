package com.tl_connect.dev.modules.auth.projection;

public interface JwtUserInfoView {
    String getMicrosoftId();

    Long getStudentId();

    String getRole();
}