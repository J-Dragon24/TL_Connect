package com.tl_connect.dev.modules.tuition.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.tuition.dto.TuitionInvoiceAdmDTO;
import com.tl_connect.dev.modules.tuition.dto.TuitionInvoiceDTO;
import com.tl_connect.dev.modules.tuition.dto.TuitionInvoiceDetailAdmDTO;
import com.tl_connect.dev.modules.tuition.dto.TuitionInvoiceDetailDTO;
import com.tl_connect.dev.modules.tuition.dto.TuitionItemDTO;
import com.tl_connect.dev.modules.tuition.projection.TuitionInvoiceRow;
import com.tl_connect.dev.modules.tuition.projection.TuitionInvoiceView;
import com.tl_connect.dev.modules.tuition.projection.TuitionItemProjection;
import com.tl_connect.dev.modules.tuition.repository.TuitionInvoiceItemRepository;
import com.tl_connect.dev.modules.tuition.repository.TuitionInvoiceRepository;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.exception.ForbiddenException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TuitionService {
    private final TuitionInvoiceRepository tuitionInvoiceRepository;
    private final TuitionInvoiceItemRepository tuitionInvoiceItemRepository;

    public List<TuitionInvoiceDTO> getTuitionInvoicesByStudent(Long studentId) {
        return tuitionInvoiceRepository.findAllByStudentId(studentId).stream()
                .map(this::toTuitionInvoiceDTO).toList();
    }

    public TuitionInvoiceDetailDTO getInvoiceDetailByStudent(Long invoiceId, Long studentId) {

        TuitionInvoiceDTO invoice = tuitionInvoiceRepository.findByIdAndStudentId(invoiceId, studentId)
                .map(this::toTuitionInvoiceDTO)
                .orElseThrow(() -> new ForbiddenException("You don't have permission to access this invoice"));

        List<TuitionItemProjection> items = tuitionInvoiceItemRepository.findItemsByInvoiceId(invoiceId, studentId);

        List<TuitionItemDTO> tuitionItems = items.stream().map(item -> TuitionItemDTO.builder()
                .id(item.getId())
                .subjectName(item.getSubjectName())
                .subjectCode(item.getSubjectCode())
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




    public PagedResponse<TuitionInvoiceAdmDTO> getAllTuitionInvoices(Pageable pageable, Long semesterId) {
        Page<TuitionInvoiceRow> page = tuitionInvoiceRepository.findAllBySemesterId(semesterId, pageable);
        return new PagedResponse<>(
            page.getContent().stream().map(this::toTuitionInvoiceAdmDTO).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }

    public TuitionInvoiceDetailAdmDTO getTuitionInvoiceDetail(Long invoiceId) {
        TuitionInvoiceRow invoice = tuitionInvoiceRepository.findByTuitionInvoiceId(invoiceId)
                .orElseThrow(() -> new NotFoundException("Invoice not found"));

        List<TuitionItemProjection> items = tuitionInvoiceItemRepository.findItemsByInvoiceId(invoiceId, invoice.getStudentId());

        List<TuitionItemDTO> tuitionItems = items.stream().map(item -> TuitionItemDTO.builder()
                .id(item.getId())
                .subjectName(item.getSubjectName())
                .credits(item.getCredits())
                .pricePerCredit(item.getPricePerCredit())
                .coefficient(item.getCoefficient())
                .amount(item.getAmount())
                .isRetake(item.getIsRetake())
                .build()).toList();

        TuitionInvoiceDetailAdmDTO tuitionInvoiceDetail = TuitionInvoiceDetailAdmDTO.builder()
                .invoiceId(invoice.getId())
                .semesterCode(invoice.getSemesterCode())
                .studentName(invoice.getStudentName())
                .studentCode(invoice.getStudentCode())
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
                .invoiceId(view.getId())
                .semesterName(view.getSemesterName())
                .totalAmount(view.getTotalAmount())
                .finalAmount(view.getFinalAmount())
                .status(view.getStatus())
                .dueDate(view.getDueDate())
                .build();
    }

    private TuitionInvoiceAdmDTO toTuitionInvoiceAdmDTO(TuitionInvoiceRow row) {
        return TuitionInvoiceAdmDTO.builder()
                .invoiceId(row.getId())
                .semesterCode(row.getSemesterCode())
                .studentName(row.getStudentName())
                .studentCode(row.getStudentCode())
                .totalAmount(row.getTotalAmount())
                .finalAmount(row.getFinalAmount())
                .status(row.getStatus())
                .dueDate(row.getDueDate())
                .build();
    }
}
