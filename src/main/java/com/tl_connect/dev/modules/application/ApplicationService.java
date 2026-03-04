package com.tl_connect.dev.modules.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.core.common.enums.ApplicationStatus;
import com.tl_connect.dev.core.common.ultility.FileHelper;
import com.tl_connect.dev.modules.application.dto.ApplicationSubmitDTO;
import com.tl_connect.dev.modules.application.dto.ApplicationTypeDTO;
import com.tl_connect.dev.modules.application.entity.ApplicationAttachment;
import com.tl_connect.dev.modules.application.entity.ApplicationType;
import com.tl_connect.dev.modules.application.entity.StudentApplication;
import com.tl_connect.dev.modules.application.repository.ApplicationAttachmentRepository;
import com.tl_connect.dev.modules.application.repository.ApplicationRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationAttachmentRepository applicationAttachmentRepository;
    private final FileHelper fileHelper;

    public List<ApplicationTypeDTO> getAllApplicationType() {
        List<ApplicationType> applicationTypes = applicationRepository.findAllApplicationType();
        return applicationTypes.stream()
                .map(applicationType -> ApplicationTypeDTO.builder()
                        .id(applicationType.getId())
                        .code(applicationType.getCode())
                        .name(applicationType.getName())
                        .build())
                .toList();
    }

    @Transactional
    public ApplicationSubmitDTO submitApplication(List<MultipartFile> files, Long applicationTypeId, String content, Long studentId)
            throws IOException {

        List<String> fileKeys = new ArrayList<>();

        try {
            System.out.println("Uploading files...");
            for (MultipartFile file : files) {
                fileKeys.add(fileHelper.uploadFile(file));
            }
        } catch (Exception e) {
            // Rollback các file đã upload
            fileKeys.forEach(fileHelper::deleteFile);
            throw new RuntimeException("Upload file thất bại", e);
        }

        StudentApplication application = applicationRepository.save(
            StudentApplication.builder()
            .studentId(studentId)
            .applicationTypeId(applicationTypeId)
            .content(content)
            .status(ApplicationStatus.PENDING)
            .build()
        );

        
        List<ApplicationAttachment> attachments = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            attachments.add(ApplicationAttachment.builder()
                .applicationId(application.getId())
                .fileKey(fileKeys.get(i))
                .originalFilename(files.get(i).getOriginalFilename())
                .fileSize(files.get(i).getSize())
                .build());
        }
        
        applicationAttachmentRepository.saveAll(attachments);

        return ApplicationSubmitDTO.builder()
        .success(true)
        .fileNames(files.stream().map(file -> file.getOriginalFilename()).toList())
        .build();
    }

    // public List<ApplicationTypeDTO> getHistoryApplication(Long studentId) {
    //     List<StudentApplication> applications = applicationRepository.findAllByStudentId(studentId);
    //     return applications.stream()
    //             .map(application -> ApplicationTypeDTO.builder()
    //                     .id(application.getId())
    //                     .code(application.getCode())
    //                     .name(applicationType.getName())
    //                     .build())
    //             .toList();
    // }

}
