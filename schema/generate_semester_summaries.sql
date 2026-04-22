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

    WHERE ssr.semester_id = :semesterId

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