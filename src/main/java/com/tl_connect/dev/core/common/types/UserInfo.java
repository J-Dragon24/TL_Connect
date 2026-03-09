package com.tl_connect.dev.core.common.types;

import java.util.List;

import lombok.Builder;

@Builder
public record UserInfo (
    String oid,
    String email,
    String name,
    List<String> roles,
    String avatar
){

}
