package com.tl_connect.dev.modules.tuition.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.tuition.entity.TuitionInvoiceItem;
import com.tl_connect.dev.modules.tuition.projection.TuitionItemProjection;

@Repository
public interface TuitionInvoiceItemRepository extends JpaRepository<TuitionInvoiceItem, Long> {

    @Query(value = """
        SELECT 
            c.id as id,
            sub.subject_name as subjectName,
            ti.credits as credits,
            ti.price_per_credit as pricePerCredit,
            ti.coefficient as coefficient,
            ti.amount as amount,
            scc.is_retake as isRetake
        FROM tuition_invoice_items ti
        JOIN course_classes c ON ti.course_class_id = c.id
        JOIN subjects sub ON c.subject_id = sub.id
        LEFT JOIN student_course_classes scc 
            ON scc.course_class_id = c.id
        AND scc.student_id = :studentId
        WHERE ti.invoice_id = :invoiceId
    """, nativeQuery = true)
    List<TuitionItemProjection> findItemsByInvoiceId(Long invoiceId, Long studentId);

    @Query(value = """
        SELECT 
            c.id as id,
            sub.subject_name as subjectName,
            ti.credits as credits,
            ti.price_per_credit as pricePerCredit,
            ti.coefficient as coefficient,
            ti.amount as amount,
            scc.is_retake as isRetake
        FROM tuition_invoice_items ti
        JOIN course_classes c ON ti.course_class_id = c.id
        JOIN subjects sub ON c.subject_id = sub.id
        LEFT JOIN student_course_classes scc 
            ON scc.course_class_id = c.id
        WHERE ti.invoice_id = :invoiceId
    """, nativeQuery = true)
    List<TuitionItemProjection> findItemsByInvoiceId(Long invoiceId);

    List<TuitionInvoiceItem> findAllByInvoiceId(Long invoiceId);

}
