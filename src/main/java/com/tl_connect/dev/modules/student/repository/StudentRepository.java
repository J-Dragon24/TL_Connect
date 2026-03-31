package com.tl_connect.dev.modules.student.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.student.entity.Student;
import com.tl_connect.dev.modules.student.projection.HealthInsuranceView;
import com.tl_connect.dev.modules.student.projection.StudentInfoView;
import com.tl_connect.dev.modules.student.projection.StudentRow;
import com.tl_connect.dev.modules.student.projection.StudyYearView;
import com.tl_connect.dev.modules.student_class.projection.ClassHeaderView;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByStudentCode(String studentCode);

    Optional<Student> findByStudentCode(String studentCode);

    boolean existsByStudentClassId(Long studentClassId);

    List<Student> findByStudentCodeIn(Collection<String> studentCodes);

    @Query(value = """
            SELECT
                s.student_code AS studentCode,
                s.full_name AS fullName,
                s.gender AS gender,
                s.date_of_birth AS dateOfBirth,
                c.class_code AS classCode,
                l.full_name AS academicAdvisor,
                sm.start_year AS startYear,
                sm.end_year AS endYear,
                m.major_code AS majorCode,
                m.major_name AS majorName,
                f.faculty_name AS faculty,
                i.card_number AS idCardNumber,
                i.card_type AS idCardType,
                i.issued_date AS issuedDate,
                i.issued_place AS issuedPlace,
                sc.phone_number AS phoneNumber,
                sc.address AS address,
                sc.email_personal AS email,
                ai.cohort AS cohort,
                ai.position AS position,
                sp.training_type AS trainingType,
                ec.full_name AS emergencyContactName,
                ec.phone_number AS emergencyContactPhoneNumber,
                ec.address AS emergencyContactAddress,
                ec.relationship AS relationship
            FROM students s
            LEFT JOIN student_classes c ON s.student_class_id = c.id
            LEFT JOIN student_majors sm ON s.id = sm.student_id AND sm.is_primary = true
            LEFT JOIN study_programs sp ON sm.study_program_id = sp.id
            LEFT JOIN majors m ON sm.major_id = m.id
            LEFT JOIN faculties f ON m.faculty_id = f.id
            LEFT JOIN identity_cards i ON s.id = i.student_id
            LEFT JOIN student_contacts sc ON s.id = sc.student_id
            LEFT JOIN academic_infos ai ON sm.id = ai.student_major_id
            LEFT JOIN emergency_contacts ec ON s.id = ec.student_id
            LEFT JOIN academic_advisors aa ON c.id = aa.student_class_id
            LEFT JOIN lecturers l ON aa.lecturer_id = l.id
            WHERE s.id = :id
            """, nativeQuery = true)
    Optional<StudentInfoView> findStudentInfoById(@Param("id") Long id);

    @Query(value = """
            SELECT
                s.id as id,
                s.student_code AS studentCode,
                s.full_name AS fullName,
                s.gender AS gender,
                s.date_of_birth AS dateOfBirth,
                c.class_code AS classCode,
                m.major_code AS majorCode,
                sp.training_type AS trainingType,
                sm.start_year AS startYear,
                sm.end_year AS endYear,
                i.card_number AS idCardNumber,
                i.card_type AS idCardType,
                i.issued_date AS issuedDate,
                i.issued_place AS issuedPlace,
                sc.phone_number AS phoneNumber,
                sc.address AS address,
                sc.email_personal AS email,
                ai.cohort AS cohort,
                ai.position AS position,
                ec.full_name AS emergencyContactName,
                ec.phone_number AS emergencyContactPhoneNumber,
                ec.address AS emergencyContactAddress,
                ec.relationship AS relationship
            FROM students s
            LEFT JOIN student_classes c ON s.student_class_id = c.id
            LEFT JOIN student_majors sm ON s.id = sm.student_id AND sm.is_primary = true
            LEFT JOIN study_programs sp ON sm.study_program_id = sp.id
            LEFT JOIN majors m ON sm.major_id = m.id
            LEFT JOIN faculties f ON m.faculty_id = f.id
            LEFT JOIN identity_cards i ON s.id = i.student_id
            LEFT JOIN student_contacts sc ON s.id = sc.student_id
            LEFT JOIN academic_infos ai ON sm.id = ai.student_major_id
            LEFT JOIN emergency_contacts ec ON s.id = ec.student_id
            ORDER BY s.student_code DESC
            """, countQuery = """
                SELECT COUNT(s.id)
                FROM students s
                LEFT JOIN student_classes c ON s.student_class_id = c.id
                LEFT JOIN student_majors sm ON s.id = sm.student_id AND sm.is_primary = true
                LEFT JOIN study_programs sp ON sm.study_program_id = sp.id
                LEFT JOIN majors m ON sm.major_id = m.id
                LEFT JOIN faculties f ON m.faculty_id = f.id
                LEFT JOIN identity_cards i ON s.id = i.student_id
                LEFT JOIN student_contacts sc ON s.id = sc.student_id
                LEFT JOIN academic_infos ai ON sm.id = ai.student_major_id
                LEFT JOIN emergency_contacts ec ON s.id = ec.student_id
            """, nativeQuery = true)
    Page<StudentRow> findAllStudent(Pageable pageable);

    @Query(value = """
            SELECT
                c.id AS classId,
                c.class_code AS classCode,
                m.major_name AS majorName,
                c.start_year AS startYear,
                l.lecturer_code AS lecturerCode,
                l.full_name AS academicAdvisor,
                l.phone_number AS phoneNumber,
                l.email AS email
            FROM students s
            JOIN student_classes c ON s.student_class_id = c.id
            LEFT JOIN majors m ON c.major_id = m.id
            LEFT JOIN academic_advisors aa ON c.id = aa.student_class_id
            LEFT JOIN lecturers l ON aa.lecturer_id = l.id
            WHERE s.id = :studentId
            """, nativeQuery = true)
    Optional<ClassHeaderView> findClassHeaderByStudentId(@Param("studentId") Long studentId);

    @Query(value = """
            SELECT
                s.student_code AS studentCode,
                s.full_name AS fullName,
                s.date_of_birth AS dateOfBirth,
                sc.phone_number AS phoneNumber,
                sc.email_personal AS email,
                hi.insurance_number AS insuranceNumber,
                hi.provider AS provider,
                hi.status AS status,
                hi.valid_from AS validFrom,
                hi.valid_to AS validTo,
                hi.registered_hospital AS registeredHospital
            FROM students s
            LEFT JOIN student_contacts sc ON s.id = sc.student_id
            LEFT JOIN (
                SELECT *, ROW_NUMBER() OVER (PARTITION BY student_id ORDER BY created_at DESC) AS rn
                FROM health_insurances
            ) hi ON s.id = hi.student_id AND hi.rn = 1
            WHERE s.id = :studentId
            """, nativeQuery = true)
    Optional<HealthInsuranceView> findHealthInsuranceById(@Param("studentId") Long studentId);

    @Query(value = """
            SELECT
                sm.start_year AS startYear,
                sm.end_year AS endYear
            FROM students s
            JOIN student_majors sm ON s.id = sm.student_id AND sm.is_primary = true
            WHERE s.id = :studentId
            """, nativeQuery = true)
    Optional<StudyYearView> findYearStudy(@Param("studentId") Long studentId);

    @Query("SELECT s.studentCode FROM Student s WHERE s.studentCode IN :codes")
    Set<String> findExistingStudentCodes(@Param("codes") Collection<String> codes);
}
