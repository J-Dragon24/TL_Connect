package com.tl_connect.dev.student;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.student.entity.Student;
import com.tl_connect.dev.student.projection.StudentInfoView;
import com.tl_connect.dev.student.projection.HealthInsuranceView;
import com.tl_connect.dev.student_class.projection.ClassHeaderView;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query(value = """
            SELECT
                s.student_code AS studentCode,
                s.full_name AS fullName,
                s.gender AS gender,
                s.date_of_birth AS dateOfBirth,
                c.class_code AS classCode,
                l.full_name AS academicAdvisor,
                m.major_code AS majorCode,
                m.major_name AS majorName,
                f.faculty_name AS faculty,
                i.card_number AS idCardNumber,
                i.card_type AS idCardType,
                i.issued_date AS issuedDate,
                i.issued_place AS issuedPlace,
                sc.phone_number AS phoneNumber,
                sc.address AS adress,
                sc.email_personal AS email,
                ai.cohort AS cohort,
                ai.position AS position,
                ai.education_mode AS educationMode,
                ec.full_name AS emergencyContactName,
                ec.phone_number AS emergencyContactPhoneNumber,
                ec.address AS emergencyContactAdress
            FROM students s
            LEFT JOIN student_classes c ON s.student_class_id = c.id
            LEFT JOIN majors m ON c.major_id = m.id
            LEFT JOIN faculties f ON m.faculty_id = f.id
            LEFT JOIN identity_cards i ON s.id = i.student_id
            LEFT JOIN student_contacts sc ON s.id = sc.student_id
            LEFT JOIN academic_infos ai ON s.id = ai.student_id
            LEFT JOIN emergency_contacts ec ON s.id = ec.student_id
            LEFT JOIN academic_advisors aa ON c.id = aa.student_class_id
            LEFT JOIN lecturers l ON aa.lecturer_id = l.id
            WHERE s.id = :id
            """, nativeQuery = true)
    Optional<StudentInfoView> findStudentInfoById(@Param("id") Long id);


    @Query(value = """
            SELECT
                c.id AS classId,
                c.class_code AS classCode,
                l.lecturer_code AS lecturerCode,
                l.full_name AS academicAdvisor,
                m.major_name AS major,
                l.phone_number AS phoneNumber,
                l.email AS email
            FROM students s
            JOIN student_classes c ON s.student_class_id = c.id
            JOIN majors m ON c.major_id = m.id
            JOIN academic_advisors aa ON c.id = aa.student_class_id
            JOIN lecturers l ON aa.lecturer_id = l.id
            WHERE s.id = :studentId
            """, nativeQuery = true)
    Optional<ClassHeaderView> findClassHeaderById(@Param("studentId") Long studentId);

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
            LEFT JOIN health_insurances hi ON s.id = hi.student_id
            WHERE s.id = :studentId
            """, nativeQuery = true)
    Optional<HealthInsuranceView> findHealthInsuranceById(@Param("studentId") Long studentId);
}
