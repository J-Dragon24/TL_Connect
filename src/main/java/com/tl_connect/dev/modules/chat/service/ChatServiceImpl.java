package com.tl_connect.dev.modules.chat.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.modules.chat.dto.UploadFileResponseDTO;
import com.tl_connect.dev.modules.chat.service.interfaces.ChatService;
import com.tl_connect.dev.shared.common.dto.UploadResult;
import com.tl_connect.dev.shared.common.exception.ExternalException;
import com.tl_connect.dev.shared.common.ultility.FileHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final FileHelper fileHelper;

    @Override
    public UploadFileResponseDTO uploadFile(MultipartFile file) throws IOException {
        String fileKey = null;
        try {
            UploadResult uploadResult = fileHelper.uploadFile("chat-multimedia", file);
            return UploadFileResponseDTO.builder()
                    .url(uploadResult.getUrl())
                    .build();
        } catch (Exception e) {
            fileHelper.deleteFile(fileKey);
            throw new ExternalException("Upload file thất bại");
        }
    }
}
