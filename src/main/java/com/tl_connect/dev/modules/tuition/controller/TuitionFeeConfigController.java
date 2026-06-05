package com.tl_connect.dev.modules.tuition.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.modules.tuition.dto.CreateTuitionFeeConfig;
import com.tl_connect.dev.modules.tuition.dto.UpdateTuitionFeeConfig;
import com.tl_connect.dev.modules.tuition.entity.TuitionFeeConfig;
import com.tl_connect.dev.modules.tuition.service.interfaces.TuitionFeeConfigService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.ultility.ResponseHelper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/tuition-fee-configs")
@RequiredArgsConstructor
public class TuitionFeeConfigController {
    
    private final TuitionFeeConfigService tuitionFeeConfigService;
    
    @GetMapping
    public ResponseEntity<?> getTuitionFeeConfig(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        PagedResponse<TuitionFeeConfig> tuitionFeeConfigs = tuitionFeeConfigService.getTuitionFeeConfig(pageable);
        return ResponseHelper.success("Get tuition fee configs successfully", tuitionFeeConfigs);
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createTuitionFeeConfig(@Valid @RequestBody CreateTuitionFeeConfig createTuitionFeeConfig) {
        Long id = tuitionFeeConfigService.createTuitionFeeConfig(createTuitionFeeConfig);
        return ResponseHelper.success("Create tuition fee config successfully", id);
    }
    
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateTuitionFeeConfig(@PathVariable Long id, @Valid @RequestBody UpdateTuitionFeeConfig updateTuitionFeeConfig) {
        tuitionFeeConfigService.updateTuitionFeeConfig(id, updateTuitionFeeConfig);
        return ResponseHelper.success("Update tuition fee config successfully", null);
    }
    
    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteTuitionFeeConfig(@PathVariable Long id) {
        tuitionFeeConfigService.deleteTuitionFeeConfig(id);
        return ResponseHelper.success("Delete tuition fee config successfully", null);
    }
    
}
