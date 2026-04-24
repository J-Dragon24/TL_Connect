package com.tl_connect.dev.core.common.ultility.provider;

import java.io.IOException;
import java.util.Map;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tl_connect.dev.core.common.dto.UploadResult;
import com.tl_connect.dev.core.common.exception.ExternalException;
import com.tl_connect.dev.core.common.ultility.FileHelper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Primary
public class CloudinaryProvider extends FileHelper {

    private final Cloudinary cloudinary;

    @Override
    public UploadResult uploadFile(MultipartFile file) throws IOException {
        try {
            String fileName = file.getOriginalFilename().split(".")[0];
            String key = System.currentTimeMillis() + "_" + fileName;
            Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder", "uploads",
                    "resource_type", "raw",
                    "public_id", key,
                    "type", "upload"
                )
            );
            return new UploadResult(result.get("public_id").toString(), result.get("secure_url").toString());
        } catch (Exception e) {
            throw new ExternalException("Upload file failed" + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new ExternalException("Delete file failed");
        }
    }
}
