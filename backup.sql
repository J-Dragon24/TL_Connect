--
-- PostgreSQL database dump
--

\restrict Sc8bPxg2fs6m04lM3L5vnCMlJiS8ZSNXW2c10emG2tUAmvBtaajX2eg4fRBT1qE

-- Dumped from database version 17.7
-- Dumped by pg_dump version 17.7

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

-- *not* creating schema, since initdb creates it


--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON SCHEMA public IS '';


--
-- Name: generate_single_tuition_invoice(bigint, bigint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.generate_single_tuition_invoice(p_student_id bigint, p_semester_id bigint) RETURNS bigint
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


--
-- Name: generate_tuition_invoices(bigint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.generate_tuition_invoices(p_semester_id bigint) RETURNS bigint
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


--
-- Name: recalc_student_semester_summary(bigint); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.recalc_student_semester_summary(p_semester_id bigint) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
WITH gpa_calc AS (
    SELECT
        ssr.student_id,
        sm.study_program_id,
        ssr.semester_id,

        SUM(ssr.credits) AS credits_registered,

        SUM(CASE WHEN ssr.is_pass THEN ssr.credits ELSE 0 END) AS credits_passed,

        ROUND(
            SUM(ssr.score_4 * ssr.credits) FILTER (WHERE ssr.is_pass)
            / NULLIF(SUM(ssr.credits) FILTER (WHERE ssr.is_pass), 0),
        2) AS semester_gpa

    FROM student_subject_results ssr
    JOIN student_majors sm 
        ON sm.student_id = ssr.student_id AND sm.is_primary = TRUE

    WHERE ssr.semester_id = p_semester_id

    GROUP BY ssr.student_id, ssr.semester_id, sm.study_program_id
)

INSERT INTO student_semester_summaries (
    student_id,
    study_program_id,
    semester_id,
    credits_registered,
    credits_passed,
    semester_gpa,
    letter_gpa,
    conduct_score,
    activity_score,
    letter_activity_score,
    group_contribution,
    letter_group_contribution
)
SELECT
    student_id,
    study_program_id,
    semester_id,
    credits_registered,
    credits_passed,
    semester_gpa,

    CASE
        WHEN semester_gpa >= 3.6 THEN 'A'
        WHEN semester_gpa >= 3.2 THEN 'B+'
        WHEN semester_gpa >= 2.8 THEN 'B'
        WHEN semester_gpa >= 2.4 THEN 'C+'
        WHEN semester_gpa >= 2.0 THEN 'C'
        WHEN semester_gpa >= 1.5 THEN 'D+'
        WHEN semester_gpa >= 1.0 THEN 'D'
        ELSE 'F'
    END AS letter_gpa,

    0,
    0,
    NULL,
    0,
    NULL

FROM gpa_calc

ON CONFLICT (student_id, semester_id)
DO UPDATE SET
    credits_registered = EXCLUDED.credits_registered,
    credits_passed = EXCLUDED.credits_passed,
    semester_gpa = EXCLUDED.semester_gpa,
    letter_gpa = EXCLUDED.letter_gpa,
    updated_at = now();
END;
$$;


--
-- Name: sync_student_course_class_fields(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.sync_student_course_class_fields() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  SELECT subject_id, semester_id
  INTO NEW.subject_id, NEW.semester_id
  FROM course_classes
  WHERE id = NEW.course_class_id;

  RETURN NEW;
END;
$$;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: academic_advisors; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.academic_advisors (
    id bigint NOT NULL,
    lecturer_id bigint NOT NULL,
    student_class_id bigint NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: academic_advisors_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.academic_advisors ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.academic_advisors_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: academic_infos; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.academic_infos (
    id bigint NOT NULL,
    student_major_id bigint NOT NULL,
    cohort character varying(20) NOT NULL,
    "position" character varying(50),
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: academic_infos_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.academic_infos ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.academic_infos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: application_attachments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.application_attachments (
    id bigint NOT NULL,
    application_id bigint NOT NULL,
    file_key character varying(255) NOT NULL,
    original_filename character varying(255),
    file_size bigint,
    created_at timestamp without time zone DEFAULT now()
);


--
-- Name: application_attachments_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.application_attachments ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.application_attachments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: application_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.application_types (
    id bigint NOT NULL,
    code character varying(100) NOT NULL,
    name character varying(255),
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: application_types_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.application_types ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.application_types_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: attendances; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.attendances (
    id bigint NOT NULL,
    course_class_id bigint NOT NULL,
    session_id character varying(255) NOT NULL,
    student_id bigint NOT NULL,
    check_in_time timestamp without time zone NOT NULL,
    created_at timestamp without time zone DEFAULT now()
);


--
-- Name: attendances_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.attendances ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.attendances_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: class_schedules; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.class_schedules (
    id bigint NOT NULL,
    course_class_id bigint NOT NULL,
    day_of_week integer NOT NULL,
    start_period integer NOT NULL,
    end_period integer NOT NULL,
    start_time time without time zone NOT NULL,
    end_time time without time zone NOT NULL,
    room character varying(50),
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT class_schedules_check CHECK ((end_period >= start_period)),
    CONSTRAINT class_schedules_check1 CHECK ((end_time > start_time)),
    CONSTRAINT class_schedules_day_of_week_check CHECK (((day_of_week >= 1) AND (day_of_week <= 7)))
);


--
-- Name: class_schedules_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.class_schedules ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.class_schedules_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: course_classes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.course_classes (
    id bigint NOT NULL,
    lecturer_id bigint,
    subject_id bigint NOT NULL,
    semester_id bigint NOT NULL,
    class_code character varying(20) NOT NULL,
    class_name character varying(100) NOT NULL,
    capacity integer NOT NULL,
    enrolled_count integer DEFAULT 0 NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    version bigint DEFAULT 0 NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: course_classes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.course_classes ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.course_classes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: departments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.departments (
    id bigint NOT NULL,
    faculty_id bigint NOT NULL,
    department_code character varying(20) NOT NULL,
    department_name character varying(255) NOT NULL,
    is_active boolean DEFAULT true,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: departments_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.departments ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.departments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: emergency_contacts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.emergency_contacts (
    id bigint NOT NULL,
    student_id bigint NOT NULL,
    full_name character varying(100),
    phone_number character varying(20),
    address character varying(255),
    relationship character varying(255),
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: emergency_contacts_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.emergency_contacts ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.emergency_contacts_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: enrollment_periods; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.enrollment_periods (
    id bigint NOT NULL,
    semester_id bigint NOT NULL,
    start_time timestamp without time zone NOT NULL,
    end_time timestamp without time zone NOT NULL,
    max_credits integer DEFAULT 18 NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT valid_time_range CHECK ((end_time > start_time))
);


--
-- Name: enrollment_periods_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.enrollment_periods ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.enrollment_periods_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: exam_schedules; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.exam_schedules (
    id bigint NOT NULL,
    subject_id bigint NOT NULL,
    semester_id bigint NOT NULL,
    exam_date date NOT NULL,
    start_time time without time zone NOT NULL,
    end_time time without time zone NOT NULL,
    exam_room character varying(50) NOT NULL,
    exam_location character varying(100),
    exam_format character varying(50),
    exam_type character varying(50),
    note character varying(255),
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT exam_schedules_check CHECK ((end_time > start_time))
);


--
-- Name: exam_schedules_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.exam_schedules ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.exam_schedules_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: faculties; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.faculties (
    id bigint NOT NULL,
    faculty_code character varying(20) NOT NULL,
    faculty_name character varying(255) NOT NULL,
    is_active boolean DEFAULT true,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: faculties_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.faculties ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.faculties_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: feedback; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feedback (
    id bigint NOT NULL,
    oauth_user_id bigint,
    title text NOT NULL,
    category_id bigint,
    content text NOT NULL,
    app_version character varying(20),
    device_info text,
    status character varying(20) DEFAULT 'PENDING'::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    CONSTRAINT feedback_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'IN_PROGRESS'::character varying, 'RESOLVED'::character varying, 'REJECTED'::character varying])::text[])))
);


--
-- Name: feedback_attachments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feedback_attachments (
    id bigint NOT NULL,
    feedback_id bigint NOT NULL,
    file_key character varying(255) NOT NULL,
    original_filename character varying(255),
    file_size bigint,
    created_at timestamp without time zone DEFAULT now()
);


--
-- Name: feedback_attachments_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.feedback_attachments ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.feedback_attachments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: feedback_category; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.feedback_category (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    description text,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: feedback_category_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.feedback_category ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.feedback_category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: feedback_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.feedback ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.feedback_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: grade_scale; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grade_scale (
    id bigint NOT NULL,
    min_score numeric(4,2) NOT NULL,
    max_score numeric(4,2) NOT NULL,
    letter_grade character varying(2) NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: grade_scale_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.grade_scale ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.grade_scale_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: health_insurances; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.health_insurances (
    id bigint NOT NULL,
    student_id bigint NOT NULL,
    insurance_number character varying(20),
    provider character varying(100),
    valid_from date,
    valid_to date,
    registered_hospital character varying(255),
    status character varying(20) DEFAULT 'ACTIVE'::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT health_insurances_check CHECK ((valid_to >= valid_from)),
    CONSTRAINT health_insurances_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'EXPIRED'::character varying])::text[])))
);


--
-- Name: health_insurances_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.health_insurances ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.health_insurances_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: identity_cards; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.identity_cards (
    id bigint NOT NULL,
    student_id bigint NOT NULL,
    card_number character varying(20) NOT NULL,
    card_type character varying(20) DEFAULT 'CCCD'::character varying NOT NULL,
    issued_date date,
    issued_place character varying(100),
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT identity_cards_card_type_check CHECK (((card_type)::text = ANY ((ARRAY['CCCD'::character varying, 'CMND'::character varying])::text[])))
);


--
-- Name: identity_cards_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.identity_cards ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.identity_cards_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: lecturers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.lecturers (
    id bigint NOT NULL,
    oauth_user_id bigint,
    department_id bigint,
    full_name character varying(100),
    lecturer_code character varying(20) NOT NULL,
    phone_number character varying(20),
    email character varying(255),
    status character varying(20) DEFAULT 'ACTIVE'::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT lecturers_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying])::text[])))
);


--
-- Name: lecturers_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.lecturers ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.lecturers_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: majors; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.majors (
    id bigint NOT NULL,
    major_code character varying(20) NOT NULL,
    major_name character varying(100) NOT NULL,
    faculty_id bigint NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: majors_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.majors ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.majors_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: news; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.news (
    id bigint NOT NULL,
    title text,
    excerpt text,
    image_url character varying(255),
    image_key character varying(255),
    source text,
    publish_date date,
    news_url character varying(255),
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: news_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.news ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.news_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: notification_read; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.notification_read (
    id bigint NOT NULL,
    notification_id bigint NOT NULL,
    oauth_user_id bigint NOT NULL,
    read_at timestamp without time zone DEFAULT now()
);


--
-- Name: notification_read_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.notification_read ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.notification_read_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: notification_targets; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.notification_targets (
    id bigint NOT NULL,
    notification_id bigint NOT NULL,
    target_id bigint NOT NULL
);


--
-- Name: notification_targets_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.notification_targets ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.notification_targets_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: notification_templates; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.notification_templates (
    id bigint NOT NULL,
    code character varying(100) NOT NULL,
    name character varying(255) NOT NULL,
    content text NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: notification_templates_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.notification_templates ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.notification_templates_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: notifications; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.notifications (
    id bigint NOT NULL,
    title text NOT NULL,
    content text NOT NULL,
    created_by character varying(255) DEFAULT 'SYSTEM'::character varying,
    target_type character varying(20) DEFAULT 'GLOBAL'::character varying NOT NULL,
    reference_type character varying(20),
    is_important boolean DEFAULT false,
    deadline date,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT notifications_created_by_check CHECK (((created_by)::text = ANY ((ARRAY['SYSTEM'::character varying, 'FACULTY'::character varying, 'LECTURER'::character varying])::text[]))),
    CONSTRAINT notifications_reference_type_check CHECK (((reference_type)::text = ANY ((ARRAY['TUITION'::character varying, 'EXAM_SCHEDULE'::character varying])::text[]))),
    CONSTRAINT notifications_target_type_check CHECK (((target_type)::text = ANY ((ARRAY['GLOBAL'::character varying, 'FACULTY'::character varying, 'STUDENT_CLASS'::character varying, 'COURSE_CLASS'::character varying, 'STUDENT'::character varying])::text[])))
);


--
-- Name: notifications_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.notifications ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.notifications_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: oauth_users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.oauth_users (
    id bigint NOT NULL,
    user_uuid character varying(255) NOT NULL,
    display_name character varying(255),
    email character varying(255) NOT NULL,
    status character varying(20) DEFAULT 'ACTIVE'::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT oauth_users_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'BLOCKED'::character varying])::text[])))
);


--
-- Name: oauth_users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.oauth_users ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.oauth_users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: payment; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.payment (
    id bigint NOT NULL,
    invoice_id bigint NOT NULL,
    amount numeric(10,2) NOT NULL,
    provider character varying(20),
    transaction_code character varying(100) NOT NULL,
    provider_trans_id character varying(100),
    status character varying(20) DEFAULT 'PENDING'::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT payment_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'SUCCESS'::character varying, 'FAILED'::character varying, 'REFUND_PENDING'::character varying, 'REFUNDED'::character varying])::text[])))
);


--
-- Name: payment_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.payment ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.payment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.roles (
    id bigint NOT NULL,
    code character varying(50) NOT NULL,
    name character varying(100),
    is_active boolean DEFAULT true,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: roles_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.roles ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: semesters; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.semesters (
    id bigint NOT NULL,
    semester_name character varying(50) NOT NULL,
    semester_code character varying(20) NOT NULL,
    academic_years character varying(20) NOT NULL,
    semester_number integer NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    is_active boolean DEFAULT true,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT valid_date_range CHECK ((end_date > start_date))
);


--
-- Name: semesters_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.semesters ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.semesters_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: student_applications; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.student_applications (
    id bigint NOT NULL,
    student_id bigint NOT NULL,
    application_type_id bigint NOT NULL,
    content text,
    status character varying(20) DEFAULT 'PENDING'::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT student_applications_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying])::text[])))
);


--
-- Name: student_applications_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.student_applications ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.student_applications_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: student_classes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.student_classes (
    id bigint NOT NULL,
    class_code character varying(20) NOT NULL,
    major_id bigint NOT NULL,
    start_year integer NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: student_classes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.student_classes ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.student_classes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: student_contacts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.student_contacts (
    id bigint NOT NULL,
    student_id bigint NOT NULL,
    phone_number character varying(20),
    address character varying(255),
    email_personal character varying(255),
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: student_contacts_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.student_contacts ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.student_contacts_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: student_course_class_logs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.student_course_class_logs (
    id bigint NOT NULL,
    student_id bigint NOT NULL,
    course_class_id bigint NOT NULL,
    action character varying(20) DEFAULT 'REJECTED'::character varying NOT NULL,
    from_status character varying(20) DEFAULT NULL::character varying,
    to_status character varying(20) DEFAULT 'PENDING'::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    CONSTRAINT student_course_class_logs_action_check CHECK (((action)::text = ANY ((ARRAY['ENROLL'::character varying, 'DROP'::character varying, 'REJECTED'::character varying])::text[]))),
    CONSTRAINT student_course_class_logs_from_status_check CHECK (((from_status)::text = ANY ((ARRAY['PENDING'::character varying, 'ENROLLED'::character varying, 'DROPPED'::character varying, 'REJECTED'::character varying])::text[]))),
    CONSTRAINT student_course_class_logs_to_status_check CHECK (((to_status)::text = ANY ((ARRAY['PENDING'::character varying, 'ENROLLED'::character varying, 'DROPPED'::character varying, 'REJECTED'::character varying])::text[])))
);


--
-- Name: student_course_class_logs_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.student_course_class_logs ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.student_course_class_logs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: student_course_classes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.student_course_classes (
    id bigint NOT NULL,
    student_id bigint NOT NULL,
    course_class_id bigint NOT NULL,
    status character varying(20) DEFAULT 'PENDING'::character varying NOT NULL,
    subject_id bigint NOT NULL,
    semester_id bigint NOT NULL,
    is_retake boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT student_course_classes_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'ENROLLED'::character varying, 'DROPPED'::character varying, 'REJECTED'::character varying])::text[])))
);


