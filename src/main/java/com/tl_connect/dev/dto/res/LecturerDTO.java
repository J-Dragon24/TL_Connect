package com.tl_connect.dev.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LecturerDTO {
    private String lecturerCode;
    private String fullName;
    private String phoneNumber;
    private String email;
}
