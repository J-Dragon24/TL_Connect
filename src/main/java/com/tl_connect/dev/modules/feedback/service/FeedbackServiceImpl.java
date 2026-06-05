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

        List<String> fileKeys = new ArrayList<>();

        if(files != null && !files.isEmpty()) {
            try {
                for (MultipartFile file : files) {
                    if (file == null || file.isEmpty()) {
                        continue;
                    }
                    fileKeys.add(fileHelper.uploadFile("feedback", file).getKey());
                }
            } catch (Exception e) {
                fileKeys.forEach(fileHelper::deleteFile);
                throw new RuntimeException("Upload file failed: " + e.getMessage(), e);
            }
        }


        Feedback feedback = feedbackRepository.save(Feedback.create(oauthUserId, feedbackRequest.getTitle(), feedbackRequest.getContent(), feedbackRequest.getCategoryId(), feedbackRequest.getAppVersion(), feedbackRequest.getDeviceInfo()));
        List<FeedbackAttachment> attachments = new ArrayList<>();
        if(files != null && !files.isEmpty()){
            for (int i = 0; i < files.size(); i++) {
                attachments.add(FeedbackAttachment.create(feedback.getId(), fileKeys.get(i),
                        files.get(i).getOriginalFilename(), files.get(i).getSize()));
            }
        }

        try {
            feedbackRepository.save(feedback);
            feedbackAttachmentRepository.saveAll(attachments);
        } catch (DataIntegrityViolationException e) {
            fileKeys.forEach(fileHelper::deleteFile);
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
