package com.tl_connect.dev.modules.tuition.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.modules.tuition.dto.TuitionInvoiceDTO;
import com.tl_connect.dev.modules.tuition.dto.TuitionInvoiceDetailDTO;
import com.tl_connect.dev.modules.tuition.service.interfaces.TuitionService;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.common.types.JwtUserInfo;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tuition")
@RequiredArgsConstructor
public class TuitionController {

    private final TuitionService tuitionService;

    @GetMapping()
    public ResponseEntity<?> getInvoices(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        List<TuitionInvoiceDTO> tuitionInvoices = tuitionService.getTuitionInvoicesByStudent(studentId);
        return ResponseHelper.success("Get tuition invoices successfully", tuitionInvoices);
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<?> getInvoiceDetail(Authentication authentication, @PathVariable Long invoiceId) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        TuitionInvoiceDetailDTO tuitionInvoiceDetail = tuitionService.getInvoiceDetailByStudent(invoiceId, studentId);
        return ResponseHelper.success("Get tuition invoice detail successfully", tuitionInvoiceDetail);
    }
}