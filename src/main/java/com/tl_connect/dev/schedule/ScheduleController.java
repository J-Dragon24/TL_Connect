package com.tl_connect.dev.schedule;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.common.dto.ResponseHelper;
import com.tl_connect.dev.common.exception.InvalidInputException;
import com.tl_connect.dev.common.types.JwtUserInfo;
import com.tl_connect.dev.schedule.dto.SemesterScheduleDTO;
import com.tl_connect.dev.schedule.dto.WeeklyScheduleDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/student/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping("/weekly")
    public ResponseEntity<?> getWeeklySchedule(JwtUserInfo userInfo, @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate) {
        try {
            Long studentId = userInfo.userId();
            if (studentId == null) {
                throw new InvalidInputException("Student ID is required");
            }
            LocalDate start = null;
            LocalDate end = null;
            if(startDate == null || endDate == null) {
                start = LocalDate.now();
                end = LocalDate.now().plusDays(7);
            }
            else {
                try{
                    start = LocalDate.parse(startDate);
                    end = LocalDate.parse(endDate);
                }catch(Exception e){
                    throw new InvalidInputException("Invalid date format");
                }
            }
            WeeklyScheduleDTO weeklySchedule = scheduleService.getWeeklySchedule(studentId, start, end);
            return ResponseHelper.success("Weekly schedule retrieved successfully", weeklySchedule);
        } catch (Exception e) {
            return ResponseHelper.internalError(e.getMessage());
        }
    }

    @GetMapping("/semester")
    public ResponseEntity<?> getSemesterSchedule(JwtUserInfo userInfo, @RequestParam String semesterName) {
        try {
            Long studentId = userInfo.userId();
            if (studentId == null) {
                throw new InvalidInputException("Student ID is required");
            }
            SemesterScheduleDTO semesterSchedule = scheduleService.getSemesterSchedule(studentId, semesterName);
            return ResponseHelper.success("Semester schedule retrieved successfully", semesterSchedule);
        } catch (Exception e) {
            return ResponseHelper.internalError(e.getMessage());
        }
    }
}
