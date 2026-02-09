package com.tl_connect.dev.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.student.entity.Student;
import com.tl_connect.dev.student.projection.StudentInfoView;
import com.tl_connect.dev.student_class.projection.ClassHeaderView;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("""
            SELECT
                s.studentCode AS studentCode,
                s.fullName AS fullName,
                s.gender AS gender,
                s.dateOfBirth AS dateOfBirth,
                c.classCode AS classCode,
                l.fullName AS academicAdvisor,
                m.majorCode AS majorCode,
                m.majorName AS majorName,
                f.facultyName AS faculty,
                i.cardNumber AS idCardNumber,
                i.cardType AS idCardType,
                i.issuedDate AS issuedDate,
                i.issuedPlace AS issuedPlace,
                sc.phoneNumber AS phoneNumber,
                sc.address AS adress,
                sc.emailPersonal AS email,
                ai.cohort AS cohort,
                ai.position AS position,
                ai.educationMode AS educationMode,
                ec.fullName AS emergencyContactName,
                ec.phoneNumber AS emergencyContactPhoneNumber,
                ec.address AS emergencyContactAdress
            FROM Student s
            LEFT JOIN StudentClass c ON s.studentClassId = c.id
            LEFT JOIN Major m ON c.majorId = m.id
            LEFT JOIN Faculty f ON m.facultyId = f.id
            LEFT JOIN IdentityCard i ON s.id = i.studentId
            LEFT JOIN StudentContact sc ON s.id = sc.studentId
            LEFT JOIN AcademicInfo ai ON s.id = ai.studentId
            LEFT JOIN EmergencyContact ec ON s.id = ec.studentId
            LEFT JOIN AcademicAdvisor aa ON c.id = aa.studentClassId
            LEFT JOIN Lecturer l ON aa.lecturerId = l.id
            WHERE s.id = :id
            """)
    StudentInfoView findStudentInfoById(@Param("id") Long id);


    @Query("""
            SELECT
                c.id AS classId,
                c.classCode AS classCode,
                l.lecturerCode AS lecturerCode,
                l.fullName AS academicAdvisor,
                m.majorName AS major,
                l.phoneNumber AS phoneNumber,
                l.email AS email
            FROM Student s
            JOIN StudentClass c On s.studentClassId = c.id
            JOIN Major m ON c.majorId = m.id
            JOIN AcademicAdvisor aa ON c.id = aa.studentClassId
            JOIN Lecturer l ON aa.lecturerId = l.id
            WHERE s.id = :studentId
            """)
    ClassHeaderView findClassHeaderById(@Param("studentId") Long studentId);
}
