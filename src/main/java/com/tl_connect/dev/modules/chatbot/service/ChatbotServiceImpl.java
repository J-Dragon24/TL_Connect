package com.tl_connect.dev.modules.chatbot.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.chatbot.dto.AIContextDTO;
import com.tl_connect.dev.modules.chatbot.dto.AcademicAIContext;
import com.tl_connect.dev.modules.chatbot.projection.AIContextView;
import com.tl_connect.dev.modules.chatbot.service.interfaces.ChatbotService;
import com.tl_connect.dev.modules.student.service.interfaces.StudentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService{

    private final StudentService studentService;

    @Override
    public AIContextDTO getAIContext(Long studentId){
        List<AIContextView> aiContext = studentService.findAIContextByStudentId(studentId);
        
        List<AcademicAIContext> academicContexts = aiContext.stream().map(aiContextView -> AcademicAIContext.builder()
                .startYear(aiContextView.getStartYear())
                .endYear(aiContextView.getEndYear())
                .majorCode(aiContextView.getMajorCode())
                .majorName(aiContextView.getMajorName())
                .facultyCode(aiContextView.getFacultyCode())
                .studyProgramCode(aiContextView.getStudyProgramCode())
                .build()).collect(Collectors.toList());
        return AIContextDTO.builder()
                .studentName(aiContext.get(0).getStudentName())
                .studentCode(aiContext.get(0).getStudentCode())
                .dateOfBirth(aiContext.get(0).getDateOfBirth())
                .gender(aiContext.get(0).getGender())
                .academicInfo(academicContexts)
                .build();
    }
}
