package com.tl_connect.dev.auth.projection;

public interface JwtUserInfoView {
    String getMicrosoftId();

    Long getStudentId();

    String getRole();
}