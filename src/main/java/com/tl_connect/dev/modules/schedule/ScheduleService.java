package com.tl_connect.dev.modules.schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.core.common.exception.BadRequestException;
import com.tl_connect.dev.core.common.exception.ConflictException;
import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.course_class.CourseClass;
import com.tl_connect.dev.modules.course_class.CourseClassRepository;
import com.tl_connect.dev.modules.lecturer.dto.LecturerDTO;
import com.tl_connect.dev.modules.schedule.dto.ClassScheduleDTO;
import com.tl_connect.dev.modules.schedule.dto.CourseClassDTO;
import com.tl_connect.dev.modules.schedule.dto.DayOfWeekScheduleDTO;
import com.tl_connect.dev.modules.schedule.dto.SemesterScheduleDTO;
import com.tl_connect.dev.modules.schedule.dto.UpdateScheduleDTO;
import com.tl_connect.dev.modules.schedule.dto.WeeklyScheduleDTO;
import com.tl_connect.dev.modules.schedule.entity.ClassSchedule;
import com.tl_connect.dev.modules.schedule.projection.ScheduleRow;
import com.tl_connect.dev.modules.semester.Semester;
import com.tl_connect.dev.modules.semester.SemesterRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ScheduleService {
        private final ScheduleRepository scheduleRepository;
        private final SemesterRepository semesterRepository;
        private final CourseClassRepository courseClassRepository;
        private final Validator validator;

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

        public List<ClassScheduleDTO> getAllClassSchedules(Long courseClassId) {
                List<ClassSchedule> schedules = scheduleRepository.findByCourseClassId(courseClassId);
                return schedules.stream()
                                .map(this::toDTO)
                                .collect(Collectors.toList());
        }

        @Transactional
        public void createClassSchedule(Long courseClassId, List<ClassScheduleDTO> newSchedules) {

                Set<ConstraintViolation<List<ClassScheduleDTO>>> violations = validator.validate(newSchedules);
                if (!violations.isEmpty()) {
                        String message = violations.stream()
                                        .map(ConstraintViolation::getMessage)
                                        .collect(Collectors.joining(", "));
                        throw new InvalidInputException(message);
                }

                CourseClass courseClass = courseClassRepository.findById(courseClassId)
                                .orElseThrow(() -> new NotFoundException("Course class not found"));


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
                Set<ConstraintViolation<UpdateScheduleDTO>> violations = validator.validate(dto);
                if (!violations.isEmpty()) {
                        String message = violations.stream()
                                        .map(ConstraintViolation::getMessage)
                                        .collect(Collectors.joining(", "));
                        throw new InvalidInputException(message);
                }

                ClassSchedule classSchedule = scheduleRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException("Schedule not found"));

                 Long courseClassId = classSchedule.getCourseClassId();

                CourseClass courseClass = courseClassRepository.findById(courseClassId)
                        .orElseThrow(() -> new NotFoundException("Course class not found"));

                List<ClassSchedule> dbList = scheduleRepository.findForConflict(
                        Set.of(dto.getDayOfWeek()),
                        courseClassId,
                        courseClass.getSemesterId()
                );

                int dayOfWeek = dto.getDayOfWeek() != null ? dto.getDayOfWeek() : classSchedule.getDayOfWeek();
                int startPeriod = dto.getStartPeriod() != null ? dto.getStartPeriod() : classSchedule.getStartPeriod();
                int endPeriod = dto.getEndPeriod() != null ? dto.getEndPeriod() : classSchedule.getEndPeriod();
                LocalTime startTime = dto.getStartTime() != null ? dto.getStartTime() : classSchedule.getStartTime();
                LocalTime endTime = dto.getEndTime() != null ? dto.getEndTime() : classSchedule.getEndTime();
                String room = dto.getRoom() != null ? dto.getRoom() : classSchedule.getRoom();

                if (!startTime.isBefore(endTime) || !(startPeriod <= endPeriod)) {
                    throw new BadRequestException("Start time must be before end time");
                }
                
                for (ClassSchedule db : dbList) {

                        if (db.getId().equals(id)) continue;

                        if (db.getCourseClassId().equals(courseClassId)
                                && startPeriod < db.getEndPeriod() && endPeriod > db.getStartPeriod()) {

                                throw new ConflictException("Class schedule conflict");
                        }

                        if (!db.getCourseClassId().equals(courseClassId)
                                && room.equals(db.getRoom())
                                && startPeriod < db.getEndPeriod() && endPeriod > db.getStartPeriod()) {

                                throw new ConflictException("Room is occupied");
                        }
                }

                classSchedule.setDayOfWeek(dayOfWeek);
                classSchedule.setStartPeriod(startPeriod);
                classSchedule.setEndPeriod(endPeriod);
                classSchedule.setStartTime(startTime);
                classSchedule.setEndTime(endTime);
                classSchedule.setRoom(room);

                try {
                        scheduleRepository.save(classSchedule);
                } catch (DataIntegrityViolationException e) {
                        throw new BadRequestException("Class schedule conflict");
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

        private ClassScheduleDTO toDTO(ClassSchedule schedule) {
                return ClassScheduleDTO.builder()
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
                return ClassSchedule.builder()
                                .courseClassId(courseClassId)
                                .dayOfWeek(dto.getDayOfWeek())
                                .startPeriod(dto.getStartPeriod())
                                .endPeriod(dto.getEndPeriod())
                                .startTime(dto.getStartTime())
                                .endTime(dto.getEndTime())
                                .room(dto.getRoom())
                                .build();
        }
}
