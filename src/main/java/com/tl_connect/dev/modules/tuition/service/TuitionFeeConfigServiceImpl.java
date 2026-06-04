package com.tl_connect.dev.modules.tuition.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.tuition.dto.CreateTuitionFeeConfig;
import com.tl_connect.dev.modules.tuition.dto.UpdateTuitionFeeConfig;
import com.tl_connect.dev.modules.tuition.entity.TuitionFeeConfig;
import com.tl_connect.dev.modules.tuition.repository.TuitionFeeConfigRepository;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.modules.tuition.service.interfaces.TuitionFeeConfigService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TuitionFeeConfigServiceImpl implements TuitionFeeConfigService {
    
    private final TuitionFeeConfigRepository tuitionFeeConfigRepository;
    
    public PagedResponse<TuitionFeeConfig> getTuitionFeeConfig(Pageable pageable) {
        Page<TuitionFeeConfig> tuitionFeeConfigs = tuitionFeeConfigRepository.findAllConfigs(pageable);
        return new PagedResponse<>(
            tuitionFeeConfigs.getContent(),
            tuitionFeeConfigs.getNumber(),
            tuitionFeeConfigs.getSize(),
            tuitionFeeConfigs.getTotalElements(),
            tuitionFeeConfigs.getTotalPages(),
            tuitionFeeConfigs.isLast(),
            tuitionFeeConfigs.isFirst()
        );
    }

    @Transactional
    public Long createTuitionFeeConfig(CreateTuitionFeeConfig createTuitionFeeConfig) {
        TuitionFeeConfig tuitionFeeConfig = TuitionFeeConfig.create(createTuitionFeeConfig.getBasePricePerCredit(), createTuitionFeeConfig.getAcademicYear(), createTuitionFeeConfig.getCohort());
        try {
            return tuitionFeeConfigRepository.save(tuitionFeeConfig).getId();
        } catch (DataIntegrityViolationException e) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to create tuition fee config");
        }
    }

    @Transactional
    public void updateTuitionFeeConfig(Long id, UpdateTuitionFeeConfig updateTuitionFeeConfig) {
        TuitionFeeConfig tuitionFeeConfig = tuitionFeeConfigRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Tuition fee config not found"));
        tuitionFeeConfig.update(updateTuitionFeeConfig.getBasePricePerCredit(), updateTuitionFeeConfig.getAcademicYear(), updateTuitionFeeConfig.getCohort());
        try {
            tuitionFeeConfigRepository.save(tuitionFeeConfig);
        } catch (DataIntegrityViolationException e) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to update tuition fee config");
        }
    }

    @Transactional
    public void deleteTuitionFeeConfig(Long id) {
        TuitionFeeConfig tuitionFeeConfig = tuitionFeeConfigRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Tuition fee config not found"));
        try {
            tuitionFeeConfigRepository.delete(tuitionFeeConfig);
        } catch (Exception e) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to delete tuition fee config");
        }
    }

}
