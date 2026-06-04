package com.tl_connect.dev.modules.attendance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceRequest {
    private String qrToken;

    private Double latitude;

    private Double longitude;
}
