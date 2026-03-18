package com.tl_connect.dev.modules.tuition;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.exception.ForbiddenException;
import com.tl_connect.dev.modules.tuition.dto.TuitionInvoiceDTO;
import com.tl_connect.dev.modules.tuition.dto.TuitionInvoiceDetailDTO;
import com.tl_connect.dev.modules.tuition.dto.TuitionItemDTO;
import com.tl_connect.dev.modules.tuition.repository.TuitionInvoiceItemRepository;
import com.tl_connect.dev.modules.tuition.repository.TuitionInvoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TuitionService {
    private final TuitionInvoiceRepository tuitionInvoiceRepository;
    private final TuitionInvoiceItemRepository tuitionInvoiceItemRepository;

    public List<TuitionInvoiceDTO> getTuitionInvoices(Long studentId) {
        return tuitionInvoiceRepository.findAllByStudentId(studentId);
    }

    public TuitionInvoiceDetailDTO getInvoiceDetail(Long invoiceId, Long studentId) {

        TuitionInvoiceDTO invoice = tuitionInvoiceRepository.findByIdAndStudentId(invoiceId, studentId)
                .orElseThrow(() -> new ForbiddenException("You don't have permission to access this invoice"));

        List<TuitionItemDTO> items = tuitionInvoiceItemRepository.findItemsByInvoiceId(invoiceId);

        TuitionInvoiceDetailDTO tuitionInvoiceDetail = TuitionInvoiceDetailDTO.builder()
                .invoiceId(invoice.getInvoiceId())
                .semesterName(invoice.getSemesterName())
                .items(items)
                .totalAmount(invoice.getTotalAmount())
                .finalAmount(invoice.getFinalAmount())
                .status(invoice.getStatus())
                .dueDate(invoice.getDueDate())
                .build();

        return tuitionInvoiceDetail;
    }
}
