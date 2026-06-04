package com.tl_connect.dev.modules.application.service.interfaces;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.modules.application.dto.ApplicationDTO;
import com.tl_connect.dev.modules.application.dto.ApplicationSubmitDTO;
import com.tl_connect.dev.modules.application.dto.DetailApplicationDTO;
import com.tl_connect.dev.modules.application.dto.HistoryApplicationDTO;
import com.tl_connect.dev.modules.application.dto.HistoryDetailApplication;
import com.tl_connect.dev.modules.application.dto.UpdateApplicationDTO;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

public interface ApplicationService {
    PagedResponse<ApplicationDTO> getAllApplication(Pageable pageable);

    DetailApplicationDTO getDetailApplication(Long id);

    void deleteApplication(Long id);

    void updateStatusApplication(Long id, UpdateApplicationDTO status);

    ApplicationSubmitDTO submitApplication(List<MultipartFile> files, Long applicationTypeId, String content, Long studentId) throws IOException;

    List<HistoryApplicationDTO> getHistoryApplication(Long studentId);

    HistoryDetailApplication getDetailApplicationHistory(Long id);
}
