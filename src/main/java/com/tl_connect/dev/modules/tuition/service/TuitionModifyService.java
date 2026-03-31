package com.tl_connect.dev.modules.tuition.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.tuition.dto.GenerateInvoiceReqDTO;
import com.tl_connect.dev.modules.tuition.entity.TuitionInvoice;
import com.tl_connect.dev.modules.tuition.repository.TuitionInvoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TuitionModifyService {
    
    private final TuitionInvoiceRepository tuitionInvoiceRepository;

    @Transactional
    public Long generateInvoices(GenerateInvoiceReqDTO request) {
        return tuitionInvoiceRepository.generateInvoices(request.getSemesterId());
    }

    @Transactional
    public Long regenerateInvoice(Long invoiceId) {
        TuitionInvoice invoice = tuitionInvoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice not found"));
       
        Long newId = tuitionInvoiceRepository.generateSingleInvoice(invoice.getStudentId(), invoice.getSemesterId());

        if(newId != null) {
            tuitionInvoiceRepository.cancelInvoiceById(invoiceId);
        }

        return newId;
    }
    
    @Transactional 
    public void deleteInvoice(Long invoiceId) {
        tuitionInvoiceRepository.cancelInvoiceById(invoiceId);
    }
}
