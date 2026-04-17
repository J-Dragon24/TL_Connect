package com.tl_connect.dev.modules.schedule.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tl_connect.dev.core.common.exception.InvalidInputException;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "class_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_class_id", nullable = false)
    private Long courseClassId;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    @Column(name = "start_period", nullable = false)
    private Integer startPeriod;

    @Column(name = "end_period", nullable = false)
    private Integer endPeriod;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "room")
    private String room;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static ClassSchedule create(Long courseClassId, Integer dayOfWeek, Integer startPeriod, Integer endPeriod, LocalTime startTime, LocalTime endTime, String room) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new InvalidInputException("Day of week must be between 1 and 7");
        }
        if (startPeriod < 1 || startPeriod > 15) {
            throw new InvalidInputException("Start period must be between 1 and 15");
        }
        if (endPeriod < 1 || endPeriod > 15) {
            throw new InvalidInputException("End period must be between 1 and 15");
        }
        if (startPeriod > endPeriod) {
            throw new InvalidInputException("Start period must be less than or equal to end period");
        }
        if (startTime.isAfter(endTime)) {
            throw new InvalidInputException("Start time must be before end time");
        }
        if(startPeriod > endPeriod) {
            throw new InvalidInputException("Start period must be less than or equal to end period");
        }
        return ClassSchedule.builder()
                .courseClassId(courseClassId)
                .dayOfWeek(dayOfWeek)
                .startPeriod(startPeriod)
                .endPeriod(endPeriod)
                .startTime(startTime)
                .endTime(endTime)
                .room(room)
                .build();
    }

    public void update(Integer dayOfWeek, Integer startPeriod, Integer endPeriod, LocalTime startTime, LocalTime endTime, String room) {
        LocalTime newStartTime = startTime != null ? startTime : this.startTime;
        LocalTime newEndTime = endTime != null ? endTime : this.endTime;
        Integer newStartPeriod = startPeriod != null ? startPeriod : this.startPeriod;
        Integer newEndPeriod = endPeriod != null ? endPeriod : this.endPeriod;
        if (dayOfWeek != null) {
            if (dayOfWeek < 1 || dayOfWeek > 7) {
                throw new InvalidInputException("Day of week must be between 1 and 7");
            }
            this.dayOfWeek = dayOfWeek;
        }
        if(newStartPeriod > newEndPeriod) {
            throw new InvalidInputException("Start period must be less than or equal to end period");
        }
        if(newStartTime.isAfter(newEndTime)) {
            throw new InvalidInputException("Start time must be before end time");
        }
        if (startPeriod != null) {
            if (startPeriod < 1 || startPeriod > 15) {
                throw new InvalidInputException("Start period must be between 1 and 15");
            }
            this.startPeriod = startPeriod;
        }
        if (endPeriod != null) {
            if (endPeriod < 1 || endPeriod > 15) {
                throw new InvalidInputException("End period must be between 1 and 15");
            }
            this.endPeriod = endPeriod;
        }
        if (startTime != null) {
            this.startTime = startTime;
        }
        if (endTime != null) {
            this.endTime = endTime;
        }
        if (room != null) {
            this.room = room;
        }
    }
}
