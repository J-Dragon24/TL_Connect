package com.tl_connect.dev.modules.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.application.dto.ApplicationTypeDTO;
import com.tl_connect.dev.modules.application.dto.CreateApplicationTypeDTO;
import com.tl_connect.dev.modules.application.dto.UpdateApplicationTypeDTO;
import com.tl_connect.dev.modules.application.entity.ApplicationType;
import com.tl_connect.dev.modules.application.repository.ApplicationTypeRepository;
import com.tl_connect.dev.shared.common.exception.BadRequestException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationTypeService {

    private final ApplicationTypeRepository applicationTypeRepository;

    public List<ApplicationTypeDTO> getAllApplicationType() {
        List<ApplicationType> applicationTypes = applicationTypeRepository.findAllApplicationType();
        return applicationTypes.stream()
                .map(applicationType -> ApplicationTypeDTO.builder()
                        .id(applicationType.getId())
                        .code(applicationType.getCode())
                        .name(applicationType.getName())
                        .isActive(applicationType.getIsActive())
                        .build())
                .toList();
    }

    @Transactional
    public Long createApplicationType(CreateApplicationTypeDTO createApplicationTypeDTO) {
        ApplicationType applicationType = ApplicationType.create(createApplicationTypeDTO.getCode(), createApplicationTypeDTO.getName());
        try {
            return applicationTypeRepository.save(applicationType).getId();
        } catch (Exception e) {
            throw new BadRequestException("Application type code already exists" + e.getMessage());
        }
    }

    @Transactional
    public void updateApplicationType(Long id, UpdateApplicationTypeDTO updateApplicationTypeDTO) {
        ApplicationType applicationType = applicationTypeRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Application type not found"));
        applicationType.update(updateApplicationTypeDTO.getCode(), updateApplicationTypeDTO.getName());
        try {
            applicationTypeRepository.save(applicationType);
        } catch (Exception e) {
            throw new BadRequestException("Application type code already exists" + e.getMessage());
        }
    }

    @Transactional
    public void deleteApplicationType(Long id) {
        ApplicationType applicationType = applicationTypeRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Application type not found"));
        applicationType.deactivate();
        try {
            applicationTypeRepository.save(applicationType);
        } catch (Exception e) {
            throw new BadRequestException("Application type code already exists" + e.getMessage());
        }
    }
    
}
