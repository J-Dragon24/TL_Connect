package com.tl_connect.dev.modules.tuition.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.tuition.dto.TuitionInvoiceDTO;
import com.tl_connect.dev.modules.tuition.entity.TuitionInvoice;

@Repository
public interface TuitionInvoiceRepository extends JpaRepository<TuitionInvoice, Long> {

    @Query("""
            SELECT 
                new com.tl_connect.dev.modules.tuition.dto.TuitionInvoiceDTO(
                    t.id,
                    s.semesterName,
                    t.totalAmount,
                    t.finalAmount,
                    t.status,
                    t.dueDate
                )
            FROM TuitionInvoice t
            JOIN Semester s ON t.semesterId = s.id
            WHERE t.studentId = :studentId
            ORDER BY s.startDate DESC
    """)
    List<TuitionInvoiceDTO> findAllByStudentId(Long studentId);

    @Query("""
            SELECT 
                new com.tl_connect.dev.modules.tuition.dto.TuitionInvoiceDTO(
                    t.id,
                    s.semesterName,
                    t.totalAmount,
                    t.finalAmount,
                    t.status,
                    t.dueDate
                )
            FROM TuitionInvoice t
            JOIN Semester s ON t.semesterId = s.id
            WHERE t.id = :invoiceId AND t.studentId = :studentId
    """)
    Optional<TuitionInvoiceDTO> findByIdAndStudentId(Long invoiceId, Long studentId);
}
