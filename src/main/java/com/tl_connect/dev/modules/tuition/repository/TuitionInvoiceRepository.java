package com.tl_connect.dev.modules.tuition.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.tuition.entity.TuitionInvoice;
import com.tl_connect.dev.modules.tuition.projection.TuitionInvoiceRow;
import com.tl_connect.dev.modules.tuition.projection.TuitionInvoiceView;
import org.springframework.data.repository.query.Param;

@Repository
public interface TuitionInvoiceRepository extends JpaRepository<TuitionInvoice, Long> {

    Optional<TuitionInvoice> findByStudentIdAndSemesterId(Long studentId, Long semesterId);

    @Query(value = """
            SELECT 
                t.id as invoiceId,
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
                t.id as invoiceId,
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

    @Query(value = """
            SELECT 
                t.id as id,
                s.semester_code as semesterCode,
                st.full_name as studentName,
                st.student_code as studentCode,
                t.total_amount as totalAmount,
                t.final_amount as finalAmount,
                t.status as status,
                t.due_date as dueDate
            FROM tuition_invoices t
            JOIN semesters s ON t.semester_id = s.id
            JOIN students st ON t.student_id = st.id
            WHERE t.semester_id = :semesterId
            ORDER BY t.created_at DESC
            """,
            countQuery = """
                    SELECT 
                        COUNT(t.id)
                    FROM tuition_invoices t
                    JOIN semesters s ON t.semester_id = s.id
                    JOIN students st ON t.student_id = st.id
                    WHERE t.semester_id = :semesterId
                    """,
            nativeQuery = true)
    Page<TuitionInvoiceRow> findAllBySemesterId(@Param("semesterId") Long semesterId, Pageable pageable);

    @Query(value = """
            SELECT 
                t.id as id,
                st.id as studentId,
                s.semester_code as semesterCode,
                st.full_name as studentName,
                st.student_code as studentCode,
                t.total_amount as totalAmount,
                t.final_amount as finalAmount,
                t.status as status,
                t.due_date as dueDate
            FROM tuition_invoices t
            JOIN semesters s ON t.semester_id = s.id
            JOIN students st ON t.student_id = st.id
            WHERE t.id = :invoiceId
            """, nativeQuery = true)
    Optional<TuitionInvoiceRow> findByTuitionInvoiceId(@Param("invoiceId") Long invoiceId);

    @Query(value = "SELECT generate_tuition_invoices(:semesterId)", nativeQuery = true)
    Long generateInvoices(@Param("semesterId") Long semesterId);

    @Modifying
    @Query(value = """
        UPDATE tuition_invoices ti
        SET status = 'CANCELLED',
            updated_at = CURRENT_TIMESTAMP
        WHERE ti.id = :invoiceId
        AND ti.status != 'CANCELLED'
    """, nativeQuery = true)
    void cancelInvoiceById(@Param("invoiceId") Long invoiceId);

    @Query(value = """
        SELECT generate_single_tuition_invoice(:studentId, :semesterId)
    """, nativeQuery = true)
    Long generateSingleInvoice(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId);
}
