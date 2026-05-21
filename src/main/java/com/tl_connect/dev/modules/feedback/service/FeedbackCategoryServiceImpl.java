package com.tl_connect.dev.modules.feedback.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.feedback.dto.CreateFeedbackCategoryDTO;
import com.tl_connect.dev.modules.feedback.dto.FeedbackCategoryDTO;
import com.tl_connect.dev.modules.feedback.entity.FeedbackCategory;
import com.tl_connect.dev.modules.feedback.repository.FeedbackCategoryRepository;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.modules.feedback.service.interfaces.FeedbackCategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackCategoryServiceImpl implements FeedbackCategoryService {
    
    private final FeedbackCategoryRepository feedbackCategoryRepository;

    public List<FeedbackCategoryDTO> getAllFeedbackCategory() {
        List<FeedbackCategory> feedbackCategories = feedbackCategoryRepository.findAll();
        List<FeedbackCategoryDTO> feedbackCategoryDTOs = new ArrayList<>();
        for (FeedbackCategory feedbackCategory : feedbackCategories) {
            feedbackCategoryDTOs.add(FeedbackCategoryDTO.builder()
                    .id(feedbackCategory.getId())
                    .name(feedbackCategory.getName())
                    .description(feedbackCategory.getDescription())
                    .build());
        }
        return feedbackCategoryDTOs;
    }

    public List<FeedbackCategory> getAdminFeedbackCategory() {
        List<FeedbackCategory> feedbackCategories = feedbackCategoryRepository.findAll();
        return feedbackCategories;
    }

    @Transactional
    public FeedbackCategory createFeedbackCategory(CreateFeedbackCategoryDTO feedbackCategoryDTO) {
        FeedbackCategory feedbackCategory = FeedbackCategory.create(feedbackCategoryDTO.getName(), feedbackCategoryDTO.getDescription());
        try{
            return feedbackCategoryRepository.save(feedbackCategory);
        }catch(DataIntegrityViolationException e){
            throw new ErrorException(ResponseStatus.DATABASE_ERROR, "Failed to create feedback category");
        }
    }

    @Transactional
    public void updateFeedbackCategory(Long id, CreateFeedbackCategoryDTO feedbackCategoryDTO) {
        FeedbackCategory feedbackCategory = feedbackCategoryRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Feedback category not found"));
        feedbackCategory.update(feedbackCategoryDTO.getName(), feedbackCategoryDTO.getDescription());
        feedbackCategoryRepository.save(feedbackCategory);
    }

    @Transactional
    public void deleteFeedbackCategory(Long id) {
        FeedbackCategory feedbackCategory = feedbackCategoryRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Feedback category not found"));
        feedbackCategory.deactive();
        feedbackCategoryRepository.save(feedbackCategory);
    }
}
