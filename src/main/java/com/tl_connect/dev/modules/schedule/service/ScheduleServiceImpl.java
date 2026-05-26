package com.tl_connect.dev.modules.schedule.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tl_connect.dev.modules.course_class.CourseClass;
import com.tl_connect.dev.modules.course_class.service.interfaces.CourseClassService;
import com.tl_connect.dev.modules.lecturer.dto.LecturerDTO;
import com.tl_connect.dev.modules.schedule.ScheduleRepository;
import com.tl_connect.dev.modules.schedule.dto.ClassScheduleAdminDTO;
import com.tl_connect.dev.modules.schedule.dto.ClassScheduleDTO;
import com.tl_connect.dev.modules.schedule.dto.ScheduleCourseClassDTO;
import com.tl_connect.dev.modules.schedule.dto.DayOfWeekScheduleDTO;
import com.tl_connect.dev.modules.schedule.dto.SemesterScheduleDTO;
import com.tl_connect.dev.modules.schedule.dto.UpdateScheduleDTO;
import com.tl_connect.dev.modules.schedule.dto.WeeklyScheduleDTO;
import com.tl_connect.dev.modules.schedule.entity.ClassSchedule;
import com.tl_connect.dev.modules.schedule.projection.ScheduleRow;
import com.tl_connect.dev.modules.schedule.service.interfaces.ScheduleService;
import com.tl_connect.dev.modules.semester.Semester;
import com.tl_connect.dev.modules.semester.service.interfaces.SemesterService;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ConflictException;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.shared.common.ultility.CacheHelper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService{
        private final ScheduleRepository scheduleRepository;
        private final SemesterService semesterService;
        private final CourseClassService courseClassService;
        private final CacheHelper cacheHelper;

        public WeeklyScheduleDTO getWeeklySchedule(Long studentId, LocalDate startDate, LocalDate endDate) {

                Semester semester = semesterService.findByDate(startDate);

                LocalDate today = LocalDate.now();
                boolean isCurrentSemester = !today.isBefore(semester.getStartDate()) 
                    && !today.isAfter(semester.getEndDate());

                if (isCurrentSemester) {
                    String key = "schedule:student:" + studentId + ":" + semester.getId();
                    Duration ttl = Duration.between(today.atStartOfDay(), semester.getEndDate().atTime(23, 59, 59));

                    WeeklyScheduleDTO result = cacheHelper.getOrSet(key, ttl, new TypeReference<WeeklyScheduleDTO>() {}, () -> {
                        return getWeeklyScheduleFromDb(studentId, startDate, endDate, semester);
                    });
                    
                    return result;
                }

                return getWeeklyScheduleFromDb(studentId, startDate, endDate, semester);
        }

        private WeeklyScheduleDTO getWeeklyScheduleFromDb(Long studentId, LocalDate startDate, LocalDate endDate, Semester semester) {
                List<ScheduleRow> scheduleRows = scheduleRepository.findScheduleByStudentId(studentId,
                                semester.getId());

                Map<Integer, List<ScheduleRow>> groupedByDayOfWeek = scheduleRows.stream()
                                .collect(Collectors.groupingBy(ScheduleRow::getDayOfWeek));

                List<DayOfWeekScheduleDTO> dailySchedules = groupedByDayOfWeek.entrySet().stream()
                                .map(entry -> {
                                        int dayOfWeek = entry.getKey();
                                        List<ScheduleRow> rows = entry.getValue();

                                        List<ScheduleCourseClassDTO> courseClasses = rows.stream()
                                                        .map(row -> {
                                                                LecturerDTO lecturer = LecturerDTO.builder()
                                                                                .fullName(row.getLecturerName())
                                                                                .email(row.getLecturerEmail())
                                                                                .phoneNumber(row.getLecturerPhone())
                                                                                .lecturerCode(row.getLecturerCode())
                                                                                .build();
                                                                return ScheduleCourseClassDTO.builder()
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
                                                        .dayOfWeek(dayOfWeek)
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

        public SemesterScheduleDTO getSemesterSchedule(Long studentId, String semesterCode) {
                Semester semester = semesterService.findBySemesterCode(semesterCode);

                List<ScheduleRow> scheduleRows = scheduleRepository.findScheduleByStudentId(studentId,
                                semester.getId());

                List<ScheduleCourseClassDTO> courseClasses = scheduleRows.stream()
                                .map(row -> {
                                        LecturerDTO lecturer = LecturerDTO.builder()
                                                        .fullName(row.getLecturerName())
                                                        .email(row.getLecturerEmail())
                                                        .phoneNumber(row.getLecturerPhone())
                                                        .lecturerCode(row.getLecturerCode())
                                                        .build();
                                        return ScheduleCourseClassDTO.builder()
                                                        .classCode(row.getClassCode())
                                                        .dayOfWeek(row.getDayOfWeek())
                                                        .subjectName(row.getSubjectName())
                                                        .subjectCode(row.getSubjectCode())
                                                        .startPeriod(row.getStartPeriod())
                                                        .endPeriod(row.getEndPeriod())
                                                        .credits(row.getCredits())
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
                        throw new InvalidInputException("Date is before semester start date");
                }

                long daysBetween = ChronoUnit.DAYS.between(semesterStartDate, date);
                return (int) (daysBetween / 7) + 1;
        }

        public DayOfWeekScheduleDTO getDayOfWeekSchedule(Long studentId, int dayOfWeek) {

                LocalDate today = LocalDate.now();

                WeeklyScheduleDTO weeklySchedule = getWeeklySchedule(studentId, today, today);

                List<DayOfWeekScheduleDTO> dailySchedules = weeklySchedule.getDailySchedules();

                DayOfWeekScheduleDTO dayOfWeekSchedule = dailySchedules.stream()
                                .filter(dailySchedule -> dailySchedule.getDayOfWeek() == dayOfWeek)
                                .findFirst()
                                .orElse(DayOfWeekScheduleDTO.builder()
                                                .dayOfWeek(dayOfWeek)
                                                .courseClasses(new ArrayList<>())
                                                .build());

                return dayOfWeekSchedule;
        }

        public List<ClassScheduleAdminDTO> getAllClassSchedules(Long courseClassId) {
                List<ClassSchedule> schedules = scheduleRepository.findByCourseClassId(courseClassId);
                return schedules.stream()
                                .map(this::toDTO)
                                .collect(Collectors.toList());
        }

        @Transactional
        public void createClassSchedule(Long courseClassId, List<ClassScheduleDTO> newSchedules) {

                CourseClass courseClass = courseClassService.findById(courseClassId);


                for (int i = 0; i < newSchedules.size(); i++) {
                                for (int j = i + 1; j < newSchedules.size(); j++) {
                                ClassScheduleDTO a = newSchedules.get(i);
                                ClassScheduleDTO b = newSchedules.get(j);

                                if (sameDay(a, b) && overlap(a, b)) {
                                        throw new ConflictException("Time conflict in request");
                                }
                        }
                }

                Set<Integer> days = newSchedules.stream()
                        .map(ClassScheduleDTO::getDayOfWeek)
                        .collect(Collectors.toSet());

                List<ClassSchedule> dbList = scheduleRepository.findForConflict(days, courseClassId, courseClass.getSemesterId());

                Map<Integer, List<ClassSchedule>> dbMap = dbList.stream()
                        .collect(Collectors.groupingBy(ClassSchedule::getDayOfWeek));


                for (ClassScheduleDTO dto : newSchedules) {

                        List<ClassSchedule> sameDayList = dbMap.getOrDefault(dto.getDayOfWeek(), List.of());

                        for (ClassSchedule db : sameDayList) {

                                // ===== RULE 1: cùng class, trùng giờ =====
                                if (db.getCourseClassId().equals(courseClassId)
                                        && overlap(dto, db)) {

                                        throw new ConflictException("Class schedule conflict");
                                }

                                // ===== RULE 2: khác class, trùng phòng =====
                                if (!db.getCourseClassId().equals(courseClassId)
                                        && dto.getRoom().equals(db.getRoom())
                                        && overlap(dto, db)) {

                                        throw new ConflictException("Room is occupied");
                                }
                        }
                }

                // ===== 4. SAVE =====
                List<ClassSchedule> entities = newSchedules.stream()
                        .map(dto -> mapToEntity(courseClassId, dto))
                        .toList();

                try{
                        scheduleRepository.saveAll(entities);
                }catch(DataIntegrityViolationException e){
                        throw new ConflictException("Schedule conflict");
                }
        }

        @Transactional
        public void updateClassSchedule(Long id, UpdateScheduleDTO dto) {

                ClassSchedule classSchedule = scheduleRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException("Schedule not found"));

                Long courseClassId = classSchedule.getCourseClassId();

                CourseClass courseClass = courseClassService.findById(courseClassId);

                classSchedule.update(dto.getDayOfWeek(), dto.getStartPeriod(), dto.getEndPeriod(), dto.getStartTime(), dto.getEndTime(), dto.getRoom());

                List<ClassSchedule> dbList = scheduleRepository.findForConflict(
                        Set.of(classSchedule.getDayOfWeek()),
                        courseClassId,
                        courseClass.getSemesterId()
                );

                if (!classSchedule.getStartTime().isBefore(classSchedule.getEndTime()) || !(classSchedule.getStartPeriod() <= classSchedule.getEndPeriod())) {
                    throw new InvalidInputException("Start time must be before end time");
                }
                
                for (ClassSchedule db : dbList) {

                        if (db.getId().equals(id)) continue;

                        if (db.getCourseClassId().equals(courseClassId)
                                && classSchedule.getStartPeriod() < db.getEndPeriod() && classSchedule.getEndPeriod() > db.getStartPeriod()) {
                                throw new ConflictException("Class schedule conflict");
                        }

                        if (!db.getCourseClassId().equals(courseClassId)
                                && classSchedule.getRoom().equals(db.getRoom())
                                && classSchedule.getStartPeriod() < db.getEndPeriod() && classSchedule.getEndPeriod() > db.getStartPeriod()) {

                                throw new ConflictException("Room is occupied");
                        }
                }
                try {
                        scheduleRepository.save(classSchedule);
                } catch (DataIntegrityViolationException e) {
                        throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Update schedule failed");
                }
        }

        @Transactional
        public void deleteClassSchedule(Long id) {
                ClassSchedule classSchedule = scheduleRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException("Schedule not found"));
                scheduleRepository.delete(classSchedule);
        }

        private boolean sameDay(ClassScheduleDTO a, ClassScheduleDTO b) {
            return a.getDayOfWeek().equals(b.getDayOfWeek());
        }

        private boolean overlap(ClassScheduleDTO a, ClassScheduleDTO b) {
            return a.getStartPeriod() < b.getEndPeriod() && a.getEndPeriod() > b.getStartPeriod();
        }

        private boolean overlap(ClassScheduleDTO a, ClassSchedule b) {
            return a.getStartPeriod() < b.getEndPeriod() && a.getEndPeriod() > b.getStartPeriod();
        }

        private ClassScheduleAdminDTO toDTO(ClassSchedule schedule) {
                return ClassScheduleAdminDTO.builder()
                                .id(schedule.getId())
                                .dayOfWeek(schedule.getDayOfWeek())
                                .startPeriod(schedule.getStartPeriod())
                                .endPeriod(schedule.getEndPeriod())
                                .startTime(schedule.getStartTime())
                                .endTime(schedule.getEndTime())
                                .room(schedule.getRoom())
                                .build();
        }

        private ClassSchedule mapToEntity(Long courseClassId, ClassScheduleDTO dto) {
                return ClassSchedule.create(courseClassId, dto.getDayOfWeek(), dto.getStartPeriod(), dto.getEndPeriod(), dto.getStartTime(), dto.getEndTime(), dto.getRoom());
        }
}
