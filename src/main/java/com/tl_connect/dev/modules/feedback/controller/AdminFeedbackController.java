package com.tl_connect.dev.modules.feedback.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.modules.feedback.dto.FeedbackDTO;
import com.tl_connect.dev.modules.feedback.dto.UpdateStatusFeedbackDTO;
import com.tl_connect.dev.modules.feedback.service.FeedbackService;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/feedback")
public class AdminFeedbackController {
    private final FeedbackService feedbackService;
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllFeedback() {
        List<FeedbackDTO> feedbackDTOs = feedbackService.getAllFeedback();
        return ResponseHelper.success("get all feedback success", feedbackDTOs);
    }

    @PostMapping("/update-status/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusFeedbackDTO feedbackDTO) {
        feedbackService.updateStatus(id, feedbackDTO);
        return ResponseHelper.success("update status success", null);
    }
}
