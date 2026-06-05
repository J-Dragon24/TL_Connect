package com.tl_connect.dev.shared.ultility.provider;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tl_connect.dev.shared.common.dto.UploadResult;
import com.tl_connect.dev.shared.common.exception.ExternalException;
import com.tl_connect.dev.shared.ultility.FileHelper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Primary
public class CloudinaryProvider extends FileHelper {

    private final Cloudinary cloudinary;

    @Override
    public UploadResult uploadFile(String type, MultipartFile file) throws IOException {
        try {
            String original = file.getOriginalFilename();
            String fileName = StringUtils.stripFilenameExtension(original);
            String ext = StringUtils.getFilenameExtension(original);

            fileName = fileName.replaceAll("[^a-zA-Z0-9_-]", "_");
            ext = ext != null ? ext.toLowerCase() : "";

            String key = System.currentTimeMillis() + "_" + fileName;
            String resourceType;

            if (List.of("png", "jpg", "jpeg", "webp", "pdf").contains(ext)) {
                resourceType = "image";
            } else if (ext.equals("mp4")) {
                resourceType = "video";
            } else {
                resourceType = "raw";
            }

            Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder", type,
                    "resource_type", resourceType,
                    "public_id", key,
                    "type", "upload",
                    "format", ext
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
