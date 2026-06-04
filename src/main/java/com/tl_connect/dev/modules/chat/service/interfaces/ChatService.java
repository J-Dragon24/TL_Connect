package com.tl_connect.dev.modules.chat.service.interfaces;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.modules.chat.dto.UploadFileResponseDTO;

public interface ChatService {
    UploadFileResponseDTO uploadFile(MultipartFile file) throws IOException;
}
