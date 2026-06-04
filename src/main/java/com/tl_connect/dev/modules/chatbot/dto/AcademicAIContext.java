package com.tl_connect.dev.modules.chatbot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AcademicAIContext {
    private int startYear;
    private int endYear;
    private String majorCode;
    private String majorName;
    private String facultyCode;
    private String studyProgramCode;
}
