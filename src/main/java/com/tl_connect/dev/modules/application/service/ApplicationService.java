package com.tl_connect.dev.modules.application.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.modules.application.dto.ApplicationAttachmentDTO;
import com.tl_connect.dev.modules.application.dto.ApplicationDTO;
import com.tl_connect.dev.modules.application.dto.ApplicationSubmitDTO;
import com.tl_connect.dev.modules.application.dto.DetailApplicationDTO;
import com.tl_connect.dev.modules.application.dto.UpdateApplicationDTO;
import com.tl_connect.dev.modules.application.entity.ApplicationAttachment;
import com.tl_connect.dev.modules.application.entity.StudentApplication;
import com.tl_connect.dev.modules.application.projection.ApplicationRow;
import com.tl_connect.dev.modules.application.projection.DetailApplicationView;
import com.tl_connect.dev.modules.application.repository.ApplicationAttachmentRepository;
import com.tl_connect.dev.modules.application.repository.ApplicationRepository;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.shared.common.ultility.FileHelper;

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

    public PagedResponse<ApplicationDTO> getAllApplication(Pageable pageable) {
        Page<ApplicationRow> applications = applicationRepository.findAllApplication(pageable);
        return new PagedResponse<>(
                applications.getContent().stream().map(this::toDTO).toList(),
                applications.getNumber(),
                applications.getSize(),
                applications.getTotalElements(),
                applications.getTotalPages(),
                applications.isFirst(),
                applications.isLast());
    }

    public DetailApplicationDTO getDetailApplication(Long id) {
        DetailApplicationView application = applicationRepository.findDetailById(id)
                .orElseThrow(() -> new NotFoundException("Application not found"));
        List<ApplicationAttachment> attachments = applicationAttachmentRepository.findByApplicationId(id);
        return DetailApplicationDTO.builder()
                .id(application.getId())
                .studentCode(application.getStudentCode())
                .studentName(application.getStudentName())
                .applicationTypeName(application.getApplicationTypeName())
                .status(application.getStatus())
                .content(application.getContent())
                .attachments(attachments.stream().map(this::toAttachmentDTO).toList())
                .build();
    }

    @Transactional
    public void deleteApplication(Long id) {
        StudentApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Application not found"));
        try {
            applicationRepository.delete(application);
        } catch (Exception e) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR, "Delete application failed");
        }

    }

    @Transactional
    public void updateStatusApplication(Long id, UpdateApplicationDTO status) {
        StudentApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Application not found"));
        application.updateStatus(status.getStatus());
        try {
            applicationRepository.save(application);
        } catch (Exception e) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR, "Update status application failed");
        }
    }

    @Transactional
    public ApplicationSubmitDTO submitApplication(List<MultipartFile> files, Long applicationTypeId, String content,
            Long studentId)
            throws IOException {

        List<String> fileKeys = new ArrayList<>();

        try {
            System.out.println("Uploading files...");
            for (MultipartFile file : files) {
                fileKeys.add(fileHelper.uploadFile(file).getKey());
            }
        } catch (Exception e) {
            fileKeys.forEach(fileHelper::deleteFile);
            throw new RuntimeException("Upload file thất bại" + e.getMessage(), e);
        }

        StudentApplication application = applicationRepository.save(
                StudentApplication.create(studentId, applicationTypeId, content));

        List<ApplicationAttachment> attachments = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            attachments.add(ApplicationAttachment.create(application.getId(), fileKeys.get(i),
                    files.get(i).getOriginalFilename(), files.get(i).getSize()));
        }

        try {
            applicationRepository.save(application);
            applicationAttachmentRepository.saveAll(attachments);
        } catch (DataIntegrityViolationException e) {
            fileKeys.forEach(fileHelper::deleteFile);
            throw new ErrorException(ResponseStatus.DATABASE_ERROR, "Save application attachments failed");
        }

        return ApplicationSubmitDTO.builder()
                .success(true)
                .fileNames(files.stream().map(file -> file.getOriginalFilename()).toList())
                .build();
    }

    private ApplicationDTO toDTO(ApplicationRow row) {
        return ApplicationDTO.builder()
                .id(row.getId())
                .studentCode(row.getStudentCode())
                .studentName(row.getStudentName())
                .applicationTypeName(row.getApplicationTypeName())
                .status(row.getStatus())
                .build();
    }

    private ApplicationAttachmentDTO toAttachmentDTO(ApplicationAttachment attachment) {
        return ApplicationAttachmentDTO.builder()
                .id(attachment.getId())
                .fileKey(attachment.getFileKey())
                .originalFilename(attachment.getOriginalFilename())
                .fileSize(attachment.getFileSize())
                .build();
    }

    // public List<ApplicationTypeDTO> getHistoryApplication(Long studentId) {
    // List<StudentApplication> applications =
    // applicationRepository.findAllByStudentId(studentId);
    // return applications.stream()
    // .map(application -> ApplicationTypeDTO.builder()
    // .id(application.getId())
    // .code(application.getCode())
    // .name(applicationType.getName())
    // .build())
    // .toList();
    // }

}
