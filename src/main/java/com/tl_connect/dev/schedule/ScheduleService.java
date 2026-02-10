package com.tl_connect.dev.schedule;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.academic.entity.Semester;
import com.tl_connect.dev.common.exception.NotFoundException;
import com.tl_connect.dev.schedule.dto.CourseClassDTO;
import com.tl_connect.dev.schedule.dto.DailyScheduleDTO;
import com.tl_connect.dev.schedule.dto.SemesterScheduleDTO;
import com.tl_connect.dev.schedule.dto.WeeklyScheduleDTO;
import com.tl_connect.dev.schedule.projection.ScheduleRow;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
        private final ScheduleRepository scheduleRepository;
        private final SemesterRepository semesterRepository;

        public WeeklyScheduleDTO getWeeklySchedule(Long studentId, LocalDate startDate, LocalDate endDate) {

                Semester semester = semesterRepository.findSemesterByDate(startDate)
                                .orElseThrow(() -> new NotFoundException("Semester not found"));

                List<ScheduleRow> scheduleRows = scheduleRepository.findScheduleByStudentId(studentId, semester.getId())
                                .orElse(Collections.emptyList());

                Map<Integer, List<ScheduleRow>> groupedByDayOfWeek = scheduleRows.stream()
                                .collect(Collectors.groupingBy(ScheduleRow::getDayOfWeek));

                List<DailyScheduleDTO> dailySchedules = groupedByDayOfWeek.entrySet().stream()
                                .map(entry -> {
                                        int dayOfWeek = entry.getKey();
                                        List<ScheduleRow> rows = entry.getValue();

                                        List<CourseClassDTO> courseClasses = rows.stream()
                                                        .map(row -> CourseClassDTO.builder()
                                                                        .classCode(row.getClassCode())
                                                                        .dayOfWeek(dayOfWeek)
                                                                        .subjectName(row.getSubjectName())
                                                                        .subjectCode(row.getSubjectCode())
                                                                        .startPeriod(row.getStartPeriod())
                                                                        .endPeriod(row.getEndPeriod())
                                                                        .startTime(row.getStartTime())
                                                                        .endTime(row.getEndTime())
                                                                        .room(row.getRoom())
                                                                        .lecturerName(row.getLecturerName())
                                                                        .lecturerEmail(row.getLecturerEmail())
                                                                        .build())
                                                        .collect(Collectors.toList());

                                        return DailyScheduleDTO.builder()
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
                Semester semester = semesterRepository.findSemesterByName(semesterName).orElse(null);

                List<ScheduleRow> scheduleRows = scheduleRepository.findScheduleByStudentId(studentId, semester.getId())
                                .orElse(null);

                List<CourseClassDTO> courseClasses = scheduleRows.stream()
                                .map(row -> CourseClassDTO.builder()
                                                .classCode(row.getClassCode())
                                                .dayOfWeek(row.getDayOfWeek())
                                                .subjectName(row.getSubjectName())
                                                .subjectCode(row.getSubjectCode())
                                                .startPeriod(row.getStartPeriod())
                                                .endPeriod(row.getEndPeriod())
                                                .startTime(row.getStartTime())
                                                .endTime(row.getEndTime())
                                                .room(row.getRoom())
                                                .lecturerName(row.getLecturerName())
                                                .lecturerEmail(row.getLecturerEmail())
                                                .build())
                                .collect(Collectors.toList());

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

}
