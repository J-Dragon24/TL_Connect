package com.tl_connect.dev.modules.feedback.service.interfaces;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.modules.feedback.dto.FeedbackDTO;
import com.tl_connect.dev.modules.feedback.dto.SendFeedbackRequestDTO;
import com.tl_connect.dev.modules.feedback.dto.UpdateStatusFeedbackDTO;

public interface FeedbackService {

    void sendFeedback(List<MultipartFile> files, Long oauthUserId, SendFeedbackRequestDTO feedbackRequest) throws IOException;

    List<FeedbackDTO> getAllFeedback();

    void updateStatus(Long feedbackId, UpdateStatusFeedbackDTO feedbackDTO);
}
