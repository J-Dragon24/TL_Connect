package com.tl_connect.dev.modules.tuition.service.interfaces;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.tuition.entity.TuitionFeeConfig;
import com.tl_connect.dev.modules.tuition.dto.CreateTuitionFeeConfig;
import com.tl_connect.dev.modules.tuition.dto.UpdateTuitionFeeConfig;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

public interface TuitionFeeConfigService {

    PagedResponse<TuitionFeeConfig> getTuitionFeeConfig(Pageable pageable);

    Long createTuitionFeeConfig(CreateTuitionFeeConfig createTuitionFeeConfig);

    void updateTuitionFeeConfig(Long id, UpdateTuitionFeeConfig updateTuitionFeeConfig);

    void deleteTuitionFeeConfig(Long id);
}
