package com.tl_connect.dev.modules.tuition.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.tuition.entity.TuitionInvoiceItem;
import com.tl_connect.dev.modules.tuition.dto.TuitionItemDTO;

@Repository
public interface TuitionInvoiceItemRepository extends JpaRepository<TuitionInvoiceItem, Long> {

    @Query("""
        SELECT new com.example.dto.TuitionItemDTO(
            c.id,
            sub.subjectName,
            ti.credits,
            ti.pricePerCredit,
            ti.coefficient,
            ti.amount,
            ti.isRetake
        )
        FROM TuitionInvoiceItem ti
        JOIN CourseClass c ON ti.courseClassId = c.id
        JOIN Subject sub ON c.subjectId = sub.id
        WHERE ti.invoiceId = :invoiceId
    """)
    List<TuitionItemDTO> findItemsByInvoiceId(Long invoiceId);
}
