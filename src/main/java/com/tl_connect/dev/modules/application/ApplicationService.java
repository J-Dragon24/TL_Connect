package com.tl_connect.dev.modules.application;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.core.common.ultility.FileHelper;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    public String submitApplication(MultipartFile file, String requestType, String content, FileHelper fileHelper)
            throws IOException {
        String fileName = fileHelper.uploadFile(file);

        return fileName;
    }

}
