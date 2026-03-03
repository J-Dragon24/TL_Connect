package com.tl_connect.dev.core.common.types;

public record MicrosoftUserInfo(
    String id,
    String displayName,
    String mail,
    String userPrincipalName
) {}