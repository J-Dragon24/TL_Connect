package com.tl_connect.dev.modules.tuition.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.tuition.entity.TuitionInvoice;
import com.tl_connect.dev.modules.tuition.projection.TuitionInvoiceRow;
import com.tl_connect.dev.modules.tuition.projection.TuitionInvoiceView;
import org.springframework.data.repository.query.Param;

@Repository
public interface TuitionInvoiceRepository extends JpaRepository<TuitionInvoice, Long> {

    Optional<TuitionInvoice> findByStudentIdAndSemesterId(Long studentId, Long semesterId);

    @Query(value = """
            SELECT 
                t.id as id,
                s.semester_code as semesterCode,
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

    @Query(value = """
            SELECT 
                t.id as id,
                s.semester_code as semesterCode,
                st.student_name as studentName,
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
                s.semester_code as semesterCode,
                st.student_name as studentName,
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

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO tuition_invoices (student_id, semester_id, due_date, total_amount, final_amount, status)
        SELECT 
            s.student_id,
            s.semester_id,
            CURRENT_DATE + INTERVAL '15 days',
            SUM(sub.credits * sub.coefficient * cfg.base_price_per_credit),
            SUM(sub.credits * sub.coefficient * cfg.base_price_per_credit),
            'UNPAID'
        FROM student_course_classes s
        JOIN subjects sub ON s.subject_id = sub.id
        JOIN tuition_fee_configs cfg 
            ON CURRENT_DATE BETWEEN cfg.effective_from AND cfg.effective_to
        WHERE s.semester_id = :semesterId
        AND s.status = 'ENROLLED'
        AND NOT EXISTS (
            SELECT 1 FROM tuition_invoices ti 
            WHERE ti.student_id = s.student_id 
            AND ti.semester_id = s.semester_id
        )
        GROUP BY s.student_id, s.semester_id
    """, nativeQuery = true)
    void insertInvoicesBySemester(Long semesterId);
}
