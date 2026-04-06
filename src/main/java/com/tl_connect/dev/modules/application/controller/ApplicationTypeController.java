package com.tl_connect.dev.modules.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.modules.application.dto.ApplicationTypeDTO;
import com.tl_connect.dev.modules.application.dto.CreateApplicationTypeDTO;
import com.tl_connect.dev.modules.application.dto.UpdateApplicationTypeDTO;
import com.tl_connect.dev.modules.application.service.ApplicationTypeService;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/application-types")
@RequiredArgsConstructor
public class ApplicationTypeController {

    private final ApplicationTypeService applicationTypeService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllApplicationType() {
        List<ApplicationTypeDTO> applicationTypes = applicationTypeService.getAllApplicationType();
        return ResponseHelper.success("List of applications", applicationTypes);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createApplicationType(@RequestBody CreateApplicationTypeDTO req) {
        Long applicationTypeId = applicationTypeService.createApplicationType(req);
        return ResponseHelper.success("Application type created successfully", applicationTypeId);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateApplicationType(@PathVariable Long id, @RequestBody UpdateApplicationTypeDTO req) {
        applicationTypeService.updateApplicationType(id, req);
        return ResponseHelper.success("Application type updated successfully", null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteApplicationType(@PathVariable Long id) {
        applicationTypeService.deleteApplicationType(id);
        return ResponseHelper.success("Application type deleted successfully", null);
    }
}
