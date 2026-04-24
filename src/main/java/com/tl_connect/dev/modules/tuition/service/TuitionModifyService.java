package com.tl_connect.dev.modules.tuition.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.core.common.enums.TuitionStatus;
import com.tl_connect.dev.core.common.exception.BadRequestException;
import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.exception.NotFoundException;
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

        try{
            return tuitionInvoiceRepository.generateInvoices(request.getSemesterId());
        }catch(Exception e){
            throw new BadRequestException("Failed to generate invoice: " + e.getMessage());
        }
    }

    @Transactional
    public Long regenerateInvoice(Long invoiceId) {
        TuitionInvoice invoice = tuitionInvoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new NotFoundException("Invoice not found"));
        
        if(invoice.getStatus() == TuitionStatus.PAID) {
            throw new InvalidInputException("Invoice is paid");
        }

        try{
            Long newId = tuitionInvoiceRepository.generateSingleInvoice(invoice.getStudentId(), invoice.getSemesterId());
            if(newId != null) {
                tuitionInvoiceRepository.cancelInvoiceById(invoiceId);
            }
            return newId;
        }catch(Exception e){
            throw new BadRequestException("Failed to generate invoice: " + e.getMessage());
        }
    }
    
    @Transactional 
    public void deleteInvoice(Long invoiceId) {
        try{
            tuitionInvoiceRepository.cancelInvoiceById(invoiceId);
        }catch(Exception e){
            throw new BadRequestException("Failed to delete invoice: " + e.getMessage());
        }
    }
}
