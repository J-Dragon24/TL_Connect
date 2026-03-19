package com.tl_connect.dev.modules.tuition;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.exception.ForbiddenException;
import com.tl_connect.dev.modules.tuition.dto.TuitionInvoiceDTO;
import com.tl_connect.dev.modules.tuition.dto.TuitionInvoiceDetailDTO;
import com.tl_connect.dev.modules.tuition.dto.TuitionItemDTO;
import com.tl_connect.dev.modules.tuition.projection.TuitionInvoiceView;
import com.tl_connect.dev.modules.tuition.projection.TuitionItemProjection;
import com.tl_connect.dev.modules.tuition.repository.TuitionInvoiceItemRepository;
import com.tl_connect.dev.modules.tuition.repository.TuitionInvoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TuitionService {
    private final TuitionInvoiceRepository tuitionInvoiceRepository;
    private final TuitionInvoiceItemRepository tuitionInvoiceItemRepository;

    public List<TuitionInvoiceDTO> getTuitionInvoices(Long studentId) {
        return tuitionInvoiceRepository.findAllByStudentId(studentId).stream()
                .map(this::toTuitionInvoiceDTO).toList();
    }

    public TuitionInvoiceDetailDTO getInvoiceDetail(Long invoiceId, Long studentId) {

        TuitionInvoiceDTO invoice = tuitionInvoiceRepository.findByIdAndStudentId(invoiceId, studentId)
                .map(this::toTuitionInvoiceDTO)
                .orElseThrow(() -> new ForbiddenException("You don't have permission to access this invoice"));

        List<TuitionItemProjection> items = tuitionInvoiceItemRepository.findItemsByInvoiceId(invoiceId, studentId);

        List<TuitionItemDTO> tuitionItems = items.stream().map(item -> TuitionItemDTO.builder()
                .id(item.getId())
                .subjectName(item.getSubjectName())
                .credits(item.getCredits())
                .pricePerCredit(item.getPricePerCredit())
                .coefficient(item.getCoefficient())
                .amount(item.getAmount())
                .isRetake(item.getIsRetake())
                .build()).toList();

        TuitionInvoiceDetailDTO tuitionInvoiceDetail = TuitionInvoiceDetailDTO.builder()
                .invoiceId(invoice.getInvoiceId())
                .semesterName(invoice.getSemesterName())
                .items(tuitionItems)
                .totalAmount(invoice.getTotalAmount())
                .finalAmount(invoice.getFinalAmount())
                .status(invoice.getStatus())
                .dueDate(invoice.getDueDate())
                .build();

        return tuitionInvoiceDetail;
    }

    private TuitionInvoiceDTO toTuitionInvoiceDTO(TuitionInvoiceView view) {
        return TuitionInvoiceDTO.builder()
                .invoiceId(view.getInvoiceId())
                .semesterName(view.getSemesterName())
                .totalAmount(view.getTotalAmount())
                .finalAmount(view.getFinalAmount())
                .status(view.getStatus())
                .dueDate(view.getDueDate())
                .build();
    }
}
