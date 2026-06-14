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
import com.tl_connect.dev.modules.application.dto.HistoryApplicationDTO;
import com.tl_connect.dev.modules.application.dto.HistoryDetailApplication;
import com.tl_connect.dev.modules.application.dto.UpdateApplicationDTO;
import com.tl_connect.dev.modules.application.entity.ApplicationAttachment;
import com.tl_connect.dev.modules.application.entity.StudentApplication;
import com.tl_connect.dev.modules.application.projection.ApplicationAdminRow;
import com.tl_connect.dev.modules.application.projection.ApplicationRow;
import com.tl_connect.dev.modules.application.projection.DetailApplicationAdminView;
import com.tl_connect.dev.modules.application.projection.DetailApplicationView;
import com.tl_connect.dev.modules.application.repository.ApplicationAttachmentRepository;
import com.tl_connect.dev.modules.application.repository.ApplicationRepository;
import com.tl_connect.dev.modules.application.service.interfaces.ApplicationService;
import com.tl_connect.dev.modules.notification.dto.CreateNotificationReqDTO;
import com.tl_connect.dev.modules.notification.service.interfaces.NotificationModifyService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.dto.UploadResult;
import com.tl_connect.dev.shared.common.enums.ApplicationStatus;
import com.tl_connect.dev.shared.common.enums.NotificationCreatedBy;
import com.tl_connect.dev.shared.common.enums.NotificationType;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.ExternalException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.shared.ultility.FileHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationAttachmentRepository applicationAttachmentRepository;
    private final FileHelper fileHelper;
    private final NotificationModifyService notificationModifyService;

    public PagedResponse<ApplicationDTO> getAllApplication(Pageable pageable) {
        Page<ApplicationAdminRow> applications = applicationRepository.findAllApplication(pageable);
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
        DetailApplicationAdminView application = applicationRepository.findDetailById(id)
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

        if(status.getStatus() == ApplicationStatus.APPROVED){
            CreateNotificationReqDTO req = CreateNotificationReqDTO.builder()
                .title("Đơn của bạn đã được duyệt")
                .content("Đơn của bạn đã được phê duyệt, vui lòng kiểm tra lại thông tin")
                .createdBy(NotificationCreatedBy.SYSTEM)
                .targetType(NotificationType.STUDENT)
                .targetIds(List.of(application.getStudentId()))
                .isImportant(false)
                .build();
            
            notificationModifyService.sendNotification(req);
        }
    }

    @Transactional
    public ApplicationSubmitDTO submitApplication(List<MultipartFile> files, Long applicationTypeId, String content,
            Long studentId)
            throws IOException {

        List<UploadResult> results = new ArrayList<>();

        try {
            System.out.println("Uploading files...");
            for (MultipartFile file : files) {
                results.add(fileHelper.uploadFile("application", file));
            }
        } catch (Exception e) {
            results.forEach(result -> fileHelper.deleteFile(result.getKey()));
            throw new ExternalException("Failed to upload file");
        }

        StudentApplication application = applicationRepository.save(
                StudentApplication.create(studentId, applicationTypeId, content));

        List<ApplicationAttachment> attachments = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            attachments.add(ApplicationAttachment.create(application.getId(), results.get(i).getKey(),
                    files.get(i).getOriginalFilename(), files.get(i).getSize(), results.get(i).getResourceType()));
        }

        try {
            applicationRepository.save(application);
            applicationAttachmentRepository.saveAll(attachments);
        } catch (DataIntegrityViolationException e) {
            results.forEach(result -> fileHelper.deleteFile(result.getKey()));
            throw new ErrorException(ResponseStatus.DATABASE_ERROR, "Save application attachments failed");
        }

        return ApplicationSubmitDTO.builder()
                .success(true)
                .fileNames(files.stream().map(file -> file.getOriginalFilename()).toList())
                .build();
    }

    private ApplicationDTO toDTO(ApplicationAdminRow row) {
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
                .resourceType(attachment.getResourceType())
                .build();
    }

    public List<HistoryApplicationDTO> getHistoryApplication(Long studentId) {
        List<ApplicationRow> applications = applicationRepository.findHistoryApplicationByStudentId(studentId);
        return applications.stream()
                .map(row -> HistoryApplicationDTO.builder()
                        .id(row.getId())
                        .typeName(row.getApplicationTypeName())
                        .status(row.getStatus())
                        .createdAt(row.getCreatedAt())
                        .build())
                .toList();
    }


    public HistoryDetailApplication getDetailApplicationHistory(Long id) {
        DetailApplicationView application = applicationRepository.findHistoryDetailById(id)
                .orElseThrow(() -> new NotFoundException("Application not found"));
        List<ApplicationAttachment> attachments = applicationAttachmentRepository.findByApplicationId(id);
        return HistoryDetailApplication.builder()
                .typeName(application.getApplicationTypeName())
                .status(application.getStatus())
                .content(application.getContent())
                .attachments(attachments.stream().map(this::toAttachmentDTO).toList())
                .createdAt(application.getCreatedAt())
                .build();
    }
}
