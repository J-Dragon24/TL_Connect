package com.tl_connect.dev.modules.tuition.service.interfaces;

import com.tl_connect.dev.modules.tuition.dto.GenerateInvoiceReqDTO;
import com.tl_connect.dev.modules.tuition.entity.TuitionInvoice;
import com.tl_connect.dev.modules.tuition.entity.TuitionTransaction;
import com.tl_connect.dev.shared.common.enums.TuitionStatus;

public interface TuitionModifyService {

    Long generateInvoices(GenerateInvoiceReqDTO request);

    Long regenerateInvoice(Long invoiceId);

    void deleteInvoice(Long invoiceId);

    void updateTuitionStatusByIdAndStudentId(Long invoiceId, Long studentId, TuitionStatus status);
    
    void saveTuition(TuitionInvoice tuitionInvoice);

    void saveTuitionTransaction(TuitionTransaction tuitionTransaction);
}
