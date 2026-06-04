package com.tl_connect.dev.modules.tuition.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.modules.tuition.dto.GenerateInvoiceReqDTO;
import com.tl_connect.dev.modules.tuition.dto.TuitionInvoiceAdmDTO;
import com.tl_connect.dev.modules.tuition.dto.TuitionInvoiceDetailAdmDTO;
import com.tl_connect.dev.modules.tuition.service.interfaces.TuitionModifyService;
import com.tl_connect.dev.modules.tuition.service.interfaces.TuitionService;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/tuition")
@RequiredArgsConstructor
public class TuitionControllerAdmin {
    
    private final TuitionModifyService tuitionModifyService;
    private final TuitionService tuitionService;

    @GetMapping("/invoices")
    public ResponseEntity<?> getAllTuitionInvoices(@RequestParam Long semesterId, Pageable pageable) {
        PagedResponse<TuitionInvoiceAdmDTO> tuitionInvoices = tuitionService.getAllTuitionInvoices(pageable, semesterId);
        return ResponseHelper.success("Get tuition invoices successfully", tuitionInvoices);
    }

    @GetMapping("/invoices/{invoiceId}")
    public ResponseEntity<?> getTuitionInvoiceDetail(@PathVariable Long invoiceId) {
        TuitionInvoiceDetailAdmDTO tuitionInvoiceDetail = tuitionService.getTuitionInvoiceDetail(invoiceId);
        return ResponseHelper.success("Get tuition invoice detail successfully", tuitionInvoiceDetail);
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateInvoices(@Valid @RequestBody GenerateInvoiceReqDTO request) {
        Long count = tuitionModifyService.generateInvoices(request);
        return ResponseHelper.success("Generate invoices successfully", count);
    }

    @PostMapping("/regenerate/{invoiceId}")
    public ResponseEntity<?> regenerateInvoice(@PathVariable Long invoiceId) {
        Long newId = tuitionModifyService.regenerateInvoice(invoiceId);
        return ResponseHelper.success("Regenerate invoice successfully", newId);
    }

    @PostMapping("/delete/{invoiceId}")
    public ResponseEntity<?> deleteInvoice(@PathVariable Long invoiceId) {
        tuitionModifyService.deleteInvoice(invoiceId);
        return ResponseHelper.success("Delete invoice successfully", null);
    }
}
