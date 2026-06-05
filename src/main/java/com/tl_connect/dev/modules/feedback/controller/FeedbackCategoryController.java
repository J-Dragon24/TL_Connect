package com.tl_connect.dev.modules.feedback.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.types.JwtUserInfo;
import com.tl_connect.dev.shared.ultility.ResponseHelper;
import com.tl_connect.dev.modules.feedback.dto.CreateFeedbackCategoryDTO;
import com.tl_connect.dev.modules.feedback.entity.FeedbackCategory;
import com.tl_connect.dev.modules.feedback.service.interfaces.FeedbackCategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/feedback-category")
public class FeedbackCategoryController {
    
    private final FeedbackCategoryService feedbackCategoryService;

    @PostMapping("/create")
    public ResponseEntity<?> createFeedbackCategory(Authentication authentication, @RequestBody CreateFeedbackCategoryDTO feedbackCategoryDTO) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        feedbackCategoryService.createFeedbackCategory(feedbackCategoryDTO);
        return ResponseHelper.success("create feedback category success", null);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateFeedbackCategory(Authentication authentication, @PathVariable Long id, @RequestBody CreateFeedbackCategoryDTO feedbackCategoryDTO) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        feedbackCategoryService.updateFeedbackCategory(id, feedbackCategoryDTO);
        return ResponseHelper.success("update feedback category success", null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteFeedbackCategory(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        feedbackCategoryService.deleteFeedbackCategory(id);
        return ResponseHelper.success("delete feedback category success", null);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllFeedbackCategory(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        List<FeedbackCategory> categories = feedbackCategoryService.getAdminFeedbackCategory();
        return ResponseHelper.success("get all feedback category success", categories);
    }
}
