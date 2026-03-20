package com.tl_connect.dev.modules.tuition.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.tuition.entity.TuitionInvoice;
import com.tl_connect.dev.modules.tuition.projection.TuitionInvoiceView;
import org.springframework.data.repository.query.Param;

@Repository
public interface TuitionInvoiceRepository extends JpaRepository<TuitionInvoice, Long> {

    @Query(value = """
            SELECT 
                t.id as id,
                s.semester_name as semesterName,
                t.total_amount as totalAmount,
                t.final_amount as finalAmount,
                t.status as status,
                t.due_date as dueDate
            FROM tuition_invoices t
            JOIN semesters s ON t.semester_id = s.id
            WHERE t.student_id = :studentId
            ORDER BY s.start_date DESC
            """, nativeQuery = true)
    List<TuitionInvoiceView> findAllByStudentId(@Param("studentId") Long studentId);

    @Query(value = """
            SELECT 
                t.id as id,
                s.semester_name as semesterName,
                t.total_amount as totalAmount,
                t.final_amount as finalAmount,
                t.status as status,
                t.due_date as dueDate
            FROM tuition_invoices t
            JOIN semesters s ON t.semester_id = s.id
            WHERE t.id = :invoiceId AND t.student_id = :studentId
        """, nativeQuery = true)
    Optional<TuitionInvoiceView> findByIdAndStudentId(@Param("invoiceId") Long invoiceId, @Param("studentId") Long studentId);
}
