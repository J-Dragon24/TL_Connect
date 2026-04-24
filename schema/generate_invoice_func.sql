CREATE OR REPLACE FUNCTION generate_tuition_invoices(p_semester_id BIGINT)
RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
    v_count BIGINT;
BEGIN

PERFORM pg_advisory_xact_lock(p_semester_id);

WITH valid_enrollments AS (
    SELECT 
        s.student_id,
        s.semester_id,
        s.course_class_id,
        sub.credits,
        sub.coefficient,
        cfg.base_price_per_credit,
        (sub.credits * sub.coefficient * cfg.base_price_per_credit) AS amount
    FROM student_course_classes s
    JOIN subjects sub ON s.subject_id = sub.id
    JOIN students st ON s.student_id = st.id
    JOIN student_classes sc ON st.student_class_id = sc.id
    JOIN semesters sem ON sem.id = s.semester_id
    JOIN tuition_fee_configs cfg 
        ON cfg.cohort = sc.start_year
        AND cfg.academic_year = sem.academic_years
    WHERE s.semester_id = p_semester_id
    AND s.status = 'ENROLLED'
),

inserted_invoices AS (
    INSERT INTO tuition_invoices (
        student_id,
        semester_id,
        due_date,
        total_amount,
        final_amount,
        status,
        created_at,
        updated_at
    )
    SELECT 
        ve.student_id,
        ve.semester_id,
        CURRENT_DATE + INTERVAL '15 days',
        SUM(ve.amount),
        SUM(ve.amount),
        'UNPAID',
        now(),
        now()
    FROM valid_enrollments ve
    WHERE NOT EXISTS (
        SELECT 1 FROM tuition_invoices ti
        WHERE ti.student_id = ve.student_id
        AND ti.semester_id = ve.semester_id
        AND ti.status != 'CANCELLED'
    )
    GROUP BY ve.student_id, ve.semester_id
    RETURNING id, student_id, semester_id
)

INSERT INTO tuition_invoice_items (
    invoice_id,
    course_class_id,
    credits,
    price_per_credit,
    amount,
    created_at,
    updated_at
)
SELECT 
    inv.id,
    ve.course_class_id,
    ve.credits,
    ve.base_price_per_credit,
    ve.amount,
    now(),
    now()
FROM valid_enrollments ve
JOIN inserted_invoices inv
    ON inv.student_id = ve.student_id
    AND inv.semester_id = ve.semester_id
WHERE NOT EXISTS (
    SELECT 1 FROM tuition_invoice_items ti
    WHERE ti.invoice_id = inv.id
    AND ti.course_class_id = ve.course_class_id
);

GET DIAGNOSTICS v_count = ROW_COUNT;
RETURN v_count;

END;
$$;

CREATE OR REPLACE FUNCTION generate_single_tuition_invoice(
    p_student_id BIGINT,
    p_semester_id BIGINT
)
RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
    v_invoice_id BIGINT;
BEGIN

PERFORM pg_advisory_xact_lock(p_student_id * 100000 + p_semester_id);

WITH valid_enrollments AS (
    SELECT 
        s.student_id,
        s.semester_id,
        s.course_class_id,
        sub.credits,
        sub.coefficient,
        cfg.base_price_per_credit,
        (sub.credits * sub.coefficient * cfg.base_price_per_credit) AS amount
    FROM student_course_classes s
    JOIN students st ON st.id = s.student_id
    JOIN student_classes sc ON sc.id = st.student_class_id
    JOIN semesters sem ON sem.id = s.semester_id
    JOIN subjects sub ON s.subject_id = sub.id
    JOIN tuition_fee_configs cfg 
        ON cfg.cohort = sc.start_year 
        AND cfg.academic_year = sem.academic_years
    WHERE s.student_id = p_student_id
    AND s.semester_id = p_semester_id
    AND s.status = 'ENROLLED'
),

inserted_invoice AS (
    INSERT INTO tuition_invoices (
        student_id,
        semester_id,
        due_date,
        total_amount,
        final_amount,
        status,
        created_at,
        updated_at
    )
    SELECT 
        p_student_id,
        p_semester_id,
        CURRENT_DATE + INTERVAL '15 days',
        SUM(ve.amount),
        SUM(ve.amount),
        'UNPAID',
        now(),
        now()
    FROM valid_enrollments ve
    WHERE NOT EXISTS (
        SELECT 1 FROM tuition_invoices ti
        WHERE ti.student_id = p_student_id
        AND ti.semester_id = p_semester_id
        AND ti.status != 'CANCELLED'
    )
	HAVING COUNT(*) > 0
    RETURNING id
),


insert_items AS (
    INSERT INTO tuition_invoice_items (
        invoice_id,
        course_class_id,
        credits,
        price_per_credit,
        amount,
        created_at,
        updated_at
    )
    SELECT 
        ii.id,
        ve.course_class_id,
        ve.credits,
        ve.base_price_per_credit,
        ve.amount,
        now(),
        now()
    FROM valid_enrollments ve
    JOIN inserted_invoice ii ON true
    ON CONFLICT (invoice_id, course_class_id) DO NOTHING
)

SELECT id INTO v_invoice_id FROM inserted_invoice LIMIT 1;

RETURN v_invoice_id;

END;
$$;