package com.tl_connect.dev.modules.enroll.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.enroll.entity.StudentCourseClass;
import com.tl_connect.dev.modules.enroll.repository.StudentCourseClassRepository;
import com.tl_connect.dev.modules.enroll.service.interfaces.StudentCourseClassService;
import com.tl_connect.dev.shared.common.enums.StudentCourseClassStatus;
import com.tl_connect.dev.shared.datastructure.intervaltree.ScheduleInterval;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentCourseClassServiceImpl implements StudentCourseClassService {
    
    private final StudentCourseClassRepository studentCourseClassRepository;

    public List<ScheduleInterval> findCurrentSchedule(Long studentId, Long semesterId){
        return studentCourseClassRepository.findCurrentSchedule(studentId, semesterId)
            .stream()
            .map(ScheduleInterval::from)
            .toList();
    }

    public boolean isSubjectAllowedForEnrollment(Long studentId, Long courseClassId){
        return studentCourseClassRepository.isSubjectAllowed(studentId, courseClassId);
    }

    public Optional<StudentCourseClass> findByStudentIdAndCourseClassId(Long studentId, Long courseClassId){
        return studentCourseClassRepository.findByStudentIdAndCourseClassId(studentId, courseClassId);
    }

    public boolean existsByStudentIdAndSubjectIdAndSemesterIdAndStatusIn(
        Long studentId,
        Long subjectId,
        Long semesterId,
        Set<StudentCourseClassStatus> statusSet
    ){
        return studentCourseClassRepository.existsByStudentIdAndSubjectIdAndSemesterIdAndStatusIn(studentId, subjectId, semesterId, statusSet);
    }

    public StudentCourseClass save(StudentCourseClass entity){
        return studentCourseClassRepository.save(entity);
    }

    public void delete(StudentCourseClass entity){
        studentCourseClassRepository.delete(entity);
    }
    
    public Integer findCreditsRegistered(Long studentId, Long semesterId){
        return studentCourseClassRepository.findCreditsRegistered(studentId, semesterId);
    }

    public List<StudentCourseClass> findByCourseClassId(Long courseClassId){
        return studentCourseClassRepository.findByCourseClassId(courseClassId);
    }
}