--
-- Name: student_course_classes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.student_course_classes ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.student_course_classes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: student_exam_registrations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.student_exam_registrations (
    id bigint NOT NULL,
    student_id bigint NOT NULL,
    exam_schedule_id bigint NOT NULL,
    exam_attempt integer NOT NULL,
    attendance_status character varying(20) DEFAULT 'UPCOMING'::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT student_exam_registrations_attendance_status_check CHECK (((attendance_status)::text = ANY ((ARRAY['ATTENDED'::character varying, 'ABSENT'::character varying, 'UPCOMING'::character varying])::text[])))
);


--
-- Name: student_exam_registrations_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.student_exam_registrations ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.student_exam_registrations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: student_majors; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.student_majors (
    id bigint NOT NULL,
    student_id bigint NOT NULL,
    major_id bigint NOT NULL,
    study_program_id bigint NOT NULL,
    is_primary boolean DEFAULT true,
    start_year integer NOT NULL,
    end_year integer NOT NULL,
    status character varying(20) DEFAULT 'STUDYING'::character varying NOT NULL,
    CONSTRAINT student_majors_status_check CHECK (((status)::text = ANY ((ARRAY['STUDYING'::character varying, 'GRADUATED'::character varying, 'DROPPED'::character varying])::text[])))
);


--
-- Name: student_majors_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.student_majors ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.student_majors_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: student_semester_summaries; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.student_semester_summaries (
    id bigint NOT NULL,
    student_id bigint NOT NULL,
    study_program_id bigint NOT NULL,
    semester_id bigint NOT NULL,
    credits_registered integer NOT NULL,
    credits_passed integer NOT NULL,
    semester_gpa numeric(4,2) NOT NULL,
    letter_gpa character varying(2),
    conduct_score integer NOT NULL,
    activity_score numeric(4,2) NOT NULL,
    letter_activity_score character varying(2),
    group_contribution numeric(4,2) NOT NULL,
    letter_group_contribution character varying(2),
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT student_semester_summaries_check CHECK ((credits_registered >= credits_passed))
);


--
-- Name: student_semester_summaries_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.student_semester_summaries ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.student_semester_summaries_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: student_subject_results; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.student_subject_results (
    id bigint NOT NULL,
    student_id bigint NOT NULL,
    subject_id bigint NOT NULL,
    semester_id bigint NOT NULL,
    credits integer NOT NULL,
    attendance_score numeric(4,2),
    midterm_score numeric(4,2),
    final_score numeric(4,2),
    score_10 numeric(4,2),
    score_4 numeric(3,2),
    letter_grade character varying(2),
    is_pass boolean DEFAULT false,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: student_subject_results_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.student_subject_results ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.student_subject_results_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: students; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.students (
    id bigint NOT NULL,
    oauth_user_id bigint,
    student_class_id bigint NOT NULL,
    full_name character varying(255) NOT NULL,
    student_code character varying(20) NOT NULL,
    gender character varying(20) DEFAULT 'NAM'::character varying NOT NULL,
    date_of_birth date NOT NULL,
    status character varying(20) DEFAULT 'ACTIVE'::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT students_gender_check CHECK (((gender)::text = ANY ((ARRAY['NAM'::character varying, 'NU'::character varying])::text[]))),
    CONSTRAINT students_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'SUSPENDED'::character varying, 'GRADUATED'::character varying, 'DROPPED_OUT'::character varying, 'DELETED'::character varying])::text[])))
);


