package com.tl_connect.dev.modules.feedback.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.modules.feedback.dto.FeedbackCategoryDTO;
import com.tl_connect.dev.modules.feedback.dto.SendFeedbackRequestDTO;
import com.tl_connect.dev.modules.feedback.service.interfaces.FeedbackCategoryService;
import com.tl_connect.dev.modules.feedback.service.interfaces.FeedbackService;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.common.types.JwtUserInfo;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feedback")
public class FeedbackController {
    
    private final FeedbackCategoryService feedbackCategoryService;
    private final FeedbackService feedbackService;

    @GetMapping("/category")
    public ResponseEntity<?> getAllFeedbackCategory() {
        List<FeedbackCategoryDTO> feedbackCategories = feedbackCategoryService.getAllFeedbackCategory();
        return ResponseHelper.success("get All feedback category success", feedbackCategories);
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendFeedback(Authentication authentication, @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @ModelAttribute SendFeedbackRequestDTO feedbackRequest ) throws IOException {
        
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long oauthUserId = userInfo.oauthUserId();
        feedbackService.sendFeedback(files, oauthUserId, feedbackRequest);
        return ResponseHelper.success("send feedback success", null);
    }
}
