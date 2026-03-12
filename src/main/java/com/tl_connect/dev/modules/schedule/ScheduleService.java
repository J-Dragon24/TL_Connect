package com.tl_connect.dev.modules.schedule;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.schedule.dto.CourseClassDTO;
import com.tl_connect.dev.modules.schedule.dto.DayOfWeekScheduleDTO;
import com.tl_connect.dev.modules.schedule.dto.SemesterScheduleDTO;
import com.tl_connect.dev.modules.schedule.dto.WeeklyScheduleDTO;
import com.tl_connect.dev.modules.schedule.projection.ScheduleRow;
import com.tl_connect.dev.modules.semester.Semester;
import com.tl_connect.dev.modules.semester.SemesterRepository;
import com.tl_connect.dev.modules.student_class.dto.LecturerDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
        private final ScheduleRepository scheduleRepository;
        private final SemesterRepository semesterRepository;

        public WeeklyScheduleDTO getWeeklySchedule(Long studentId, LocalDate startDate, LocalDate endDate) {

                Semester semester = semesterRepository.findSemesterByDate(startDate)
                                .orElseThrow(() -> new NotFoundException("Semester not found"));

                List<ScheduleRow> scheduleRows = scheduleRepository.findScheduleByStudentId(studentId,
                                semester.getId());

                Map<Integer, List<ScheduleRow>> groupedByDayOfWeek = scheduleRows.stream()
                                .collect(Collectors.groupingBy(ScheduleRow::getDayOfWeek));

                List<DayOfWeekScheduleDTO> dailySchedules = groupedByDayOfWeek.entrySet().stream()
                                .map(entry -> {
                                        int dayOfWeek = entry.getKey();
                                        List<ScheduleRow> rows = entry.getValue();

                                        List<CourseClassDTO> courseClasses = rows.stream()
                                                        .map(row -> {
                                                                LecturerDTO lecturer = LecturerDTO.builder()
                                                                                .fullName(row.getLecturerName())
                                                                                .email(row.getLecturerEmail())
                                                                                .phoneNumber(row.getLecturerPhone())
                                                                                .lecturerCode(row.getLecturerCode())
                                                                                .build();
                                                                return CourseClassDTO.builder()
                                                                                .classCode(row.getClassCode())
                                                                                .dayOfWeek(dayOfWeek)
                                                                                .subjectName(row.getSubjectName())
                                                                                .subjectCode(row.getSubjectCode())
                                                                                .startPeriod(row.getStartPeriod())
                                                                                .endPeriod(row.getEndPeriod())
                                                                                .startTime(row.getStartTime())
                                                                                .endTime(row.getEndTime())
                                                                                .room(row.getRoom())
                                                                                .lecturer(lecturer)
                                                                                .build();
                                                        }).collect(Collectors.toList());

                                        return DayOfWeekScheduleDTO.builder()
                                                        .courseClasses(courseClasses)
                                                        .build();
                                })
                                .collect(Collectors.toList());

                return WeeklyScheduleDTO.builder()
                                .semester(semester.getSemesterName())
                                .week(getWeekOfSemester(semester.getStartDate(), startDate))
                                .startDate(startDate)
                                .endDate(endDate)
                                .dailySchedules(dailySchedules)
                                .build();
        }

        public SemesterScheduleDTO getSemesterSchedule(Long studentId, String semesterName) {
                Semester semester = semesterRepository.findSemesterByName(semesterName)
                                .orElseThrow(() -> new NotFoundException("Semester not found"));

                List<ScheduleRow> scheduleRows = scheduleRepository.findScheduleByStudentId(studentId,
                                semester.getId());

                List<CourseClassDTO> courseClasses = scheduleRows.stream()
                                .map(row -> {
                                        LecturerDTO lecturer = LecturerDTO.builder()
                                                        .fullName(row.getLecturerName())
                                                        .email(row.getLecturerEmail())
                                                        .phoneNumber(row.getLecturerPhone())
                                                        .lecturerCode(row.getLecturerCode())
                                                        .build();
                                        return CourseClassDTO.builder()
                                                        .classCode(row.getClassCode())
                                                        .dayOfWeek(row.getDayOfWeek())
                                                        .subjectName(row.getSubjectName())
                                                        .subjectCode(row.getSubjectCode())
                                                        .startPeriod(row.getStartPeriod())
                                                        .endPeriod(row.getEndPeriod())
                                                        .startTime(row.getStartTime())
                                                        .endTime(row.getEndTime())
                                                        .room(row.getRoom())
                                                        .lecturer(lecturer)
                                                        .build();
                                }).collect(Collectors.toList());

                return SemesterScheduleDTO.builder()
                                .semester(semester.getSemesterName())
                                .courseClasses(courseClasses)
                                .build();
        }

        private int getWeekOfSemester(LocalDate semesterStartDate, LocalDate date) {
                if (date.isBefore(semesterStartDate)) {
                        throw new IllegalArgumentException("Date is before semester start date");
                }

                long daysBetween = ChronoUnit.DAYS.between(semesterStartDate, date);
                return (int) (daysBetween / 7) + 1;
        }

        public DayOfWeekScheduleDTO getDayOfWeekSchedule(Long studentId, int dayOfWeek) {

                LocalDate today = LocalDate.now();
                Semester semester = semesterRepository.findSemesterByDate(today)
                                .orElseThrow(() -> new NotFoundException("Semester not found"));

                List<ScheduleRow> scheduleRows = scheduleRepository.findDayOfWeekSchedule(studentId, semester.getId(),
                                dayOfWeek);

                List<CourseClassDTO> courseClasses = scheduleRows.stream()
                                .map(row -> {
                                        LecturerDTO lecturer = LecturerDTO.builder()
                                                        .fullName(row.getLecturerName())
                                                        .email(row.getLecturerEmail())
                                                        .phoneNumber(row.getLecturerPhone())
                                                        .lecturerCode(row.getLecturerCode())
                                                        .build();
                                        return CourseClassDTO.builder()
                                                        .classCode(row.getClassCode())
                                                        .dayOfWeek(row.getDayOfWeek())
                                                        .subjectName(row.getSubjectName())
                                                        .subjectCode(row.getSubjectCode())
                                                        .startPeriod(row.getStartPeriod())
                                                        .endPeriod(row.getEndPeriod())
                                                        .startTime(row.getStartTime())
                                                        .endTime(row.getEndTime())
                                                        .room(row.getRoom())
                                                        .lecturer(lecturer)
                                                        .build();
                                }).collect(Collectors.toList());

                return DayOfWeekScheduleDTO.builder()
                                .courseClasses(courseClasses)
                                .build();
        }
}