--
-- Name: students_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.students ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.students_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: study_program_subjects; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.study_program_subjects (
    id bigint NOT NULL,
    study_program_id bigint NOT NULL,
    subject_id bigint NOT NULL,
    semester_id bigint NOT NULL,
    elective_group character varying(50),
    is_required boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: study_program_subjects_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.study_program_subjects ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.study_program_subjects_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: study_programs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.study_programs (
    id bigint NOT NULL,
    major_id bigint NOT NULL,
    study_program_code character varying(20) NOT NULL,
    study_program_name character varying(255) NOT NULL,
    total_credits integer,
    start_year integer NOT NULL,
    training_type character varying(20) DEFAULT 'CHINH_QUY'::character varying NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT study_programs_training_type_check CHECK (((training_type)::text = ANY ((ARRAY['CHINH_QUY'::character varying, 'LIEN_THONG'::character varying])::text[])))
);


--
-- Name: study_programs_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.study_programs ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.study_programs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: subject_enrollment_conditions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.subject_enrollment_conditions (
    id bigint NOT NULL,
    subject_id bigint NOT NULL,
    condition_type character varying(50) NOT NULL,
    condition_value numeric(6,2) NOT NULL,
    condition_operator character varying(5) DEFAULT '>='::character varying,
    description text,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT subject_enrollment_conditions_condition_operator_check CHECK (((condition_operator)::text = ANY ((ARRAY['>='::character varying, '>'::character varying, '='::character varying, '<='::character varying])::text[]))),
    CONSTRAINT subject_enrollment_conditions_condition_type_check CHECK (((condition_type)::text = ANY ((ARRAY['GPA'::character varying, 'TOTAL_CREDITS'::character varying])::text[])))
);


--
-- Name: subject_enrollment_conditions_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.subject_enrollment_conditions ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.subject_enrollment_conditions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: subject_prerequisite_group_items; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.subject_prerequisite_group_items (
    group_id bigint NOT NULL,
    prerequisite_subject_id bigint NOT NULL,
    created_at timestamp without time zone DEFAULT now()
);


--
-- Name: subject_prerequisite_groups; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.subject_prerequisite_groups (
    id bigint NOT NULL,
    subject_id bigint NOT NULL,
    min_subjects_required integer DEFAULT 1,
    description text,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: subject_prerequisite_groups_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.subject_prerequisite_groups ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.subject_prerequisite_groups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: subjects; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.subjects (
    id bigint NOT NULL,
    faculty_id bigint,
    department_id bigint,
    subject_code character varying(20) NOT NULL,
    subject_name character varying(255) NOT NULL,
    credits integer NOT NULL,
    coefficient numeric(3,2) DEFAULT 1.0,
    lecture_hours integer,
    practice_hours integer,
    is_active boolean DEFAULT true,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: subjects_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.subjects ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.subjects_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tuition_fee_configs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tuition_fee_configs (
    id bigint NOT NULL,
    academic_year character varying(20) NOT NULL,
    cohort integer NOT NULL,
    base_price_per_credit numeric(10,2) NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: tuition_fee_configs_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.tuition_fee_configs ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.tuition_fee_configs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tuition_invoice_items; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tuition_invoice_items (
    id bigint NOT NULL,
    invoice_id bigint NOT NULL,
    course_class_id bigint NOT NULL,
    price_per_credit numeric(10,2) NOT NULL,
    credits integer NOT NULL,
    coefficient numeric(3,2) DEFAULT 1.0,
    amount numeric(10,2) NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: tuition_invoice_items_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.tuition_invoice_items ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.tuition_invoice_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tuition_invoices; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tuition_invoices (
    id bigint NOT NULL,
    student_id bigint NOT NULL,
    semester_id bigint NOT NULL,
    due_date date NOT NULL,
    total_amount numeric(10,2) NOT NULL,
    final_amount numeric(10,2) NOT NULL,
    status character varying(20) DEFAULT 'UNPAID'::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT tuition_invoices_status_check CHECK (((status)::text = ANY ((ARRAY['UNPAID'::character varying, 'PENDING'::character varying, 'PAID'::character varying, 'OVERDUE'::character varying, 'CANCELLED'::character varying])::text[])))
);


--
-- Name: tuition_invoices_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.tuition_invoices ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.tuition_invoices_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tuition_transactions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tuition_transactions (
    id bigint NOT NULL,
    student_id bigint NOT NULL,
    invoice_id bigint,
    amount numeric(10,2) NOT NULL,
    type character varying(20) NOT NULL,
    reference_id bigint,
    reference_type character varying(20),
    description text,
    created_at timestamp without time zone DEFAULT now(),
    CONSTRAINT tuition_transactions_type_check CHECK (((type)::text = ANY ((ARRAY['TUITION'::character varying, 'PAYMENT'::character varying, 'REFUND'::character varying, 'ADJUSTMENT'::character varying])::text[])))
);


--
-- Name: tuition_transactions_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.tuition_transactions ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.tuition_transactions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: user_devices; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_devices (
    id bigint NOT NULL,
    oauth_user_id bigint NOT NULL,
    device_id character varying(255) NOT NULL,
    fcm_token text NOT NULL,
    platform character varying(20),
    last_used_at timestamp without time zone,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: user_devices_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.user_devices ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.user_devices_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: user_roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_roles (
    oauth_user_id bigint NOT NULL,
    role_id bigint NOT NULL,
    assigned_at timestamp without time zone DEFAULT now()
);


--
-- Data for Name: academic_advisors; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.academic_advisors (id, lecturer_id, student_class_id, created_at, updated_at) FROM stdin;
1	1	1	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	1	2	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	2	3	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	2	4	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	3	5	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	3	6	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	4	7	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	4	8	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	5	9	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	5	10	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
11	6	11	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
12	6	12	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
13	7	13	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: academic_infos; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.academic_infos (id, student_major_id, cohort, "position", created_at, updated_at) FROM stdin;
1	1	K35	Lớp trưởng	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	2	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	3	K35	Lớp phó	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	4	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	5	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	6	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	7	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	8	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	9	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	10	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
11	11	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
12	12	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
13	13	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
14	14	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
15	15	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
16	16	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
17	17	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
18	18	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
19	19	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
20	20	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
21	21	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
22	22	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
23	23	K35	Sinh viên	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: application_attachments; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.application_attachments (id, application_id, file_key, original_filename, file_size, created_at) FROM stdin;
1	1	file1.pdf	don_xin_nghi.pdf	123456	2026-05-27 17:43:12.004061
2	2	file2.pdf	phuc_khao.pdf	223456	2026-05-27 17:43:12.004061
3	3	file3.pdf	giay_xac_nhan.pdf	323456	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: application_types; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.application_types (id, code, name, is_active, created_at, updated_at) FROM stdin;
1	LEAVE	Xin nghi hoc	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	GRADE_REVIEW	Phuc khao diem	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	CERT	Xin giay xac nhan	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: attendances; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.attendances (id, course_class_id, session_id, student_id, check_in_time, created_at) FROM stdin;
\.


--
-- Data for Name: class_schedules; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.class_schedules (id, course_class_id, day_of_week, start_period, end_period, start_time, end_time, room, created_at, updated_at) FROM stdin;
1	2	3	4	6	10:10:00	12:40:00	A102	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	2	5	4	6	10:10:00	12:40:00	A102	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	3	2	7	9	13:00:00	15:30:00	B201	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	3	6	7	9	13:00:00	15:30:00	B201	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	4	3	1	3	07:30:00	10:00:00	C101	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	4	5	1	3	07:30:00	10:00:00	C101	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	5	2	4	6	10:10:00	12:40:00	A201	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	5	4	4	6	10:10:00	12:40:00	A201	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	6	3	7	9	13:00:00	15:30:00	B101	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	6	6	7	9	13:00:00	15:30:00	B101	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
11	7	2	1	3	07:30:00	10:00:00	D101	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
12	7	5	1	3	07:30:00	10:00:00	D101	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
13	8	3	4	6	10:10:00	12:40:00	C202	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
14	8	6	4	6	10:10:00	12:40:00	C202	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
15	9	2	7	9	13:00:00	15:30:00	A301	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
16	9	4	7	9	13:00:00	15:30:00	A301	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
17	10	3	1	3	07:30:00	10:00:00	B202	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
18	10	5	1	3	07:30:00	10:00:00	B202	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: course_classes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.course_classes (id, lecturer_id, subject_id, semester_id, class_code, class_name, capacity, enrolled_count, is_active, version, created_at, updated_at) FROM stdin;
1	1	32	5	252CF21301	Cấu trúc dữ liệu và giải thuật 01	40	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	1	1	5	252AD21501	Kỹ năng sống 01	40	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	2	2	5	252GF10101	Tiếng Pháp 1 - 01	35	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	2	3	6	252GF10201	Tiếng Pháp 2 - 01	35	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	3	4	5	252PG10201	GDTC cổ truyền 01	50	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	1	5	5	252CS10001	Tin đại cương 01	45	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	4	6	5	252CS10201	Tin học văn phòng 01	45	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	5	7	5	252EC10201	Nhập môn kinh tế học 01	50	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	6	8	5	252GE11101	Tiếng Anh sơ cấp 1 - 01	40	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	1	9	5	252MA10101	Logic và suy luận toán học 01	45	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
11	7	10	5	252ML11301	Triết học Mác - Lênin 01	50	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
12	7	11	1	252NA15101	Khoa học môi trường 01	40	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
13	4	12	1	252SH13101	Pháp luật đại cương 01	45	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
14	4	13	1	252VL10101	Tiếng Việt thực hành 01	45	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
15	1	14	1	252CS12101	Lập trình cơ sở 1 - 01	45	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
16	1	15	1	252CS21201	Kiến trúc máy tính 01	40	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
17	1	16	1	252CS21301	Cấu trúc dữ liệu và giải thuật 01	40	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
18	6	17	2	252GE11201	Tiếng Anh sơ cấp 2 - 01	40	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
19	1	18	2	252MA12001	Đại số tuyến tính 01	45	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
20	7	19	2	252ML11401	Kinh tế chính trị M-L 01	50	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
21	6	20	2	252GE12101	Tiếng Anh sơ trung cấp 1 - 01	40	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
22	1	21	2	252MA11001	Giải tích 1 - 01	45	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
23	1	22	2	252MA11101	Giải tích 2 - 01	45	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
24	1	23	2	252MI20101	Toán rời rạc 01	45	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
25	7	24	2	252ML11501	CNXH khoa học 01	50	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
26	1	25	2	252CS11101	Kỹ thuật số 01	40	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
27	1	26	4	252CF21301	CTDL & GT nâng cao 01	40	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
28	6	33	4	252GE22201	Tiếng Anh sơ trung cấp 2 - 01	40	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
29	1	28	4	252IS22201	Cơ sở dữ liệu 01	45	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
30	1	29	4	252MA23901	Xác suất thống kê 01	45	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
31	7	30	4	252ML20201	Tư tưởng HCM 01	50	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
32	1	31	4	252CS31501	Hệ điều hành 01	40	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
33	1	32	5	252IS32201	Hệ QTCSDL 01	40	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
34	1	33	5	252IS31401	Hệ thống thông tin 01	40	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
35	1	34	5	252CS12201	Lập trình hướng đối tượng 01	45	0	t	0	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: departments; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.departments (id, faculty_id, department_code, department_name, is_active, created_at, updated_at) FROM stdin;
1	1	TT	Toán Tin	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	1	AI	Trí tuệ nhân tạo	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	2	KT	Kế toán	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	2	KDMK	Quản trị Kinh doanh & Marketing	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	4	HQ	Ngôn ngữ Hàn Quốc	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	4	TQ	Ngôn ngữ Trung Quốc	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	2	LGC	Logistics và Quản lý Chuỗi cung ứng	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	4	TA	Ngôn ngữ Anh	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	3	GDT	Giáo dục Thể chất	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	2	LE	Luật Kinh tế	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
11	4	GF	Ngôn ngữ Pháp	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: emergency_contacts; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.emergency_contacts (id, student_id, full_name, phone_number, address, relationship, created_at, updated_at) FROM stdin;
1	1	Nguyễn Văn Hùng	0911111111	Hà Nội	Cha	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	2	Trần Thị Mai	0922222222	Hà Nội	Mẹ	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	3	Phạm Văn Dũng	0933333333	Hà Nội	Cha	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	4	Lê Thị Hoa	0944444444	Hà Nội	Mẹ	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	5	Hoàng Văn Sơn	0955555555	Đà Nẵng	Cha	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	6	Đỗ Thị Lan	0966666666	Huế	Mẹ	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	7	Nguyễn Văn Bình	0977777777	TP.HCM	Cha	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	8	Trần Văn Phúc	0980000001	Hải Phòng	Cha	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	9	Lê Thị Hạnh	0980000002	Nam Định	Mẹ	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	10	Phạm Văn Khánh	0980000003	Hà Nội	Cha	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
11	11	Hoàng Thị Thu	0980000004	Đà Nẵng	Mẹ	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
12	12	Đỗ Văn Long	0980000005	Huế	Cha	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
13	13	Nguyễn Thị Tuyết	0980000006	Hà Nội	Mẹ	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
14	14	Trần Văn Nam	0980000007	TP.HCM	Cha	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
15	15	Lê Thị Ngọc	0980000008	Hải Phòng	Mẹ	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
16	16	Phạm Văn Hòa	0980000009	Nam Định	Cha	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
17	17	Hoàng Thị Dung	0980000010	Hà Nội	Mẹ	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
18	18	Đỗ Văn Tài	0980000011	Đà Nẵng	Cha	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
19	19	Nguyễn Thị Hồng	0980000012	Huế	Mẹ	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
20	20	Trần Văn Quang	0980000013	TP.HCM	Cha	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
21	21	Lê Thị Thanh	0980000014	Hà Nội	Mẹ	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
22	22	Phạm Văn Cường	0980000015	Hải Phòng	Cha	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: enrollment_periods; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.enrollment_periods (id, semester_id, start_time, end_time, max_credits, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: exam_schedules; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.exam_schedules (id, subject_id, semester_id, exam_date, start_time, end_time, exam_room, exam_location, exam_format, exam_type, note, created_at, updated_at) FROM stdin;
1	6	1	2026-01-17	08:00:00	10:00:00	C101	Cơ sở 1	OFFLINE	Final	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	7	1	2026-01-18	13:00:00	15:00:00	C102	Cơ sở 1	OFFLINE	Final	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	8	1	2026-01-20	09:00:00	11:00:00	D201	Cơ sở 1	OFFLINE	Midterm	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	9	1	2026-01-22	08:00:00	10:00:00	A103	Cơ sở 1	OFFLINE	Final	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	10	1	2026-01-24	13:00:00	15:00:00	B203	Cơ sở 1	OFFLINE	Final	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	11	2	2026-06-14	08:00:00	10:00:00	A301	Cơ sở 1	OFFLINE	Final	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	12	2	2026-06-15	13:00:00	15:00:00	A302	Cơ sở 1	OFFLINE	Final	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	13	2	2026-06-16	09:00:00	11:00:00	B301	Cơ sở 1	OFFLINE	Midterm	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	14	2	2026-06-18	08:00:00	10:00:00	C301	Cơ sở 1	OFFLINE	Final	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	15	2	2026-06-20	13:00:00	15:00:00	C302	Cơ sở 1	OFFLINE	Final	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
11	16	3	2026-08-05	08:00:00	10:00:00	D101	Cơ sở 1	OFFLINE	Final	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
12	17	3	2026-08-06	13:00:00	15:00:00	D102	Cơ sở 1	OFFLINE	Final	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
13	18	3	2026-08-07	09:00:00	11:00:00	A104	Cơ sở 1	OFFLINE	Midterm	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
14	19	3	2026-08-09	08:00:00	10:00:00	B204	Cơ sở 1	OFFLINE	Final	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
15	20	3	2026-08-10	13:00:00	15:00:00	C103	Cơ sở 1	OFFLINE	Final	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: faculties; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.faculties (id, faculty_code, faculty_name, is_active, created_at, updated_at) FROM stdin;
1	CNTT	Công nghệ thông tin	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	KTQL	Kinh tế - Quản lý	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	KHSK	Khoa học sức khỏe	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	KNN	Ngoại ngữ	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	XHNV	Khoa học xã hội và nhân văn	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	KDL	Du lịch	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	KTT	Truyền thông đa phương tiện	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	KAN	Âm nhạc ứng dụng	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	PDT	Phòng đào tạo	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: feedback; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.feedback (id, oauth_user_id, title, category_id, content, app_version, device_info, status, created_at) FROM stdin;
\.


--
-- Data for Name: feedback_attachments; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.feedback_attachments (id, feedback_id, file_key, original_filename, file_size, created_at) FROM stdin;
\.


--
-- Data for Name: feedback_category; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.feedback_category (id, name, description, is_active, created_at, updated_at) FROM stdin;
1	BUG	Phản hồi liên quan đến lỗi hệ thống hoặc chức năng ứng dụng	t	2026-01-01 08:00:00	2026-01-01 08:00:00
2	UI_UX	Phản hồi liên quan đến giao diện và trải nghiệm người dùng	t	2026-01-01 08:10:00	2026-01-01 08:10:00
3	ACCOUNT	Các vấn đề liên quan đến tài khoản người dùng	t	2026-01-01 08:15:00	2026-01-01 08:15:00
\.


--
-- Data for Name: grade_scale; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.grade_scale (id, min_score, max_score, letter_grade, created_at, updated_at) FROM stdin;
1	9.50	10.00	A+	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	9.00	9.50	A	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	8.50	9.00	A-	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	8.00	8.50	B+	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	7.50	8.00	B	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	7.00	7.50	B-	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	6.50	7.00	C	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	6.00	7.50	D	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	5.00	6.00	E	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	0.00	5.00	F	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: health_insurances; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.health_insurances (id, student_id, insurance_number, provider, valid_from, valid_to, registered_hospital, status, created_at, updated_at) FROM stdin;
1	1	BHYT008	Bao Minh	2024-02-01	2026-02-01	BV Hải Phòng	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	2	BHYT009	Bao Viet	2025-04-01	2027-04-01	BV Nam Định	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	3	BHYT010	Bao Minh	2024-07-01	2026-07-01	BV Bạch Mai	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	4	BHYT011	Bao Viet	2025-08-01	2027-08-01	BV Đà Nẵng	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	5	BHYT012	Bao Minh	2024-10-01	2026-10-01	BV Huế	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	6	BHYT013	Bao Viet	2025-11-01	2027-11-01	BV 108	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	7	BHYT014	Bao Minh	2024-03-01	2026-03-01	BV Chợ Rẫy	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	8	BHYT015	Bao Viet	2025-06-01	2027-06-01	BV Việt Đức	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	9	BHYT016	Bao Minh	2024-09-15	2026-09-15	BV Thanh Nhàn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	10	BHYT017	Bao Viet	2025-12-01	2027-12-01	BV Bạch Mai	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
11	11	BHYT018	Bao Minh	2024-05-01	2026-05-01	BV Đà Nẵng	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
12	12	BHYT019	Bao Viet	2025-07-01	2027-07-01	BV Huế	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
13	13	BHYT020	Bao Minh	2024-08-01	2026-08-01	BV Chợ Rẫy	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
14	14	BHYT021	Bao Viet	2025-09-01	2027-09-01	BV 108	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
15	15	BHYT022	Bao Minh	2024-11-01	2026-11-01	BV Hải Phòng	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
16	16	BHYT023	Bao Viet	2025-01-15	2027-01-15	BV Nam Định	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
17	17	BHYT024	Bao Minh	2024-04-01	2026-04-01	BV Việt Đức	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
18	18	BHYT025	Bao Viet	2025-02-01	2027-02-01	BV Đà Nẵng	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
19	19	BHYT026	Bao Minh	2024-06-15	2026-06-15	BV Huế	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
20	20	BHYT027	Bao Viet	2025-03-15	2027-03-15	BV Chợ Rẫy	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
21	21	BHYT028	Bao Minh	2024-12-15	2026-12-15	BV Bạch Mai	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
22	22	BHYT029	Bao Viet	2025-10-01	2027-10-01	BV 108	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: identity_cards; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.identity_cards (id, student_id, card_number, card_type, issued_date, issued_place, created_at, updated_at) FROM stdin;
1	1	100000000008	CCCD	2021-08-08	Hải Phòng	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	2	100000000009	CCCD	2022-09-09	Nam Định	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	3	100000000010	CCCD	2021-10-10	Hà Nội	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	4	100000000011	CCCD	2022-11-11	Đà Nẵng	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	5	100000000012	CCCD	2023-12-12	Huế	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	6	100000000013	CCCD	2021-01-13	Hà Nội	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	7	100000000014	CCCD	2022-02-14	TP.HCM	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	8	100000000015	CCCD	2023-03-15	Hải Phòng	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	9	100000000016	CCCD	2021-04-16	Nam Định	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	10	100000000017	CCCD	2022-05-17	Hà Nội	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
11	11	100000000018	CCCD	2023-06-18	Đà Nẵng	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
12	12	100000000019	CCCD	2021-07-19	Huế	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
13	13	100000000020	CCCD	2022-08-20	TP.HCM	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
14	14	100000000021	CCCD	2023-09-21	Hà Nội	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
15	15	100000000022	CCCD	2021-10-22	Hải Phòng	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
16	16	100000000023	CCCD	2022-11-23	Nam Định	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
17	17	100000000024	CCCD	2023-12-24	Hà Nội	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
18	18	100000000025	CCCD	2021-01-25	Đà Nẵng	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
19	19	100000000026	CCCD	2022-02-26	Huế	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
20	20	100000000027	CCCD	2023-03-27	TP.HCM	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
21	21	100000000028	CCCD	2021-04-28	Hà Nội	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
22	22	100000000029	CCCD	2022-05-29	Hải Phòng	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: lecturers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.lecturers (id, oauth_user_id, department_id, full_name, lecturer_code, phone_number, email, status, created_at, updated_at) FROM stdin;
1	4	1	Nguyễn Đức Minh	CTI064	0969642001	minhnd@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	5	2	Hoàng Thu Hà	KDM001	0123456789	ha.hoang@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	6	3	Đỗ Minh Tuấn	KDM002	0123456789	tuan.do@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	7	1	Nguyễn Thị Lan Anh	CTI065	0901000001	lananh.nguyen@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	8	1	Trần Quốc Bảo	CTI066	0901000002	bao.tran@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	9	1	Lê Hoàng Nam	CTI067	0901000003	nam.le@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	10	1	Phạm Gia Khánh	CTI068	0901000004	khanh.pham@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: majors; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.majors (id, major_code, major_name, faculty_id, is_active, created_at, updated_at) FROM stdin;
1	TA	Trí tuệ nhân tạo	1	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	TI	Khoa học máy tính	1	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	TE	Mạng máy tính và truyền thông dữ liệu	1	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	TT	Hệ thống thông tin	1	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	IT	Công nghệ thông tin	1	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	EC	Thương mại điện tử	2	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	IE	Kinh tế quốc tế	2	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	EL	Luật kinh tế	2	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	MK	Marketing	2	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	AC	Kế toán	2	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
11	FN	Tài chính - Ngân hàng	2	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
12	LG	Logistics và Quản lí chuỗi cung ứng	2	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
13	BA	Quản trị kinh doanh	2	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
14	NU	Điều dưỡng	3	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
15	EN	Ngôn ngữ Anh	4	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
16	KR	Ngôn ngữ Hàn Quốc	4	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
17	CN	Ngôn ngữ Trung Quốc	4	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
18	JP	Ngôn ngữ Nhật	4	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
19	VN	Việt Nam học	5	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
20	TM	Quản trị và Du lịch - Lữ hành	6	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
21	HM	Quản trị khách sạn	6	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
22	MM	Truyền thông đa phương tiện	7	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
23	GD	Thiết kế đồ họa	7	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
24	VO	Thanh nhạc	8	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: news; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.news (id, title, excerpt, image_url, image_key, source, publish_date, news_url, created_at, updated_at) FROM stdin;
1	Sinh viên Kế toán định hướng nghề nghiệp trong thời đại số tại workshop “Accounting Next Gen: Kế toán trong thời đại chuyển đổi số”	Chiều ngày 19.12.2025, workshop “Accounting next gen: Kế toán trong thời đại chuyển đổi số” do Trường Đại học Thăng Long phối hợp cùng ACCA (Hiệp hội Kế toán Công chứng Anh quốc) và Công ty Cổ phần Misa (doanh nghiệp công nghệ thông tin hàng đầu Việt Nam, cung cấp giải pháp chuyển đổi số trong lĩnh vực tài chính kế toán và quản trị doanh nghiệp) tổ chức đã diễn ra thành công rực rỡ, thu hút sự tham gia của đông đảo sinh viên từ năm 2 đến năm 4 ngành Kế toán.	https://res.cloudinary.com/dm5ev1isi/image/upload/v1775458101/uploads/1775458100898_605685389_1322159439950890_5475185904169852220_n.jpg.png	uploads/1775458100898_605685389_1322159439950890_5475185904169852220_n.jpg	Khoa kinh tế	2025-12-20	https://thanglong.edu.vn/sinh-vien-ke-toan-dinh-huong-nghe-nghiep-trong-thoi-dai-so-tai-workshop-accounting-next-gen-ke-toan-trong-thoi-dai-chuyen-doi-so-21619.html	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	Sinh viên, học viên khoa Khoa học sức khỏe mở mang kiến thức chuyên ngành với tọa đàm “Đổi mới sáng tạo trong nghiên cứu khoa học và đào tạo nhân lực y tế”	Ngày 30.12.2025, tại Trường Đại học Thăng Long, tọa đàm với chủ đề “Đổi mới sáng tạo trong nghiên cứu khoa học và đào tạo nhân lực Y tế” đã diễn ra trong không khí sôi nổi, thu hút đông đảo giảng viên, nhà nghiên cứu, học viên, sinh viên khoa Khoa học sức khỏe tham dự.	https://res.cloudinary.com/dm5ev1isi/image/upload/v1775458052/uploads/1775458050825_608115782_1322766886556812_5292800162340264472_n.jpg.jpg	uploads/1775458050825_608115782_1322766886556812_5292800162340264472_n.jpg	Khoa điều dưỡngTruong	2025-12-31	https://thanglong.edu.vn/sinh-vien-hoc-vien-khoa-khoa-hoc-suc-khoe-mo-mang-kien-thuc-chuyen-nganh-voi-toa-dam-doi-moi-sang-tao-trong-nghien-cuu-khoa-hoc-va-dao-tao-nhan-luc-y-te-21633.html	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	Sinh viên Khoa Công nghệ thông tin tham gia trải nghiệm thực tế tại Công ty Cổ phần VTI	Ngày 08.12, sinh viên Khoa Công nghệ Thông tin, Trường Đại học Thăng Long vừa có chuyến tham quan, trải nghiệm thực tế đầy ý nghĩa tại Công ty Cổ phần VTI - tập đoàn công nghệ hàng đầu tại Việt Nam.	https://res.cloudinary.com/dm5ev1isi/image/upload/v1775458467/uploads/1775458464887_596791541_1306010964899071_2118744865079236732_n.jpg.png	uploads/1775458464887_596791541_1306010964899071_2118744865079236732_n.jpg	Khoa công nghệ thông tin	2025-12-09	https://thanglong.edu.vn/sinh-vien-khoa-cong-nghe-thong-tin-tham-gia-trai-nghiem-thuc-te-tai-cong-ty-co-phan-vti-21587.html	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	THÔNG BÁO (GẤP) Gia hạn thời gian nhận hồ sơ xét tốt nghiệp hệ chính quy đợt 1 năm 2026		\N	\N	Phòng Đào tạo	2025-01-31	https://thanglong.edu.vn/thong-bao-gap-gia-han-thoi-gian-nhan-ho-so-xet-tot-nghiep-he-chinh-quy-dot-1-nam-2026-21653.html	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	Bộ môn Luật kinh tế tổ chức thành công hội thảo khoa học “Các vấn đề pháp lý mới trong kỷ nguyên số: Vai trò của các cơ sở đào tạo, nghiên cứu luật”	Vào ngày 31.12.2025, Hội thảo Khoa học cấp trường “Các vấn đề pháp lý mới trong kỷ nguyên số: Vai trò của các cơ sở đào tạo, nghiên cứu luật” được đồng tổ chức bởi Bộ môn Luật Kinh tế, Trường Đại học Thăng Long và Viện Chính sách công và Pháp luật (IPL) đã diễn ra thành công. Hội thảo thu hút sự tham gia của đông đảo các nhà khoa học, giảng viên, chuyên viên pháp lý và sinh viên Bộ môn Luật kinh tế.	https://res.cloudinary.com/dm5ev1isi/image/upload/v1775457941/uploads/1775457939544_607712071_1325747526258748_1488510943557057778_n.jpg.jpg	uploads/1775457939544_607712071_1325747526258748_1488510943557057778_n.jpg	Bộ môn Luật kinh tế	2026-01-01	https://thanglong.edu.vn/bo-mon-luat-kinh-te-to-chuc-thanh-cong-hoi-thao-khoa-hoc-cac-van-de-phap-ly-moi-trong-ky-nguyen-so-vai-tro-cua-cac-co-so-dao-tao-nghien-cuu-luat-21632.html	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: notification_read; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.notification_read (id, notification_id, oauth_user_id, read_at) FROM stdin;
1	1	1	2026-05-27 17:43:12.004061
2	2	1	2026-05-27 17:43:12.004061
3	3	1	2026-05-27 17:43:12.004061
4	4	1	2026-05-27 17:43:12.004061
5	1	2	2026-05-27 17:43:12.004061
6	2	2	2026-05-27 17:43:12.004061
7	5	3	2026-05-27 17:43:12.004061
8	6	3	2026-05-27 17:43:12.004061
9	7	3	2026-05-27 17:43:12.004061
10	10	4	2026-05-27 17:43:12.004061
11	11	4	2026-05-27 17:43:12.004061
12	15	5	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: notification_targets; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.notification_targets (id, notification_id, target_id) FROM stdin;
1	6	2
2	7	2
3	8	3
4	9	4
5	10	5
6	11	10
7	12	10
8	13	10
9	14	1
10	15	1
11	16	1
12	17	1
13	18	1
\.


--
-- Data for Name: notification_templates; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.notification_templates (id, code, name, content, created_at, updated_at) FROM stdin;
1	GENERAL	Thong bao chung	{{content}}	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	EXAM	Thong bao lich thi	Lich thi: {{content}}	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	FEE	Nhac nop hoc phi	Sinh vien can nop hoc phi truoc {{deadline}}	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	SCHOLARSHIP	Thong bao hoc bong	{{content}}	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	ACADEMIC_WARNING	Canh bao hoc vu	{{content}}	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	DEFENSE	Thong bao bao ve do an	{{content}}	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.notifications (id, title, content, created_by, target_type, reference_type, is_important, deadline, created_at, updated_at) FROM stdin;
1	Thong bao he thong	He thong se bao tri vao 23:00 toi nay	SYSTEM	GLOBAL	\N	f	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	Cap nhat cong thong tin	Da cap nhat giao dien moi	SYSTEM	GLOBAL	\N	f	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	Thong bao nghi le	Sinh vien nghi le quoc khanh	SYSTEM	GLOBAL	\N	f	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	Lich thi HK1	Lich thi da duoc cap nhat tren portal	SYSTEM	GLOBAL	EXAM_SCHEDULE	t	2024-01-05	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	Ket qua hoc bong	Danh sach hoc bong HK1 da duoc cong bo	SYSTEM	GLOBAL	\N	f	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	Canh bao hoc vu	Ket qua hoc tap duoi muc yeu cau	SYSTEM	STUDENT_CLASS	\N	t	2024-03-01	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	Canh bao hoc vu lan 2	Sinh vien can gap co van hoc tap	SYSTEM	STUDENT_CLASS	\N	t	2024-03-10	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	Thong bao rieng	Sinh vien duoc chon tham gia workshop	SYSTEM	STUDENT_CLASS	\N	f	2024-03-10	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	Thong bao rieng	Sinh vien duoc cap tai khoan lab	SYSTEM	STUDENT_CLASS	\N	f	2024-03-10	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	Thong bao rieng	Sinh vien cap nhat thong tin ca nhan	SYSTEM	STUDENT_CLASS	\N	f	2024-03-10	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
11	Thong bao mon hoc	Lop lap trinh web thay doi phong hoc	LECTURER	COURSE_CLASS	\N	f	2024-03-10	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
12	Thong bao mon hoc	Buoi hoc toi se hoc online	LECTURER	COURSE_CLASS	\N	f	2024-03-10	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
13	Thong bao mon hoc	Deadline project duoc gia han	LECTURER	COURSE_CLASS	\N	f	2024-04-10	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
14	Thong bao khoa CNTT	Sinh vien tham gia hoi thao AI	FACULTY	FACULTY	\N	f	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
15	Thong bao khoa CNTT	Cuoc thi lap trinh sap dien ra	FACULTY	FACULTY	\N	f	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
16	Thong bao khoa CNTT	Mo dang ky CLB AI	FACULTY	FACULTY	\N	f	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
17	Thong bao khoa CNTT	Workshop Cloud Computing	FACULTY	FACULTY	\N	f	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
18	Thong bao khoa CNTT	Sinh vien dang ky thuc tap he	FACULTY	FACULTY	\N	f	\N	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: oauth_users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.oauth_users (id, user_uuid, display_name, email, status, created_at, updated_at) FROM stdin;
1	1deb00a9-835c-4ab7-a50f-57c12a56c7bd	Nguyễn Văn An	nhokthanh3211@gmail.com	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	a65d03d4-6a2a-426f-963d-8dca24399b83	Trần Thị Bích	bich.tran@university.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	4a43cbd4-f3c6-4f5e-8db3-5b3aa1ef7369	Phùng Thanh Độ	a45044@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	uuid-4	Phạm Quang Huy	a45123@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	uuid-5	Hoàng Thu Hà	a45124@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	uuid-6	Đỗ Minh Tuấn	a45125@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	uuid-7	Nguyễn Thị Lan Anh	a45126@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	uuid-8	Trần Quốc Bảo	a45127@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	uuid-9	Lê Hoàng Nam	a45128@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	uuid-10	Phạm Gia Khánh	a45129@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
11	uuid-11	Nguyễn Hoàng Long	a45130@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
12	uuid-12	Trần Minh Tuấn	a45131@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
13	uuid-13	Lê Thị Thu Trang	a45132@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
14	uuid-14	Phạm Đức Anh	a45133@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
15	uuid-15	Hoàng Ngọc Linh	a45134@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
16	uuid-16	Đỗ Quang Trung	a45135@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
17	uuid-17	Nguyễn Hải Đăng	a45136@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
18	uuid-18	Trần Thu Phương	a45137@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
19	uuid-19	Lê Quốc Khánh	a45138@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
20	uuid-20	Phạm Thùy Dương	a45139@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
21	uuid-21	Hoàng Minh Khang	a45140@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
22	uuid-22	Đỗ Thị Ngọc Anh	a45141@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
23	uuid-23	Nguyễn Văn Hưng	a45142@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
24	uuid-24	Trần Đức Thành	a45143@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
25	uuid-25	Lê Thanh Tùng	a45144@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
26	uuid-26	Phạm Nhật Quang	a45145@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
27	uuid-27	Hoàng Thị Mai	a45146@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
28	uuid-28	Đỗ Văn Nam	a45147@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
29	uuid-29	Nguyễn Thị Hồng Nhung	a45148@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
30	uuid-30	Trần Gia Bảo	a45149@thanglong.edu.vn	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: payment; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.payment (id, invoice_id, amount, provider, transaction_code, provider_trans_id, status, created_at, updated_at) FROM stdin;
1	1	4500000.00	ZALOPAY	TXN_001_SUCCESS	\N	SUCCESS	2025-12-05 00:00:00	2025-12-05 00:00:00
2	2	4200000.00	ZALOPAY	TXN_002_FAIL	\N	FAILED	2025-12-07 00:00:00	2025-12-07 00:00:00
3	2	4200000.00	ZALOPAY	TXN_002_PENDING	\N	PENDING	2025-12-08 00:00:00	2025-12-08 00:00:00
4	3	4300000.00	VNPAY	TXN_003_PENDING	\N	PENDING	2025-12-09 00:00:00	2025-12-09 00:00:00
5	5	5200000.00	VNPAY	TXN_005_SUCCESS	\N	SUCCESS	2026-03-10 00:00:00	2026-03-10 00:00:00
6	6	4000000.00	ZALOPAY	TXN_006_SUCCESS	\N	SUCCESS	2025-12-10 00:00:00	2025-12-10 00:00:00
\.


--
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.roles (id, code, name, is_active, created_at, updated_at) FROM stdin;
1	ADMIN	Administrator	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	STUDENT	Student	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	LECTURER	Lecturer	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: semesters; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.semesters (id, semester_name, semester_code, academic_years, semester_number, start_date, end_date, is_active, created_at, updated_at) FROM stdin;
1	Học kỳ 1 2024-2025	HK1-2024-2025	2024-2025	1	2024-09-02	2024-12-15	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	Học kỳ 2 2024-2025	HK2-2024-2025	2024-2025	2	2025-01-02	2025-04-27	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	Học kỳ tăng cường 2024-2025	HKTC-2024-2025	2024-2025	3	2025-05-05	2025-08-24	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	Học kỳ 1 2025-2026	HK1-2025-2026	2025-2026	1	2025-09-08	2025-12-28	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	Học kỳ 2 2025-2026	HK2-2025-2026	2025-2026	2	2026-01-05	2026-04-26	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	Học kỳ tăng cường 2025-2026	HKTC-2025-2026	2025-2026	3	2026-05-04	2026-08-23	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: student_applications; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.student_applications (id, student_id, application_type_id, content, status, created_at, updated_at) FROM stdin;
1	1	1	Xin nghi vi om	PENDING	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	2	2	Phuc khao mon Java	APPROVED	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	3	3	Xin giay xac nhan sinh vien	PENDING	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	5	1	Xin nghi viec gia dinh	REJECTED	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: student_classes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.student_classes (id, class_code, major_id, start_year, created_at, updated_at) FROM stdin;
1	TA35CL01	1	2022	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	TA35CL02	1	2022	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	TI35CL01	2	2022	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	TI35CL02	2	2022	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	TI35CL03	2	2022	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	TE35CL01	3	2022	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	TE35CL02	3	2022	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	TT35CL01	4	2022	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	TT35CL02	4	2022	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	IT35CL01	5	2022	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
11	IT35CL02	5	2022	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
12	IT35CL03	5	2022	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
13	IT35CL04	5	2022	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
14	EC35CL01	6	2022	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
15	MK35CL01	9	2022	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
16	BA35CL01	13	2022	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
17	EN35CL01	15	2022	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
18	JP35CL01	18	2022	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: student_contacts; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.student_contacts (id, student_id, phone_number, address, email_personal, created_at, updated_at) FROM stdin;
1	1	0967000008	Hà Nội	i1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	2	0900000009	Hải Phòng	j1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	3	0900000010	Hà Nội	k1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	4	0900000011	Nam Định	l1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	5	0900000012	Thanh Hóa	m1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	6	0900000013	Huế	n1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	7	0900000014	Hồ Chí Minh	o1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	8	0900000015	Hà Nội	p1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	9	0900000016	Hải Phòng	q1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	10	0900000017	Hà Nội	r1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
11	11	0900000018	Nam Định	s1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
12	12	0900000019	Đà Nẵng	t1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
13	13	0900000020	Huế	u1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
14	14	0900000021	Hồ Chí Minh	v1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
15	15	0900000022	Hà Nội	w1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
16	16	0900000023	Hải Phòng	x1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
17	17	0900000024	Hà Nội	y1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
18	18	0900000025	Nam Định	z1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
19	19	0900000026	Đà Nẵng	aa1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
20	20	0900000027	Huế	bb1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
21	21	0900000028	Hồ Chí Minh	cc1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
22	22	0900000029	Hà Nội	dd1@gmail.com	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: student_course_class_logs; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.student_course_class_logs (id, student_id, course_class_id, action, from_status, to_status, created_at) FROM stdin;
1	1	1	ENROLL	PENDING	ENROLLED	2025-12-01 00:00:00
2	1	1	DROP	ENROLLED	DROPPED	2025-12-10 00:00:00
3	1	1	ENROLL	DROPPED	ENROLLED	2025-12-15 00:00:00
4	2	4	REJECTED	PENDING	REJECTED	2025-12-01 00:00:00
5	3	5	REJECTED	PENDING	REJECTED	2025-12-01 00:00:00
6	4	2	ENROLL	PENDING	ENROLLED	2025-12-02 00:00:00
7	5	4	ENROLL	PENDING	ENROLLED	2026-03-05 00:00:00
8	6	3	REJECTED	PENDING	REJECTED	2025-12-01 00:00:00
9	6	3	ENROLL	REJECTED	ENROLLED	2025-12-20 00:00:00
10	7	5	ENROLL	PENDING	ENROLLED	2025-12-03 00:00:00
11	7	5	DROP	ENROLLED	DROPPED	2025-12-12 00:00:00
\.


--
-- Data for Name: student_course_classes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.student_course_classes (id, student_id, course_class_id, status, subject_id, semester_id, is_retake, created_at, updated_at) FROM stdin;
1	1	1	ENROLLED	1	5	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	1	5	ENROLLED	5	5	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	1	14	ENROLLED	14	5	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	2	1	ENROLLED	1	1	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	2	6	ENROLLED	6	1	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	2	14	ENROLLED	14	1	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	3	5	ENROLLED	5	1	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	3	14	ENROLLED	14	1	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	3	16	ENROLLED	16	1	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	4	5	ENROLLED	5	2	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
11	4	9	ENROLLED	9	2	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
12	4	14	ENROLLED	14	2	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
13	5	5	ENROLLED	5	2	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
14	5	14	ENROLLED	14	2	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
15	5	15	ENROLLED	15	2	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
16	6	6	ENROLLED	6	2	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
17	6	7	ENROLLED	7	2	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
18	6	14	ENROLLED	14	3	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
19	7	5	ENROLLED	5	3	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
20	7	14	ENROLLED	14	3	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
21	7	18	ENROLLED	18	3	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
22	8	5	ENROLLED	5	4	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
23	8	9	ENROLLED	9	4	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
24	8	14	ENROLLED	14	4	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
25	9	5	ENROLLED	5	4	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
26	9	14	ENROLLED	14	4	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
27	9	21	ENROLLED	21	4	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
28	10	5	ENROLLED	5	4	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
29	10	9	ENROLLED	9	4	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
30	10	14	ENROLLED	14	4	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
31	11	5	ENROLLED	5	4	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
32	11	14	ENROLLED	14	4	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
33	11	23	ENROLLED	23	4	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
34	12	5	ENROLLED	5	5	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
35	12	14	ENROLLED	14	5	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
36	12	25	ENROLLED	25	5	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
37	13	7	ENROLLED	7	5	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
38	13	10	ENROLLED	10	5	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
39	13	12	ENROLLED	12	5	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
40	14	5	ENROLLED	5	5	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
41	14	14	ENROLLED	14	5	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
42	14	28	ENROLLED	28	5	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
43	15	5	ENROLLED	5	5	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
44	15	14	ENROLLED	14	5	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
45	15	31	ENROLLED	31	5	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
46	16	5	ENROLLED	5	5	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
47	16	14	ENROLLED	14	5	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
48	16	32	ENROLLED	32	5	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: student_exam_registrations; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.student_exam_registrations (id, student_id, exam_schedule_id, exam_attempt, attendance_status, created_at, updated_at) FROM stdin;
1	1	1	1	UPCOMING	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	1	2	1	UPCOMING	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	2	1	1	UPCOMING	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	2	2	1	UPCOMING	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	3	3	1	UPCOMING	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: student_majors; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.student_majors (id, student_id, major_id, study_program_id, is_primary, start_year, end_year, status) FROM stdin;
1	1	2	2	t	2022	2026	STUDYING
2	2	5	5	t	2022	2026	STUDYING
3	3	5	5	t	2022	2026	STUDYING
4	4	1	1	t	2022	2026	STUDYING
5	5	1	1	t	2022	2026	STUDYING
6	6	5	5	t	2022	2026	STUDYING
7	7	2	2	t	2022	2026	STUDYING
8	8	1	1	t	2022	2026	STUDYING
9	9	1	1	t	2022	2026	STUDYING
10	10	4	4	t	2022	2026	STUDYING
11	11	4	4	t	2022	2026	STUDYING
12	12	5	5	t	2022	2026	STUDYING
13	13	5	5	t	2022	2026	STUDYING
14	14	5	5	t	2022	2026	STUDYING
15	15	1	1	t	2022	2026	STUDYING
16	16	1	1	t	2022	2026	STUDYING
17	17	2	2	t	2022	2026	STUDYING
18	18	2	2	t	2022	2026	STUDYING
19	19	3	3	t	2022	2026	STUDYING
20	20	3	3	t	2022	2026	STUDYING
21	21	4	4	t	2022	2026	STUDYING
22	22	4	4	t	2022	2026	STUDYING
23	23	5	5	t	2022	2026	STUDYING
\.


--
-- Data for Name: student_semester_summaries; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.student_semester_summaries (id, student_id, study_program_id, semester_id, credits_registered, credits_passed, semester_gpa, letter_gpa, conduct_score, activity_score, letter_activity_score, group_contribution, letter_group_contribution, created_at, updated_at) FROM stdin;
1	1	2	1	6	6	3.20	B	80	8.00	B	7.50	B	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	2	5	1	3	3	3.50	A	85	8.50	A	8.00	A	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	3	5	1	3	3	3.00	B	75	7.00	B	7.00	B	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	4	1	1	3	3	2.80	C	70	6.50	C	6.00	C	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: student_subject_results; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.student_subject_results (id, student_id, subject_id, semester_id, credits, attendance_score, midterm_score, final_score, score_10, score_4, letter_grade, is_pass, created_at, updated_at) FROM stdin;
1	1	1	1	3	9.00	8.00	9.00	8.80	3.70	A	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	2	1	1	3	7.00	7.00	6.00	6.70	2.50	C	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	3	2	1	3	5.00	4.00	3.00	3.80	0.00	F	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	4	2	1	3	8.00	7.00	8.00	7.80	3.20	B	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	5	4	1	3	8.00	8.00	7.00	7.50	3.00	B	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	7	6	1	3	6.00	6.00	6.00	6.00	2.00	C	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: students; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.students (id, oauth_user_id, student_class_id, full_name, student_code, gender, date_of_birth, status, created_at, updated_at) FROM stdin;
1	1	1	Lê Việt Hoàng	A45033	NAM	2003-05-10	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	2	10	Nguyễn Ngọc Anh	A45035	NU	2004-08-20	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	3	10	Phùng Thanh Độ	A45044	NAM	2003-02-11	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	11	1	Nguyễn Hoàng Long	A45039	NAM	2004-02-15	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	12	1	Trần Minh Tuấn	A45040	NAM	2004-06-21	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	13	10	Lê Thị Thu Trang	A45041	NU	2004-09-12	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	14	2	Phạm Đức Anh	A45042	NAM	2003-11-05	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	15	1	Hoàng Ngọc Linh	A45043	NU	2004-07-30	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	16	1	Đỗ Quang Trung	A45045	NAM	2003-03-18	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	17	4	Nguyễn Hải Đăng	A45046	NAM	2004-01-25	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
11	18	4	Trần Thu Phương	A45047	NU	2004-05-14	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
12	19	10	Lê Quốc Khánh	A45048	NAM	2003-08-09	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
13	20	5	Phạm Thùy Dương	A45049	NU	2004-10-01	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
14	21	6	Hoàng Minh Khang	A45050	NAM	2003-12-22	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
15	22	6	Đỗ Thị Ngọc Anh	A45051	NU	2004-04-11	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
16	23	7	Nguyễn Văn Hưng	A45052	NAM	2003-09-17	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
17	24	7	Trần Đức Thành	A45053	NAM	2004-02-28	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
18	25	8	Lê Thanh Tùng	A45054	NAM	2003-06-06	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
19	26	8	Phạm Nhật Quang	A45055	NAM	2004-08-19	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
20	27	9	Hoàng Thị Mai	A45056	NU	2004-03-27	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
21	28	9	Đỗ Văn Nam	A45057	NAM	2003-07-13	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
22	29	10	Nguyễn Thị Hồng Nhung	A45058	NU	2004-11-02	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
23	30	10	Trần Gia Bảo	A45059	NAM	2003-10-10	ACTIVE	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: study_program_subjects; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.study_program_subjects (id, study_program_id, subject_id, semester_id, elective_group, is_required, created_at, updated_at) FROM stdin;
1	5	10	1	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	5	11	1	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	5	12	1	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	5	13	1	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	5	14	1	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	5	15	1	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	5	16	2	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	5	17	2	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	5	18	2	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	5	19	2	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
11	5	20	2	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
12	5	21	2	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
13	5	22	2	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
14	5	23	2	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
15	5	24	2	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
16	5	25	4	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
17	5	26	4	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
18	5	27	4	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
19	5	28	4	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
20	5	29	4	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
21	5	30	4	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
22	5	31	4	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
23	5	32	4	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
24	5	33	4	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
25	5	34	5	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
26	5	35	5	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
27	5	36	5	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
28	5	37	5	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
29	5	38	5	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
30	5	39	5	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
31	5	40	5	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
32	5	1	5	\N	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
33	5	2	5	\N	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
34	5	3	5	\N	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
35	5	4	5	\N	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
36	5	5	5	\N	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
37	5	6	5	\N	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
38	5	7	5	\N	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
39	5	8	5	\N	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
40	5	9	5	\N	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
41	2	10	1	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
42	2	11	1	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
43	2	12	1	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
44	2	13	1	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
45	2	14	1	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
46	2	15	1	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
47	2	16	2	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
48	2	17	2	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
49	2	18	2	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
50	2	19	2	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
51	2	20	2	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
52	2	21	2	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
53	2	22	2	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
54	2	23	2	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
55	2	24	2	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
56	2	25	4	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
57	2	26	4	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
58	2	27	4	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
59	2	28	4	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
60	2	29	4	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
61	2	30	4	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
62	2	31	4	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
63	2	32	4	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
64	2	33	4	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
65	2	34	5	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
66	2	35	5	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
67	2	36	5	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
68	2	37	5	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
69	2	38	5	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
70	2	39	5	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
71	2	40	5	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
72	2	1	5	\N	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
73	2	2	5	\N	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
74	2	3	5	\N	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
75	2	4	5	\N	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
76	2	5	5	\N	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
77	2	6	5	\N	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
78	2	7	5	\N	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
79	2	8	5	\N	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
80	2	9	5	\N	f	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: study_programs; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.study_programs (id, major_id, study_program_code, study_program_name, total_credits, start_year, training_type, is_active, created_at, updated_at) FROM stdin;
1	1	DHCQK35TA	Trí tuệ nhân tạo - Khóa 35	130	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	2	DHCQK35TI	Khoa học máy tính - Khóa 35	130	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	3	DHCQK35TE	Mạng máy tính và truyền thông dữ liệu - Khóa 35	130	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	4	DHCQK35TT	Hệ thống thông tin - Khóa 35	130	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	5	DHCQK35IT	Công nghệ thông tin - Khóa 35	130	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	6	DHCQK35EC	Thương mại điện tử - Khóa 35	125	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	7	DHCQK35IE	Kinh tế quốc tế - Khóa 35	125	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	8	DHCQK35EL	Luật kinh tế - Khóa 35	125	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	9	DHCQK35MK	Marketing - Khóa 35	125	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	10	DHCQK35AC	Kế toán - Khóa 35	125	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
11	11	DHCQK35FN	Tài chính - Ngân hàng - Khóa 35	125	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
12	12	DHCQK35LG	Logistics và Quản lí chuỗi cung ứng - Khóa 35	125	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
13	13	DHCQK35BA	Quản trị kinh doanh - Khóa 35	125	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
14	14	DHCQK35NU	Điều dưỡng - Khóa 35	140	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
15	15	DHCQK35EN	Ngôn ngữ Anh - Khóa 35	120	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
16	16	DHCQK35KR	Ngôn ngữ Hàn Quốc - Khóa 35	120	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
17	17	DHCQK35CN	Ngôn ngữ Trung Quốc - Khóa 35	120	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
18	18	DHCQK35JP	Ngôn ngữ Nhật - Khóa 35	120	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
19	19	DHCQK35VN	Việt Nam học - Khóa 35	120	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
20	20	DHCQK35TM	Quản trị và Du lịch - Lữ hành - Khóa 35	120	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
21	21	DHCQK35HM	Quản trị khách sạn - Khóa 35	120	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
22	22	DHCQK35MM	Truyền thông đa phương tiện - Khóa 35	120	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
23	23	DHCQK35GD	Thiết kế đồ họa - Khóa 35	120	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
24	24	DHCQK35VO	Thanh nhạc - Khóa 35	110	2022	CHINH_QUY	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: subject_enrollment_conditions; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.subject_enrollment_conditions (id, subject_id, condition_type, condition_value, condition_operator, description, created_at, updated_at) FROM stdin;
1	5	GPA	2.50	>=	Yeu cau GPA toi thieu 2.5	2025-12-01 00:00:00	2025-12-01 00:00:00
2	5	TOTAL_CREDITS	30.00	>=	Phai tich luy it nhat 30 tin chi	2025-12-01 00:00:00	2025-12-01 00:00:00
3	4	GPA	2.00	>	Yeu cau GPA > 2.0	2025-12-01 00:00:00	2025-12-01 00:00:00
4	6	TOTAL_CREDITS	20.00	>=	Yeu cau >= 20 tin chi	2025-12-01 00:00:00	2025-12-01 00:00:00
5	6	GPA	2.00	=	Yeu cau GPA toi thieu 2	2025-12-01 00:00:00	2025-12-01 00:00:00
\.


--
-- Data for Name: subject_prerequisite_group_items; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.subject_prerequisite_group_items (group_id, prerequisite_subject_id, created_at) FROM stdin;
1	5	2026-05-27 17:43:12.004061
2	8	2026-05-27 17:43:12.004061
3	11	2026-05-27 17:43:12.004061
4	20	2026-05-27 17:43:12.004061
5	40	2026-05-27 17:43:12.004061
6	14	2026-05-27 17:43:12.004061
7	15	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: subject_prerequisite_groups; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.subject_prerequisite_groups (id, subject_id, min_subjects_required, description, created_at, updated_at) FROM stdin;
1	6	1	Cần học học phần mã AD213	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	9	1	Cần học học phần mã GF101	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	20	1	Cần học học phần mã CS100	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	21	1	Cần học học phần mã CS121	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	22	1	Cần học học phần mã CS122	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	23	1	Cần học học phần mã GE111	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	24	1	Cần học học phần mã MA101	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: subjects; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.subjects (id, faculty_id, department_id, subject_code, subject_name, credits, coefficient, lecture_hours, practice_hours, is_active, created_at, updated_at) FROM stdin;
1	9	\N	AD205	Kỹ năng soạn thảo văn bản (MS Office)	3	1.00	45	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	6	\N	AD206	Ẩm thực Việt Nam	3	1.00	30	30	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	9	\N	AD207	Kỹ năng soạn thảo văn bản (MS Open)	3	1.00	45	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	9	\N	AD212	Phương pháp hùng biện và các thủ thuật tranh biện	3	1.00	45	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	9	\N	AD213	Hát - Nhạc	3	1.00	45	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
6	9	\N	AD214	Nâng cao chất lượng giọng hát	3	1.00	45	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
7	5	\N	AD215	Kỹ năng sống	3	1.00	45	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
8	4	11	GF101	Tiếng Pháp 1	2	1.00	54	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
9	4	11	GF102	Tiếng Pháp 2	2	1.00	54	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
10	4	9	PG102	GDTC: Thể dục cổ truyền cơ bản	1	1.00	25	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
11	1	\N	CS100	Tin đại cương	2	1.50	18	24	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
12	9	\N	CS102	Tin học văn phòng	2	1.50	30	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
13	2	\N	EC102	Nhập môn kinh tế học	2	1.20	30	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
14	4	8	GE111	Tiếng Anh sơ cấp 1	2	1.20	54	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
15	1	1	MA101	Logic, suy luận toán học và kỹ thuật đếm	3	1.20	27	36	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
16	9	\N	ML113	Triết học Mác - Lênin	3	1.20	45	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
17	9	\N	NA151	Khoa học môi trường	2	1.20	30	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
18	5	\N	SH131	Pháp luật đại cương	2	1.20	30	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
19	5	\N	VL101	Tiếng Việt thực hành	2	1.20	30	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
20	1	\N	CS121	Lập trình cơ sở 1	3	1.50	27	36	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
21	1	\N	CS212	Kiến trúc máy tính	3	1.50	45	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
22	1	\N	CS213	Cấu trúc dữ liệu và giải thuật	3	1.50	27	36	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
23	4	8	GE112	Tiếng Anh sơ cấp 2	2	1.20	54	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
24	1	1	MA120	Đại số tuyến tính	3	1.20	27	36	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
25	9	\N	ML114	Kinh tế chính trị Mác - Lênin	2	1.20	30	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
26	4	8	GE121	Tiếng Anh sơ trung cấp 1	2	1.20	54	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
27	1	1	MA110	Giải tích 1	3	1.20	27	36	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
28	1	1	MA111	Giải tích 2	3	1.20	27	36	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
29	1	\N	MI201	Toán rời rạc	3	1.20	45	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
30	9	\N	ML115	Chủ nghĩa xã hội khoa học	2	1.20	30	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
31	1	\N	CS111	Kỹ thuật số	2	1.50	18	24	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
32	1	1	CF213	Cấu trúc dữ liệu và giải thuật	4	1.60	45	36	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
33	4	8	GE222	Tiếng Anh sơ trung cấp 2	2	1.20	54	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
34	1	\N	IS222	Cơ sở dữ liệu	3	1.20	45	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
35	1	1	MA239	Xác suất thống kê	4	1.60	45	27	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
36	9	\N	ML202	Tư tưởng Hồ Chí Minh	2	1.20	30	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
37	1	\N	CS315	Nguyên lý hệ điều hành	3	1.20	45	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
38	1	\N	IS322	Hệ quản trị cơ sở dữ liệu	3	1.20	45	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
39	1	\N	IS314	Hệ thống thông tin	3	1.20	45	\N	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
40	1	\N	CS122	Lập trình hướng đối tượng	3	1.50	27	36	t	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: tuition_fee_configs; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.tuition_fee_configs (id, academic_year, cohort, base_price_per_credit, created_at, updated_at) FROM stdin;
1	2022-2023	2022	500000.00	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
2	2023-2024	2022	520000.00	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
3	2024-2025	2022	480000.00	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
4	2025-2026	2022	700000.00	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
5	2026-2027	2022	750000.00	2026-05-27 17:43:12.004061	2026-05-27 17:43:12.004061
\.


--
-- Data for Name: tuition_invoice_items; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.tuition_invoice_items (id, invoice_id, course_class_id, price_per_credit, credits, coefficient, amount, created_at, updated_at) FROM stdin;
1	1	1	500000.00	3	1.00	1500000.00	2025-12-01 00:00:00	2025-12-01 00:00:00
2	1	2	520000.00	3	1.00	1560000.00	2025-12-01 00:00:00	2025-12-01 00:00:00
3	1	3	480000.00	3	1.00	1440000.00	2025-12-01 00:00:00	2025-12-01 00:00:00
4	2	1	500000.00	3	1.00	1500000.00	2025-12-01 00:00:00	2025-12-01 00:00:00
5	2	2	520000.00	3	1.00	1560000.00	2025-12-01 00:00:00	2025-12-01 00:00:00
6	3	4	700000.00	3	1.00	2100000.00	2025-12-01 00:00:00	2025-12-01 00:00:00
7	3	5	750000.00	3	1.00	2250000.00	2025-12-01 00:00:00	2025-12-01 00:00:00
8	4	6	450000.00	3	1.00	1350000.00	2026-03-01 00:00:00	2026-03-01 00:00:00
9	5	4	700000.00	3	1.00	2100000.00	2026-03-01 00:00:00	2026-03-01 00:00:00
10	5	5	750000.00	3	1.00	2250000.00	2026-03-01 00:00:00	2026-03-01 00:00:00
11	6	1	500000.00	3	1.00	1500000.00	2025-12-01 00:00:00	2025-12-01 00:00:00
\.


--
-- Data for Name: tuition_invoices; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.tuition_invoices (id, student_id, semester_id, due_date, total_amount, final_amount, status, created_at, updated_at) FROM stdin;
1	1	1	2026-01-10	4500000.00	4500000.00	UNPAID	2025-12-01 00:00:00	2025-12-05 00:00:00
2	2	1	2026-01-05	4200000.00	4200000.00	OVERDUE	2025-12-01 00:00:00	2026-01-06 00:00:00
3	1	1	2026-01-15	4300000.00	4300000.00	UNPAID	2025-12-01 00:00:00	2025-12-01 00:00:00
4	4	2	2026-05-01	5000000.00	5000000.00	UNPAID	2026-03-01 00:00:00	2026-03-01 00:00:00
5	5	2	2026-05-01	5200000.00	5200000.00	PAID	2026-03-01 00:00:00	2026-03-10 00:00:00
6	6	1	2026-01-10	4000000.00	4000000.00	CANCELLED	2025-12-01 00:00:00	2025-12-20 00:00:00
\.


--
-- Data for Name: tuition_transactions; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.tuition_transactions (id, student_id, invoice_id, amount, type, reference_id, reference_type, description, created_at) FROM stdin;
1	1	1	4500000.00	TUITION	1	INVOICE	Tao hoa don hoc phi HK1	2025-12-01 00:00:00
2	1	1	4500000.00	PAYMENT	1	PAYMENT	Thanh toan qua ZaloPay	2025-12-05 00:00:00
3	2	2	4200000.00	TUITION	2	INVOICE	Hoc phi HK1	2025-12-01 00:00:00
4	2	2	4200000.00	PAYMENT	2	PAYMENT	Thanh toan that bai	2025-12-07 00:00:00
5	3	3	4300000.00	TUITION	3	INVOICE	Hoc phi HK1	2025-12-01 00:00:00
6	3	3	4300000.00	PAYMENT	4	PAYMENT	Dang xu ly thanh toan	2025-12-09 00:00:00
7	5	5	5200000.00	TUITION	5	INVOICE	Hoc phi HK2	2026-03-01 00:00:00
8	5	5	5200000.00	PAYMENT	5	PAYMENT	Thanh toan ngan hang	2026-03-10 00:00:00
9	6	6	4000000.00	TUITION	6	INVOICE	Hoc phi HK1	2025-12-01 00:00:00
10	6	6	4000000.00	PAYMENT	6	PAYMENT	Thanh toan thanh cong	2025-12-10 00:00:00
11	6	6	-4000000.00	REFUND	6	PAYMENT	Hoan tien do huy hoa don	2025-12-20 00:00:00
\.


--
-- Data for Name: user_devices; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.user_devices (id, oauth_user_id, device_id, fcm_token, platform, last_used_at, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: user_roles; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.user_roles (oauth_user_id, role_id, assigned_at) FROM stdin;
1	2	2026-05-27 17:43:12.004061
2	2	2026-05-27 17:43:12.004061
4	2	2026-05-27 17:43:12.004061
5	2	2026-05-27 17:43:12.004061
6	2	2026-05-27 17:43:12.004061
7	2	2026-05-27 17:43:12.004061
8	2	2026-05-27 17:43:12.004061
3	3	2026-05-27 17:43:12.004061
9	3	2026-05-27 17:43:12.004061
10	3	2026-05-27 17:43:12.004061
\.


--
-- Name: academic_advisors_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.academic_advisors_id_seq', 13, true);


--
-- Name: academic_infos_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.academic_infos_id_seq', 23, true);


--
-- Name: application_attachments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.application_attachments_id_seq', 3, true);


--
-- Name: application_types_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.application_types_id_seq', 3, true);


--
-- Name: attendances_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.attendances_id_seq', 1, false);


--
-- Name: class_schedules_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.class_schedules_id_seq', 18, true);


--
-- Name: course_classes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.course_classes_id_seq', 35, true);


--
-- Name: departments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.departments_id_seq', 11, true);


--
-- Name: emergency_contacts_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.emergency_contacts_id_seq', 22, true);


--
-- Name: enrollment_periods_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.enrollment_periods_id_seq', 1, false);


--
-- Name: exam_schedules_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.exam_schedules_id_seq', 15, true);


--
-- Name: faculties_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.faculties_id_seq', 9, true);


--
-- Name: feedback_attachments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.feedback_attachments_id_seq', 1, false);


--
-- Name: feedback_category_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.feedback_category_id_seq', 3, true);


--
-- Name: feedback_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.feedback_id_seq', 1, false);


--
-- Name: grade_scale_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.grade_scale_id_seq', 10, true);


--
-- Name: health_insurances_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.health_insurances_id_seq', 22, true);


--
-- Name: identity_cards_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.identity_cards_id_seq', 22, true);


--
-- Name: lecturers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.lecturers_id_seq', 7, true);


--
-- Name: majors_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.majors_id_seq', 24, true);


--
-- Name: news_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.news_id_seq', 5, true);


--
-- Name: notification_read_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.notification_read_id_seq', 12, true);


--
-- Name: notification_targets_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.notification_targets_id_seq', 13, true);


--
-- Name: notification_templates_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.notification_templates_id_seq', 6, true);


--
-- Name: notifications_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.notifications_id_seq', 18, true);


--
-- Name: oauth_users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.oauth_users_id_seq', 30, true);


--
-- Name: payment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.payment_id_seq', 6, true);


--
-- Name: roles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.roles_id_seq', 3, true);


--
-- Name: semesters_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.semesters_id_seq', 6, true);


--
-- Name: student_applications_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.student_applications_id_seq', 4, true);


--
-- Name: student_classes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.student_classes_id_seq', 18, true);


--
-- Name: student_contacts_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.student_contacts_id_seq', 22, true);


--
-- Name: student_course_class_logs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.student_course_class_logs_id_seq', 11, true);


--
-- Name: student_course_classes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.student_course_classes_id_seq', 48, true);


--
-- Name: student_exam_registrations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.student_exam_registrations_id_seq', 5, true);


--
-- Name: student_majors_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.student_majors_id_seq', 23, true);


--
-- Name: student_semester_summaries_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.student_semester_summaries_id_seq', 4, true);


--
-- Name: student_subject_results_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.student_subject_results_id_seq', 6, true);


--
-- Name: students_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.students_id_seq', 23, true);


--
-- Name: study_program_subjects_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.study_program_subjects_id_seq', 80, true);


--
-- Name: study_programs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.study_programs_id_seq', 24, true);


--
-- Name: subject_enrollment_conditions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.subject_enrollment_conditions_id_seq', 5, true);


--
-- Name: subject_prerequisite_groups_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.subject_prerequisite_groups_id_seq', 7, true);


--
-- Name: subjects_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.subjects_id_seq', 40, true);


--
-- Name: tuition_fee_configs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tuition_fee_configs_id_seq', 5, true);


--
-- Name: tuition_invoice_items_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tuition_invoice_items_id_seq', 11, true);


--
-- Name: tuition_invoices_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tuition_invoices_id_seq', 6, true);


--
-- Name: tuition_transactions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tuition_transactions_id_seq', 11, true);


--
-- Name: user_devices_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.user_devices_id_seq', 1, false);


--
-- Name: academic_advisors academic_advisors_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.academic_advisors
    ADD CONSTRAINT academic_advisors_pkey PRIMARY KEY (id);


--
-- Name: academic_advisors academic_advisors_student_class_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.academic_advisors
    ADD CONSTRAINT academic_advisors_student_class_id_key UNIQUE (student_class_id);


--
-- Name: academic_infos academic_infos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.academic_infos
    ADD CONSTRAINT academic_infos_pkey PRIMARY KEY (id);


--
-- Name: academic_infos academic_infos_student_major_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.academic_infos
    ADD CONSTRAINT academic_infos_student_major_id_key UNIQUE (student_major_id);


--
-- Name: application_attachments application_attachments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.application_attachments
    ADD CONSTRAINT application_attachments_pkey PRIMARY KEY (id);


--
-- Name: application_types application_types_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.application_types
    ADD CONSTRAINT application_types_code_key UNIQUE (code);


--
-- Name: application_types application_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.application_types
    ADD CONSTRAINT application_types_pkey PRIMARY KEY (id);


--
-- Name: attendances attendances_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.attendances
    ADD CONSTRAINT attendances_pkey PRIMARY KEY (id);


--
-- Name: class_schedules class_schedules_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.class_schedules
    ADD CONSTRAINT class_schedules_pkey PRIMARY KEY (id);


--
-- Name: course_classes course_classes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.course_classes
    ADD CONSTRAINT course_classes_pkey PRIMARY KEY (id);


--
-- Name: departments departments_department_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.departments
    ADD CONSTRAINT departments_department_code_key UNIQUE (department_code);


--
-- Name: departments departments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.departments
    ADD CONSTRAINT departments_pkey PRIMARY KEY (id);


--
-- Name: emergency_contacts emergency_contacts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.emergency_contacts
    ADD CONSTRAINT emergency_contacts_pkey PRIMARY KEY (id);


--
-- Name: emergency_contacts emergency_contacts_student_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.emergency_contacts
    ADD CONSTRAINT emergency_contacts_student_id_key UNIQUE (student_id);


--
-- Name: enrollment_periods enrollment_periods_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.enrollment_periods
    ADD CONSTRAINT enrollment_periods_pkey PRIMARY KEY (id);


--
-- Name: exam_schedules exam_schedules_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.exam_schedules
    ADD CONSTRAINT exam_schedules_pkey PRIMARY KEY (id);


--
-- Name: faculties faculties_faculty_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.faculties
    ADD CONSTRAINT faculties_faculty_code_key UNIQUE (faculty_code);


--
-- Name: faculties faculties_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.faculties
    ADD CONSTRAINT faculties_pkey PRIMARY KEY (id);


--
-- Name: feedback_attachments feedback_attachments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feedback_attachments
    ADD CONSTRAINT feedback_attachments_pkey PRIMARY KEY (id);


--
-- Name: feedback_category feedback_category_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feedback_category
    ADD CONSTRAINT feedback_category_name_key UNIQUE (name);


--
-- Name: feedback_category feedback_category_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feedback_category
    ADD CONSTRAINT feedback_category_pkey PRIMARY KEY (id);


--
-- Name: feedback feedback_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feedback
    ADD CONSTRAINT feedback_pkey PRIMARY KEY (id);


--
-- Name: grade_scale grade_scale_letter_grade_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grade_scale
    ADD CONSTRAINT grade_scale_letter_grade_key UNIQUE (letter_grade);


--
-- Name: grade_scale grade_scale_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grade_scale
    ADD CONSTRAINT grade_scale_pkey PRIMARY KEY (id);


--
-- Name: health_insurances health_insurances_insurance_number_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.health_insurances
    ADD CONSTRAINT health_insurances_insurance_number_key UNIQUE (insurance_number);


--
-- Name: health_insurances health_insurances_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.health_insurances
    ADD CONSTRAINT health_insurances_pkey PRIMARY KEY (id);


--
-- Name: identity_cards identity_cards_card_number_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.identity_cards
    ADD CONSTRAINT identity_cards_card_number_key UNIQUE (card_number);


--
-- Name: identity_cards identity_cards_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.identity_cards
    ADD CONSTRAINT identity_cards_pkey PRIMARY KEY (id);


--
-- Name: identity_cards identity_cards_student_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.identity_cards
    ADD CONSTRAINT identity_cards_student_id_key UNIQUE (student_id);


--
-- Name: lecturers lecturers_lecturer_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lecturers
    ADD CONSTRAINT lecturers_lecturer_code_key UNIQUE (lecturer_code);


--
-- Name: lecturers lecturers_oauth_user_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lecturers
    ADD CONSTRAINT lecturers_oauth_user_id_key UNIQUE (oauth_user_id);


--
-- Name: lecturers lecturers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lecturers
    ADD CONSTRAINT lecturers_pkey PRIMARY KEY (id);


--
-- Name: majors majors_major_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.majors
    ADD CONSTRAINT majors_major_code_key UNIQUE (major_code);


--
-- Name: majors majors_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.majors
    ADD CONSTRAINT majors_pkey PRIMARY KEY (id);


--
-- Name: news news_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.news
    ADD CONSTRAINT news_pkey PRIMARY KEY (id);


--
-- Name: enrollment_periods no_overlap_period; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.enrollment_periods
    ADD CONSTRAINT no_overlap_period EXCLUDE USING gist (tsrange(start_time, end_time, '[)'::text) WITH &&);


--
-- Name: notification_read notification_read_notification_id_oauth_user_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notification_read
    ADD CONSTRAINT notification_read_notification_id_oauth_user_id_key UNIQUE (notification_id, oauth_user_id);


--
-- Name: notification_read notification_read_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notification_read
    ADD CONSTRAINT notification_read_pkey PRIMARY KEY (id);


--
-- Name: notification_targets notification_targets_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notification_targets
    ADD CONSTRAINT notification_targets_pkey PRIMARY KEY (id);


--
-- Name: notification_templates notification_templates_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notification_templates
    ADD CONSTRAINT notification_templates_code_key UNIQUE (code);


--
-- Name: notification_templates notification_templates_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notification_templates
    ADD CONSTRAINT notification_templates_pkey PRIMARY KEY (id);


--
-- Name: notifications notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_pkey PRIMARY KEY (id);


--
-- Name: oauth_users oauth_users_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.oauth_users
    ADD CONSTRAINT oauth_users_email_key UNIQUE (email);


--
-- Name: oauth_users oauth_users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.oauth_users
    ADD CONSTRAINT oauth_users_pkey PRIMARY KEY (id);


--
-- Name: oauth_users oauth_users_user_uuid_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.oauth_users
    ADD CONSTRAINT oauth_users_user_uuid_key UNIQUE (user_uuid);


--
-- Name: payment payment_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_pkey PRIMARY KEY (id);


--
-- Name: payment payment_transaction_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_transaction_code_key UNIQUE (transaction_code);


--
-- Name: roles roles_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_code_key UNIQUE (code);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- Name: semesters semesters_academic_years_semester_number_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.semesters
    ADD CONSTRAINT semesters_academic_years_semester_number_key UNIQUE (academic_years, semester_number);


--
-- Name: semesters semesters_daterange_excl; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.semesters
    ADD CONSTRAINT semesters_daterange_excl EXCLUDE USING gist (daterange(start_date, end_date, '[]'::text) WITH &&);


--
-- Name: semesters semesters_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.semesters
    ADD CONSTRAINT semesters_pkey PRIMARY KEY (id);


--
-- Name: semesters semesters_semester_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.semesters
    ADD CONSTRAINT semesters_semester_code_key UNIQUE (semester_code);


--
-- Name: student_applications student_applications_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_applications
    ADD CONSTRAINT student_applications_pkey PRIMARY KEY (id);


--
-- Name: student_classes student_classes_class_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_classes
    ADD CONSTRAINT student_classes_class_code_key UNIQUE (class_code);


--
-- Name: student_classes student_classes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_classes
    ADD CONSTRAINT student_classes_pkey PRIMARY KEY (id);


--
-- Name: student_contacts student_contacts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_contacts
    ADD CONSTRAINT student_contacts_pkey PRIMARY KEY (id);


--
-- Name: student_contacts student_contacts_student_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_contacts
    ADD CONSTRAINT student_contacts_student_id_key UNIQUE (student_id);


--
-- Name: student_course_class_logs student_course_class_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_course_class_logs
    ADD CONSTRAINT student_course_class_logs_pkey PRIMARY KEY (id);


--
-- Name: student_course_classes student_course_classes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_course_classes
    ADD CONSTRAINT student_course_classes_pkey PRIMARY KEY (id);


--
-- Name: student_course_classes student_course_classes_student_id_course_class_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_course_classes
    ADD CONSTRAINT student_course_classes_student_id_course_class_id_key UNIQUE (student_id, course_class_id);


--
-- Name: student_course_classes student_course_classes_student_id_subject_id_semester_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_course_classes
    ADD CONSTRAINT student_course_classes_student_id_subject_id_semester_id_key UNIQUE (student_id, subject_id, semester_id);


--
-- Name: student_exam_registrations student_exam_registrations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_exam_registrations
    ADD CONSTRAINT student_exam_registrations_pkey PRIMARY KEY (id);


--
-- Name: student_exam_registrations student_exam_registrations_student_id_exam_schedule_id_exam_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_exam_registrations
    ADD CONSTRAINT student_exam_registrations_student_id_exam_schedule_id_exam_key UNIQUE (student_id, exam_schedule_id, exam_attempt);


--
-- Name: student_majors student_majors_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_majors
    ADD CONSTRAINT student_majors_pkey PRIMARY KEY (id);


--
-- Name: student_majors student_majors_student_id_major_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_majors
    ADD CONSTRAINT student_majors_student_id_major_id_key UNIQUE (student_id, major_id);


--
-- Name: student_majors student_majors_student_id_study_program_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_majors
    ADD CONSTRAINT student_majors_student_id_study_program_id_key UNIQUE (student_id, study_program_id);


--
-- Name: student_semester_summaries student_semester_summaries_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_semester_summaries
    ADD CONSTRAINT student_semester_summaries_pkey PRIMARY KEY (id);


--
-- Name: student_subject_results student_subject_results_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_subject_results
    ADD CONSTRAINT student_subject_results_pkey PRIMARY KEY (id);


--
-- Name: student_subject_results student_subject_results_student_id_subject_id_semester_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_subject_results
    ADD CONSTRAINT student_subject_results_student_id_subject_id_semester_id_key UNIQUE (student_id, subject_id, semester_id);


--
-- Name: students students_oauth_user_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.students
    ADD CONSTRAINT students_oauth_user_id_key UNIQUE (oauth_user_id);


--
-- Name: students students_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.students
    ADD CONSTRAINT students_pkey PRIMARY KEY (id);


--
-- Name: students students_student_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.students
    ADD CONSTRAINT students_student_code_key UNIQUE (student_code);


--
-- Name: study_program_subjects study_program_subjects_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.study_program_subjects
    ADD CONSTRAINT study_program_subjects_pkey PRIMARY KEY (id);


--
-- Name: study_program_subjects study_program_subjects_study_program_id_subject_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.study_program_subjects
    ADD CONSTRAINT study_program_subjects_study_program_id_subject_id_key UNIQUE (study_program_id, subject_id);


--
-- Name: study_programs study_programs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.study_programs
    ADD CONSTRAINT study_programs_pkey PRIMARY KEY (id);


--
-- Name: study_programs study_programs_study_program_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.study_programs
    ADD CONSTRAINT study_programs_study_program_code_key UNIQUE (study_program_code);


--
-- Name: subject_enrollment_conditions subject_enrollment_conditions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subject_enrollment_conditions
    ADD CONSTRAINT subject_enrollment_conditions_pkey PRIMARY KEY (id);


--
-- Name: subject_enrollment_conditions subject_enrollment_conditions_subject_id_condition_type_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subject_enrollment_conditions
    ADD CONSTRAINT subject_enrollment_conditions_subject_id_condition_type_key UNIQUE (subject_id, condition_type);


--
-- Name: subject_prerequisite_group_items subject_prerequisite_group_items_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subject_prerequisite_group_items
    ADD CONSTRAINT subject_prerequisite_group_items_pkey PRIMARY KEY (group_id, prerequisite_subject_id);


--
-- Name: subject_prerequisite_groups subject_prerequisite_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subject_prerequisite_groups
    ADD CONSTRAINT subject_prerequisite_groups_pkey PRIMARY KEY (id);


--
-- Name: subjects subjects_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subjects
    ADD CONSTRAINT subjects_pkey PRIMARY KEY (id);


--
-- Name: subjects subjects_subject_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subjects
    ADD CONSTRAINT subjects_subject_code_key UNIQUE (subject_code);


--
-- Name: tuition_fee_configs tuition_fee_configs_academic_year_cohort_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tuition_fee_configs
    ADD CONSTRAINT tuition_fee_configs_academic_year_cohort_key UNIQUE (academic_year, cohort);


--
-- Name: tuition_fee_configs tuition_fee_configs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tuition_fee_configs
    ADD CONSTRAINT tuition_fee_configs_pkey PRIMARY KEY (id);


--
-- Name: tuition_invoice_items tuition_invoice_items_invoice_id_course_class_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tuition_invoice_items
    ADD CONSTRAINT tuition_invoice_items_invoice_id_course_class_id_key UNIQUE (invoice_id, course_class_id);


--
-- Name: tuition_invoice_items tuition_invoice_items_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tuition_invoice_items
    ADD CONSTRAINT tuition_invoice_items_pkey PRIMARY KEY (id);


--
-- Name: tuition_invoices tuition_invoices_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tuition_invoices
    ADD CONSTRAINT tuition_invoices_pkey PRIMARY KEY (id);


--
-- Name: tuition_transactions tuition_transactions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tuition_transactions
    ADD CONSTRAINT tuition_transactions_pkey PRIMARY KEY (id);


--
-- Name: user_devices user_devices_device_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_devices
    ADD CONSTRAINT user_devices_device_id_key UNIQUE (device_id);


--
-- Name: user_devices user_devices_fcm_token_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_devices
    ADD CONSTRAINT user_devices_fcm_token_key UNIQUE (fcm_token);


--
-- Name: user_devices user_devices_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_devices
    ADD CONSTRAINT user_devices_pkey PRIMARY KEY (id);


--
-- Name: user_roles user_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (oauth_user_id, role_id);


--
-- Name: idx_academic_advisors_lecturer_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_academic_advisors_lecturer_id ON public.academic_advisors USING btree (lecturer_id);


--
-- Name: idx_application_attachments_application_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_application_attachments_application_id ON public.application_attachments USING btree (application_id);


--
-- Name: idx_attendance_course_class_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_attendance_course_class_id ON public.attendances USING btree (course_class_id);


--
-- Name: idx_attendance_session_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_attendance_session_id ON public.attendances USING btree (session_id);


--
-- Name: idx_attendance_student_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_attendance_student_id ON public.attendances USING btree (student_id);


--
-- Name: idx_class_schedules_course_class_day; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_class_schedules_course_class_day ON public.class_schedules USING btree (course_class_id, day_of_week);


--
-- Name: idx_course_classes_lecturer_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_course_classes_lecturer_id ON public.course_classes USING btree (lecturer_id);


--
-- Name: idx_course_classes_semester_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_course_classes_semester_id ON public.course_classes USING btree (semester_id);


--
-- Name: idx_course_classes_subject_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_course_classes_subject_id ON public.course_classes USING btree (subject_id);


--
-- Name: idx_departments_faculty_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_departments_faculty_id ON public.departments USING btree (faculty_id);


--
-- Name: idx_exam_schedules_semester_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_exam_schedules_semester_id ON public.exam_schedules USING btree (semester_id);


--
-- Name: idx_exam_schedules_subject_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_exam_schedules_subject_id ON public.exam_schedules USING btree (subject_id);


--
-- Name: idx_feedback_attachments_feedback_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_feedback_attachments_feedback_id ON public.feedback_attachments USING btree (feedback_id);


--
-- Name: idx_feedback_category_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_feedback_category_id ON public.feedback USING btree (category_id);


--
-- Name: idx_health_insurances_student_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_health_insurances_student_id ON public.health_insurances USING btree (student_id);


--
-- Name: idx_lecturers_department_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_lecturers_department_id ON public.lecturers USING btree (department_id);


--
-- Name: idx_majors_faculty_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_majors_faculty_id ON public.majors USING btree (faculty_id);


--
-- Name: idx_news_publish_date; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_news_publish_date ON public.news USING btree (publish_date DESC);


--
-- Name: idx_notification_read_user_notification; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_notification_read_user_notification ON public.notification_read USING btree (oauth_user_id, notification_id);


--
-- Name: idx_notification_targets_notification_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_notification_targets_notification_id ON public.notification_targets USING btree (notification_id);


--
-- Name: idx_notification_targets_target_notification; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_notification_targets_target_notification ON public.notification_targets USING btree (target_id, notification_id);


--
-- Name: idx_notifications_created; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_notifications_created ON public.notifications USING btree (created_at DESC);


--
-- Name: idx_notifications_target_type; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_notifications_target_type ON public.notifications USING btree (target_type);


--
-- Name: idx_oauth_users_user_uuid; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_oauth_users_user_uuid ON public.oauth_users USING btree (user_uuid);


--
-- Name: idx_payment_invoice_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_payment_invoice_id ON public.payment USING btree (invoice_id);


--
-- Name: idx_student_applications_student_id_application_type_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_student_applications_student_id_application_type_id ON public.student_applications USING btree (student_id, application_type_id);


--
-- Name: idx_student_classes_major_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_student_classes_major_id ON public.student_classes USING btree (major_id);


--
-- Name: idx_student_course_classes_course_class_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_student_course_classes_course_class_id ON public.student_course_classes USING btree (course_class_id);


--
-- Name: idx_student_course_classes_student_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_student_course_classes_student_id ON public.student_course_classes USING btree (student_id);


--
-- Name: idx_student_majors_major_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_student_majors_major_id ON public.student_majors USING btree (major_id);


--
-- Name: idx_student_majors_program_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_student_majors_program_id ON public.student_majors USING btree (study_program_id);


--
-- Name: idx_student_majors_student_id_primary; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_student_majors_student_id_primary ON public.student_majors USING btree (student_id, is_primary);


--
-- Name: idx_student_semester_summaries_semester_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_student_semester_summaries_semester_id ON public.student_semester_summaries USING btree (semester_id);


--
-- Name: idx_student_semester_summaries_student_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_student_semester_summaries_student_id ON public.student_semester_summaries USING btree (student_id);


--
-- Name: idx_student_semester_summaries_study_program_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_student_semester_summaries_study_program_id ON public.student_semester_summaries USING btree (study_program_id);


--
-- Name: idx_student_subject_results_student_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_student_subject_results_student_id ON public.student_subject_results USING btree (student_id);


--
-- Name: idx_students_student_class_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_students_student_class_id ON public.students USING btree (student_class_id);


--
-- Name: idx_study_program_subjects_subject_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_study_program_subjects_subject_id ON public.study_program_subjects USING btree (subject_id);


--
-- Name: idx_study_programs_major_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_study_programs_major_id ON public.study_programs USING btree (major_id);


--
-- Name: idx_subject_enrollment_conditions_subject_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_subject_enrollment_conditions_subject_id ON public.subject_enrollment_conditions USING btree (subject_id);


--
-- Name: idx_subject_prerequisite_group_items_prerequisite_subject_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_subject_prerequisite_group_items_prerequisite_subject_id ON public.subject_prerequisite_group_items USING btree (prerequisite_subject_id);


--
-- Name: idx_subject_prerequisite_groups_subject_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_subject_prerequisite_groups_subject_id ON public.subject_prerequisite_groups USING btree (subject_id);


--
-- Name: idx_subjects_department_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_subjects_department_id ON public.subjects USING btree (department_id);


--
-- Name: idx_subjects_faculty_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_subjects_faculty_id ON public.subjects USING btree (faculty_id);


--
-- Name: idx_tuition_invoices_semester_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_tuition_invoices_semester_id ON public.tuition_invoices USING btree (semester_id);


--
-- Name: idx_tuition_invoices_student_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_tuition_invoices_student_id ON public.tuition_invoices USING btree (student_id);


--
-- Name: idx_user_devices_oauth_user_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_user_devices_oauth_user_id ON public.user_devices USING btree (oauth_user_id);


--
-- Name: idx_user_roles_role_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_user_roles_role_id ON public.user_roles USING btree (role_id);


--
-- Name: student_course_classes trg_sync_student_course_class; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_sync_student_course_class BEFORE INSERT ON public.student_course_classes FOR EACH ROW EXECUTE FUNCTION public.sync_student_course_class_fields();


--
-- Name: academic_advisors academic_advisors_lecturer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.academic_advisors
    ADD CONSTRAINT academic_advisors_lecturer_id_fkey FOREIGN KEY (lecturer_id) REFERENCES public.lecturers(id) ON DELETE CASCADE;


--
-- Name: academic_advisors academic_advisors_student_class_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.academic_advisors
    ADD CONSTRAINT academic_advisors_student_class_id_fkey FOREIGN KEY (student_class_id) REFERENCES public.student_classes(id) ON DELETE CASCADE;


--
-- Name: academic_infos academic_infos_student_major_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.academic_infos
    ADD CONSTRAINT academic_infos_student_major_id_fkey FOREIGN KEY (student_major_id) REFERENCES public.student_majors(id) ON DELETE CASCADE;


--
-- Name: application_attachments application_attachments_application_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.application_attachments
    ADD CONSTRAINT application_attachments_application_id_fkey FOREIGN KEY (application_id) REFERENCES public.student_applications(id) ON DELETE CASCADE;


--
-- Name: attendances attendances_course_class_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.attendances
    ADD CONSTRAINT attendances_course_class_id_fkey FOREIGN KEY (course_class_id) REFERENCES public.course_classes(id) ON DELETE CASCADE;


--
-- Name: attendances attendances_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.attendances
    ADD CONSTRAINT attendances_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(id) ON DELETE CASCADE;


--
-- Name: class_schedules class_schedules_course_class_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.class_schedules
    ADD CONSTRAINT class_schedules_course_class_id_fkey FOREIGN KEY (course_class_id) REFERENCES public.course_classes(id) ON DELETE CASCADE;


--
-- Name: course_classes course_classes_lecturer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.course_classes
    ADD CONSTRAINT course_classes_lecturer_id_fkey FOREIGN KEY (lecturer_id) REFERENCES public.lecturers(id) ON DELETE SET NULL;


--
-- Name: course_classes course_classes_semester_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.course_classes
    ADD CONSTRAINT course_classes_semester_id_fkey FOREIGN KEY (semester_id) REFERENCES public.semesters(id) ON DELETE RESTRICT;


--
-- Name: course_classes course_classes_subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.course_classes
    ADD CONSTRAINT course_classes_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES public.subjects(id) ON DELETE SET NULL;


--
-- Name: departments departments_faculty_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.departments
    ADD CONSTRAINT departments_faculty_id_fkey FOREIGN KEY (faculty_id) REFERENCES public.faculties(id) ON DELETE RESTRICT;


--
-- Name: emergency_contacts emergency_contacts_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.emergency_contacts
    ADD CONSTRAINT emergency_contacts_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(id) ON DELETE CASCADE;


--
-- Name: enrollment_periods enrollment_periods_semester_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.enrollment_periods
    ADD CONSTRAINT enrollment_periods_semester_id_fkey FOREIGN KEY (semester_id) REFERENCES public.semesters(id) ON DELETE RESTRICT;


--
-- Name: exam_schedules exam_schedules_semester_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.exam_schedules
    ADD CONSTRAINT exam_schedules_semester_id_fkey FOREIGN KEY (semester_id) REFERENCES public.semesters(id) ON DELETE CASCADE;


--
-- Name: exam_schedules exam_schedules_subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.exam_schedules
    ADD CONSTRAINT exam_schedules_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES public.subjects(id) ON DELETE CASCADE;


--
-- Name: feedback_attachments feedback_attachments_feedback_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feedback_attachments
    ADD CONSTRAINT feedback_attachments_feedback_id_fkey FOREIGN KEY (feedback_id) REFERENCES public.feedback(id) ON DELETE CASCADE;


--
-- Name: feedback feedback_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feedback
    ADD CONSTRAINT feedback_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.feedback_category(id) ON DELETE SET NULL;


--
-- Name: feedback feedback_oauth_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.feedback
    ADD CONSTRAINT feedback_oauth_user_id_fkey FOREIGN KEY (oauth_user_id) REFERENCES public.oauth_users(id) ON DELETE SET NULL;


--
-- Name: health_insurances health_insurances_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.health_insurances
    ADD CONSTRAINT health_insurances_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(id) ON DELETE CASCADE;


--
-- Name: identity_cards identity_cards_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.identity_cards
    ADD CONSTRAINT identity_cards_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(id) ON DELETE CASCADE;


--
-- Name: lecturers lecturers_department_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lecturers
    ADD CONSTRAINT lecturers_department_id_fkey FOREIGN KEY (department_id) REFERENCES public.departments(id) ON DELETE SET NULL;


--
-- Name: lecturers lecturers_oauth_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lecturers
    ADD CONSTRAINT lecturers_oauth_user_id_fkey FOREIGN KEY (oauth_user_id) REFERENCES public.oauth_users(id) ON DELETE SET NULL;


--
-- Name: majors majors_faculty_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.majors
    ADD CONSTRAINT majors_faculty_id_fkey FOREIGN KEY (faculty_id) REFERENCES public.faculties(id) ON DELETE RESTRICT;


--
-- Name: notification_read notification_read_notification_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notification_read
    ADD CONSTRAINT notification_read_notification_id_fkey FOREIGN KEY (notification_id) REFERENCES public.notifications(id) ON DELETE CASCADE;


--
-- Name: notification_read notification_read_oauth_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notification_read
    ADD CONSTRAINT notification_read_oauth_user_id_fkey FOREIGN KEY (oauth_user_id) REFERENCES public.oauth_users(id) ON DELETE CASCADE;


--
-- Name: notification_targets notification_targets_notification_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notification_targets
    ADD CONSTRAINT notification_targets_notification_id_fkey FOREIGN KEY (notification_id) REFERENCES public.notifications(id) ON DELETE CASCADE;


--
-- Name: payment payment_invoice_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_invoice_id_fkey FOREIGN KEY (invoice_id) REFERENCES public.tuition_invoices(id) ON DELETE RESTRICT;


--
-- Name: student_applications student_applications_application_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_applications
    ADD CONSTRAINT student_applications_application_type_id_fkey FOREIGN KEY (application_type_id) REFERENCES public.application_types(id) ON DELETE SET NULL;


--
-- Name: student_applications student_applications_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_applications
    ADD CONSTRAINT student_applications_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(id) ON DELETE RESTRICT;


--
-- Name: student_classes student_classes_major_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_classes
    ADD CONSTRAINT student_classes_major_id_fkey FOREIGN KEY (major_id) REFERENCES public.majors(id) ON DELETE RESTRICT;


--
-- Name: student_contacts student_contacts_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_contacts
    ADD CONSTRAINT student_contacts_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(id) ON DELETE CASCADE;


--
-- Name: student_course_class_logs student_course_class_logs_course_class_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_course_class_logs
    ADD CONSTRAINT student_course_class_logs_course_class_id_fkey FOREIGN KEY (course_class_id) REFERENCES public.course_classes(id) ON DELETE CASCADE;


--
-- Name: student_course_class_logs student_course_class_logs_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_course_class_logs
    ADD CONSTRAINT student_course_class_logs_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(id) ON DELETE CASCADE;


--
-- Name: student_course_classes student_course_classes_course_class_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_course_classes
    ADD CONSTRAINT student_course_classes_course_class_id_fkey FOREIGN KEY (course_class_id) REFERENCES public.course_classes(id) ON DELETE CASCADE;


--
-- Name: student_course_classes student_course_classes_semester_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_course_classes
    ADD CONSTRAINT student_course_classes_semester_id_fkey FOREIGN KEY (semester_id) REFERENCES public.semesters(id) ON DELETE RESTRICT;


--
-- Name: student_course_classes student_course_classes_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_course_classes
    ADD CONSTRAINT student_course_classes_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(id) ON DELETE CASCADE;


--
-- Name: student_course_classes student_course_classes_subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_course_classes
    ADD CONSTRAINT student_course_classes_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES public.subjects(id) ON DELETE SET NULL;


--
-- Name: student_exam_registrations student_exam_registrations_exam_schedule_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_exam_registrations
    ADD CONSTRAINT student_exam_registrations_exam_schedule_id_fkey FOREIGN KEY (exam_schedule_id) REFERENCES public.exam_schedules(id) ON DELETE CASCADE;


--
-- Name: student_exam_registrations student_exam_registrations_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_exam_registrations
    ADD CONSTRAINT student_exam_registrations_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(id) ON DELETE CASCADE;


--
-- Name: student_majors student_majors_major_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_majors
    ADD CONSTRAINT student_majors_major_id_fkey FOREIGN KEY (major_id) REFERENCES public.majors(id) ON DELETE CASCADE;


--
-- Name: student_majors student_majors_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_majors
    ADD CONSTRAINT student_majors_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(id) ON DELETE CASCADE;


--
-- Name: student_majors student_majors_study_program_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_majors
    ADD CONSTRAINT student_majors_study_program_id_fkey FOREIGN KEY (study_program_id) REFERENCES public.study_programs(id) ON DELETE SET NULL;


--
-- Name: student_semester_summaries student_semester_summaries_semester_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_semester_summaries
    ADD CONSTRAINT student_semester_summaries_semester_id_fkey FOREIGN KEY (semester_id) REFERENCES public.semesters(id) ON DELETE RESTRICT;


--
-- Name: student_semester_summaries student_semester_summaries_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_semester_summaries
    ADD CONSTRAINT student_semester_summaries_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(id) ON DELETE RESTRICT;


--
-- Name: student_semester_summaries student_semester_summaries_study_program_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_semester_summaries
    ADD CONSTRAINT student_semester_summaries_study_program_id_fkey FOREIGN KEY (study_program_id) REFERENCES public.study_programs(id) ON DELETE RESTRICT;


--
-- Name: student_subject_results student_subject_results_semester_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_subject_results
    ADD CONSTRAINT student_subject_results_semester_id_fkey FOREIGN KEY (semester_id) REFERENCES public.semesters(id) ON DELETE RESTRICT;


--
-- Name: student_subject_results student_subject_results_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_subject_results
    ADD CONSTRAINT student_subject_results_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(id) ON DELETE RESTRICT;


--
-- Name: student_subject_results student_subject_results_subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.student_subject_results
    ADD CONSTRAINT student_subject_results_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES public.subjects(id) ON DELETE RESTRICT;


--
-- Name: students students_oauth_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.students
    ADD CONSTRAINT students_oauth_user_id_fkey FOREIGN KEY (oauth_user_id) REFERENCES public.oauth_users(id) ON DELETE SET NULL;


--
-- Name: students students_student_class_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.students
    ADD CONSTRAINT students_student_class_id_fkey FOREIGN KEY (student_class_id) REFERENCES public.student_classes(id) ON DELETE SET NULL;


--
-- Name: study_program_subjects study_program_subjects_semester_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.study_program_subjects
    ADD CONSTRAINT study_program_subjects_semester_id_fkey FOREIGN KEY (semester_id) REFERENCES public.semesters(id) ON DELETE RESTRICT;


--
-- Name: study_program_subjects study_program_subjects_study_program_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.study_program_subjects
    ADD CONSTRAINT study_program_subjects_study_program_id_fkey FOREIGN KEY (study_program_id) REFERENCES public.study_programs(id) ON DELETE CASCADE;


--
-- Name: study_program_subjects study_program_subjects_subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.study_program_subjects
    ADD CONSTRAINT study_program_subjects_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES public.subjects(id) ON DELETE CASCADE;


--
-- Name: study_programs study_programs_major_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.study_programs
    ADD CONSTRAINT study_programs_major_id_fkey FOREIGN KEY (major_id) REFERENCES public.majors(id) ON DELETE RESTRICT;


--
-- Name: subject_enrollment_conditions subject_enrollment_conditions_subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subject_enrollment_conditions
    ADD CONSTRAINT subject_enrollment_conditions_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES public.subjects(id) ON DELETE CASCADE;


--
-- Name: subject_prerequisite_group_items subject_prerequisite_group_items_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subject_prerequisite_group_items
    ADD CONSTRAINT subject_prerequisite_group_items_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.subject_prerequisite_groups(id) ON DELETE CASCADE;


--
-- Name: subject_prerequisite_group_items subject_prerequisite_group_items_prerequisite_subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subject_prerequisite_group_items
    ADD CONSTRAINT subject_prerequisite_group_items_prerequisite_subject_id_fkey FOREIGN KEY (prerequisite_subject_id) REFERENCES public.subjects(id) ON DELETE CASCADE;


--
-- Name: subject_prerequisite_groups subject_prerequisite_groups_subject_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subject_prerequisite_groups
    ADD CONSTRAINT subject_prerequisite_groups_subject_id_fkey FOREIGN KEY (subject_id) REFERENCES public.subjects(id) ON DELETE CASCADE;


--
-- Name: subjects subjects_department_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subjects
    ADD CONSTRAINT subjects_department_id_fkey FOREIGN KEY (department_id) REFERENCES public.departments(id) ON DELETE SET NULL;


--
-- Name: subjects subjects_faculty_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subjects
    ADD CONSTRAINT subjects_faculty_id_fkey FOREIGN KEY (faculty_id) REFERENCES public.faculties(id) ON DELETE SET NULL;


--
-- Name: tuition_invoice_items tuition_invoice_items_course_class_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tuition_invoice_items
    ADD CONSTRAINT tuition_invoice_items_course_class_id_fkey FOREIGN KEY (course_class_id) REFERENCES public.course_classes(id) ON DELETE SET NULL;


--
-- Name: tuition_invoice_items tuition_invoice_items_invoice_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tuition_invoice_items
    ADD CONSTRAINT tuition_invoice_items_invoice_id_fkey FOREIGN KEY (invoice_id) REFERENCES public.tuition_invoices(id) ON DELETE CASCADE;


--
-- Name: tuition_invoices tuition_invoices_semester_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tuition_invoices
    ADD CONSTRAINT tuition_invoices_semester_id_fkey FOREIGN KEY (semester_id) REFERENCES public.semesters(id) ON DELETE RESTRICT;


--
-- Name: tuition_invoices tuition_invoices_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tuition_invoices
    ADD CONSTRAINT tuition_invoices_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.students(id) ON DELETE RESTRICT;


--
-- Name: tuition_transactions tuition_transactions_invoice_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tuition_transactions
    ADD CONSTRAINT tuition_transactions_invoice_id_fkey FOREIGN KEY (invoice_id) REFERENCES public.tuition_invoices(id) ON DELETE RESTRICT;


--
-- Name: user_devices user_devices_oauth_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_devices
    ADD CONSTRAINT user_devices_oauth_user_id_fkey FOREIGN KEY (oauth_user_id) REFERENCES public.oauth_users(id) ON DELETE CASCADE;


--
-- Name: user_roles user_roles_oauth_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_oauth_user_id_fkey FOREIGN KEY (oauth_user_id) REFERENCES public.oauth_users(id) ON DELETE CASCADE;


--
-- Name: user_roles user_roles_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_role_id_fkey FOREIGN KEY (role_id) REFERENCES public.roles(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

\unrestrict Sc8bPxg2fs6m04lM3L5vnCMlJiS8ZSNXW2c10emG2tUAmvBtaajX2eg4fRBT1qE

