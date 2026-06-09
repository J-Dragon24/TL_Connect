package com.tl_connect.dev.modules.feedback.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.modules.feedback.dto.FeedbackDTO;
import com.tl_connect.dev.modules.feedback.dto.SendFeedbackRequestDTO;
import com.tl_connect.dev.modules.feedback.dto.UpdateStatusFeedbackDTO;
import com.tl_connect.dev.modules.feedback.entity.Feedback;
import com.tl_connect.dev.modules.feedback.entity.FeedbackAttachment;
import com.tl_connect.dev.modules.feedback.projection.FeedbackRow;
import com.tl_connect.dev.modules.feedback.repository.FeedbackAttachmentRepository;
import com.tl_connect.dev.modules.feedback.repository.FeedbackRepository;
import com.tl_connect.dev.shared.common.dto.UploadResult;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.shared.ultility.FileHelper;
import com.tl_connect.dev.modules.feedback.service.interfaces.FeedbackService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final FeedbackAttachmentRepository feedbackAttachmentRepository;
    private final FileHelper fileHelper;

    @Transactional
    public void sendFeedback(List<MultipartFile> files, Long oauthUserId, SendFeedbackRequestDTO feedbackRequest) throws IOException {

        List<UploadResult> results = new ArrayList<>();

        if(files != null && !files.isEmpty()) {
            try {
                for (MultipartFile file : files) {
                    if (file == null || file.isEmpty()) {
                        continue;
                    }
                    results.add(fileHelper.uploadFile("feedback", file));
                }
            } catch (Exception e) {
                results.forEach(result -> fileHelper.deleteFile(result.getKey()));
                throw new RuntimeException("Upload file failed: " + e.getMessage(), e);
            }
        }


        Feedback feedback = feedbackRepository.save(Feedback.create(oauthUserId, feedbackRequest.getTitle(), feedbackRequest.getContent(), feedbackRequest.getCategoryId(), feedbackRequest.getAppVersion(), feedbackRequest.getDeviceInfo()));
        List<FeedbackAttachment> attachments = new ArrayList<>();
        if(files != null && !files.isEmpty()){
            for (int i = 0; i < files.size(); i++) {
                UploadResult result = results.get(i);
                attachments.add(FeedbackAttachment.create(feedback.getId(), result.getKey(), files.get(i).getOriginalFilename(), files.get(i).getSize(), result.getResourceType()));
            }
        }

        try {
            feedbackRepository.save(feedback);
            feedbackAttachmentRepository.saveAll(attachments);
        } catch (DataIntegrityViolationException e) {
            results.forEach(result -> fileHelper.deleteFile(result.getKey()));
            throw new ErrorException(ResponseStatus.DATABASE_ERROR, "Save feedback attachments failed");
        }
    }

    public List<FeedbackDTO> getAllFeedback() {
        List<FeedbackRow> feedbacks = feedbackRepository.findAllFeedback();
        return feedbacks.stream().map(f -> FeedbackDTO.builder()
            .id(f.getFeedbackId())
            .email(f.getEmail())
            .title(f.getTitle())
            .content(f.getContent())
            .categoryName(f.getCategoryName())
            .appVersion(f.getAppVersion())
            .deviceInfo(f.getDeviceInfo())
            .feedbackImages(f.getFeedbackImages() == null
                ? List.of()
                : Arrays.asList(f.getFeedbackImages().split(",")))
            .resourceTypes(f.getResourceTypes() == null
                ? List.of()
                : Arrays.asList(f.getResourceTypes().split(",")))
            .status(f.getStatus())
            .createdAt(f.getCreatedAt())
            .build()).collect(Collectors.toList());
    }

    @Transactional
    public void updateStatus(Long feedbackId, UpdateStatusFeedbackDTO feedbackDTO) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
        .orElseThrow(() -> new NotFoundException("Feedback not found"));
        feedback.setStatus(feedbackDTO.getStatus());
        feedbackRepository.save(feedback);
    }
}
