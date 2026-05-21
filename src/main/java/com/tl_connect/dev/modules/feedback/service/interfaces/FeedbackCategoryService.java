package com.tl_connect.dev.modules.feedback.service.interfaces;

import java.util.List;

import com.tl_connect.dev.modules.feedback.dto.FeedbackCategoryDTO;
import com.tl_connect.dev.modules.feedback.dto.CreateFeedbackCategoryDTO;
import com.tl_connect.dev.modules.feedback.entity.FeedbackCategory;

public interface FeedbackCategoryService {

    List<FeedbackCategoryDTO> getAllFeedbackCategory();

    List<FeedbackCategory> getAdminFeedbackCategory();

    FeedbackCategory createFeedbackCategory(CreateFeedbackCategoryDTO feedbackCategoryDTO);

    void updateFeedbackCategory(Long id, CreateFeedbackCategoryDTO feedbackCategoryDTO);

    void deleteFeedbackCategory(Long id);
}
