package com.tl_connect.dev.modules.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.modules.application.dto.ApplicationTypeDTO;
import com.tl_connect.dev.modules.application.dto.CreateApplicationTypeDTO;
import com.tl_connect.dev.modules.application.dto.UpdateApplicationTypeDTO;
import com.tl_connect.dev.modules.application.service.interfaces.ApplicationTypeService;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.types.JwtUserInfo;
import com.tl_connect.dev.shared.ultility.ResponseHelper;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/application-types")
@RequiredArgsConstructor
public class ApplicationTypeController {

    private final ApplicationTypeService applicationTypeService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllApplicationType(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        List<ApplicationTypeDTO> applicationTypes = applicationTypeService.getAllApplicationType();
        return ResponseHelper.success("List of applications", applicationTypes);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createApplicationType(Authentication authentication, @RequestBody CreateApplicationTypeDTO req) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long applicationTypeId = applicationTypeService.createApplicationType(req);
        return ResponseHelper.success("Application type created successfully", applicationTypeId);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateApplicationType(Authentication authentication, @PathVariable Long id, @RequestBody UpdateApplicationTypeDTO req) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        applicationTypeService.updateApplicationType(id, req);
        return ResponseHelper.success("Application type updated successfully", null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteApplicationType(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        applicationTypeService.deleteApplicationType(id);
        return ResponseHelper.success("Application type deleted successfully", null);
    }
}
