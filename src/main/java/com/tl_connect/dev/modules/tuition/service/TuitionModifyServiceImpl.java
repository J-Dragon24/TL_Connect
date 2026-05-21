package com.tl_connect.dev.modules.tuition.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.tuition.dto.GenerateInvoiceReqDTO;
import com.tl_connect.dev.modules.tuition.entity.TuitionInvoice;
import com.tl_connect.dev.modules.tuition.entity.TuitionTransaction;
import com.tl_connect.dev.modules.tuition.repository.TuitionInvoiceRepository;
import com.tl_connect.dev.modules.tuition.repository.TuitionTransactionRepository;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.enums.TuitionStatus;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.modules.tuition.service.interfaces.TuitionModifyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TuitionModifyServiceImpl implements TuitionModifyService {
    
    private final TuitionInvoiceRepository tuitionInvoiceRepository;
    private final TuitionTransactionRepository tuitionTransactionRepository;

    @Transactional
    public Long generateInvoices(GenerateInvoiceReqDTO request) {

        try{
            return tuitionInvoiceRepository.generateInvoices(request.getSemesterId());
        }catch(Exception e){
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to generate invoice: " + e.getMessage());
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
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to generate invoice: " + e.getMessage());
        }
    }
    
    @Transactional 
    public void deleteInvoice(Long invoiceId) {
        try{
            tuitionInvoiceRepository.cancelInvoiceById(invoiceId);
        }catch(Exception e){
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to delete invoice: " + e.getMessage());
        }
    }

    @Transactional
    public void updateTuitionStatusByIdAndStudentId(Long invoiceId, Long studentId, TuitionStatus status) {
        TuitionInvoice tuitionInvoice = tuitionInvoiceRepository.findTuitionByIdAndStudentId(invoiceId, studentId)
            .orElseThrow(() -> new NotFoundException("Invoice not found"));
        
        if(tuitionInvoice.getStatus() == TuitionStatus.PAID || tuitionInvoice.getStatus() == TuitionStatus.CANCELLED) {
            throw new InvalidInputException("Invoice is paid or cancelled");
        }

        try{
            tuitionInvoice.updateStatus(status);
            tuitionInvoiceRepository.save(tuitionInvoice);
        }catch(Exception e){
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Failed to update tuition status: " + e.getMessage());
        }
    }

    public void saveTuitionTransaction(TuitionTransaction tuitionTransaction) {
        tuitionTransactionRepository.save(tuitionTransaction);
    }

    public void saveTuition(TuitionInvoice tuitionInvoice) {
        tuitionInvoiceRepository.save(tuitionInvoice);
    }
}
