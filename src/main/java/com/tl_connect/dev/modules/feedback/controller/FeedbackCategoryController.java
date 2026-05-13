package com.tl_connect.dev.modules.feedback.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import com.tl_connect.dev.modules.feedback.service.FeedbackCategoryService;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.feedback.dto.CreateFeedbackCategoryDTO;
import com.tl_connect.dev.modules.feedback.entity.FeedbackCategory;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/feedback-category")
public class FeedbackCategoryController {
    
    private final FeedbackCategoryService feedbackCategoryService;

    @PostMapping("/create")
    public ResponseEntity<?> createFeedbackCategory(@RequestBody CreateFeedbackCategoryDTO feedbackCategoryDTO) {
        feedbackCategoryService.createFeedbackCategory(feedbackCategoryDTO);
        return ResponseHelper.success("create feedback category success", null);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateFeedbackCategory(@PathVariable Long id, @RequestBody CreateFeedbackCategoryDTO feedbackCategoryDTO) {
        feedbackCategoryService.updateFeedbackCategory(id, feedbackCategoryDTO);
        return ResponseHelper.success("update feedback category success", null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteFeedbackCategory(@PathVariable Long id) {
        feedbackCategoryService.deleteFeedbackCategory(id);
        return ResponseHelper.success("delete feedback category success", null);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllFeedbackCategory() {
        List<FeedbackCategory> categories = feedbackCategoryService.getAdminFeedbackCategory();
        return ResponseHelper.success("get all feedback category success", categories);
    }
}
