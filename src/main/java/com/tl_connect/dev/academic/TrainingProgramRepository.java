package com.tl_connect.dev.academic;

import java.util.List;
import java.util.Optional;

import com.tl_connect.dev.academic.projection.SubjectPrerequisiteRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.academic.entity.TrainingProgram;
import com.tl_connect.dev.academic.projection.TrainingProgramHeaderView;
import com.tl_connect.dev.academic.projection.TrainingProgramSubjectRow;

@Repository
public interface TrainingProgramRepository extends JpaRepository<TrainingProgram, Long> {

    @Query("""
            SELECT
                tp.id AS id,
                tp.trainingProgramName AS trainingProgramName,
                tp.yearStart AS yearStart,
                tp.totalCredits AS totalCredits,
                m.majorCode AS majorCode,
                m.majorName AS majorName,
                f.facultyName AS faculty
            FROM Student s
            JOIN StudentClass sc ON s.studentClassId = sc.id
            JOIN Major m ON sc.majorId = m.id
            JOIN TrainingProgram tp ON m.id = tp.majorId
            JOIN Faculty f ON m.facultyId = f.id
            WHERE s.id = :studentId
            """)
    Optional<TrainingProgramHeaderView> findTrainingProgramHeaderByStudentId(@Param("studentId") Long studentId);

    @Query("""
            SELECT
                sem.id AS semesterId,
                sem.semesterName AS semesterName,
                sem.startDate AS semesterStartDate,
                sem.endDate AS semesterEndDate,
                sub.id AS subjectId,
                sub.subjectCode AS subjectCode,
                sub.subjectName AS subjectName,
                sub.credits AS credits,
                tps.isRequired AS isRequired,
                tps.electiveGroup AS electiveGroup,
                sub.lectureHours AS lectureHours,
                sub.practiceHours AS practiceHours,
                f.facultyName AS faculty,
                d.departmentName AS department
            FROM TrainingProgramSubject tps
            JOIN Subject sub ON tps.id.subjectId = sub.id
            LEFT JOIN Faculty f ON sub.facultyId = f.id
            LEFT JOIN Department d ON sub.departmentId = d.id
            JOIN Semester sem ON tps.semesterId = sem.id
            WHERE tps.id.programId = :programId
            ORDER BY sem.id, sub.subjectCode
            """)
    Optional<List<TrainingProgramSubjectRow>> findSubjectsByProgramId(@Param("programId") Long programId);

    @Query("""
            SELECT
                tps.id.subjectId AS subjectId,
                sp.id.prerequisiteSubjectId AS prerequisiteSubjectId,
                s.subjectCode AS prerequisiteSubjectCode,
                s.subjectName AS prerequisiteSubjectName
            FROM TrainingProgramSubject tps
            JOIN SubjectPrerequisite sp ON tps.id.subjectId = sp.id.subjectId
            JOIN Subject s ON sp.id.prerequisiteSubjectId = s.id
            WHERE tps.id.programId = :programId
            """)
    Optional<List<SubjectPrerequisiteRow>> findSubjectPrerequisitesByProgramId(@Param("programId") Long programId);
}
