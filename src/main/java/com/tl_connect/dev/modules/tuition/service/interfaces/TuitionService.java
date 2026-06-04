package com.tl_connect.dev.modules.tuition.service.interfaces;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.tuition.dto.TuitionInvoiceDTO;
import com.tl_connect.dev.modules.tuition.dto.TuitionInvoiceDetailDTO;
import com.tl_connect.dev.modules.tuition.entity.TuitionInvoice;
import com.tl_connect.dev.modules.tuition.entity.TuitionInvoiceItem;
import com.tl_connect.dev.modules.tuition.projection.TuitionInvoiceView;
import com.tl_connect.dev.modules.tuition.dto.TuitionInvoiceAdmDTO;
import com.tl_connect.dev.modules.tuition.dto.TuitionInvoiceDetailAdmDTO;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

import java.util.List;

public interface TuitionService {

    List<TuitionInvoiceDTO> getTuitionInvoicesByStudent(Long studentId);

    TuitionInvoiceDetailDTO getInvoiceDetailByStudent(Long invoiceId, Long studentId);

    PagedResponse<TuitionInvoiceAdmDTO> getAllTuitionInvoices(Pageable pageable, Long semesterId);

    TuitionInvoiceDetailAdmDTO getTuitionInvoiceDetail(Long invoiceId);

    TuitionInvoiceView findByIdAndStudentId(Long invoiceId, Long studentId);

    TuitionInvoice findById(Long invoiceId);

    List<TuitionInvoiceItem> findAllItemsByInvoiceId(Long invoiceId);
}
