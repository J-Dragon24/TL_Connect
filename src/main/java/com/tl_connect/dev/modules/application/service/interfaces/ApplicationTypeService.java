package com.tl_connect.dev.modules.application.service.interfaces;

import java.util.List;

import com.tl_connect.dev.modules.application.dto.ApplicationTypeDTO;
import com.tl_connect.dev.modules.application.dto.CreateApplicationTypeDTO;
import com.tl_connect.dev.modules.application.dto.UpdateApplicationTypeDTO;

public interface ApplicationTypeService {
    List<ApplicationTypeDTO> getAllApplicationType();

    Long createApplicationType(CreateApplicationTypeDTO createApplicationTypeDTO);

    void updateApplicationType(Long id, UpdateApplicationTypeDTO updateApplicationTypeDTO);

    void deleteApplicationType(Long id);
}
