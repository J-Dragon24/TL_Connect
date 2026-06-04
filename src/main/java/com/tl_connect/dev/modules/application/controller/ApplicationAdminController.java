package com.tl_connect.dev.modules.application.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.modules.application.dto.ApplicationDTO;
import com.tl_connect.dev.modules.application.dto.DetailApplicationDTO;
import com.tl_connect.dev.modules.application.dto.UpdateApplicationDTO;
import com.tl_connect.dev.modules.application.service.interfaces.ApplicationService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.common.types.JwtUserInfo;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/application")
@RequiredArgsConstructor
public class ApplicationAdminController {
    
    private final ApplicationService applicationService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllApplication(Authentication authentication, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        PagedResponse<ApplicationDTO> applications = applicationService.getAllApplication(pageable);
        return ResponseHelper.success("List of applications", applications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getApplicationById(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        DetailApplicationDTO application = applicationService.getDetailApplication(id);
        return ResponseHelper.success("Application", application);
    }

    @PostMapping("/update-status/{id}")
    public ResponseEntity<?> updateStatusApplication(Authentication authentication, @PathVariable Long id, @RequestBody UpdateApplicationDTO req) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        applicationService.updateStatusApplication(id, req);
        return ResponseHelper.success("Application status updated successfully", null);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteApplication(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        applicationService.deleteApplication(id);
        return ResponseHelper.success("Application deleted successfully", null);
    }
}
