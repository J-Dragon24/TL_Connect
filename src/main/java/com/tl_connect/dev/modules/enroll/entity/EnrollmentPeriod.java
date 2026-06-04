package com.tl_connect.dev.modules.enroll.entity;

import java.time.LocalDateTime;
import java.util.Optional;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tl_connect.dev.shared.common.exception.InvalidInputException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "enrollment_periods")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "semester_id", nullable = false)
    private Long semesterId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "max_credits", nullable = false)
    private Integer maxCredits;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static EnrollmentPeriod create(Long semesterId, LocalDateTime startTime, LocalDateTime endTime, int maxCredits) {
        if(semesterId <= 0) {
            throw new InvalidInputException("Semester ID must be positive");
        }
        if(startTime.isAfter(endTime)) {
            throw new InvalidInputException("Start time must be before end time");
        }

        if(maxCredits < 1) {
            throw new InvalidInputException("Max credits must be at least 1");
        }

        EnrollmentPeriod period = new EnrollmentPeriod();
        period.setSemesterId(semesterId);
        period.setStartTime(startTime);
        period.setEndTime(endTime);
        period.setMaxCredits(maxCredits);
        return period;
    }


    public void update(Long semesterId, LocalDateTime startTime, LocalDateTime endTime, int maxCredits) {
        LocalDateTime newStartTime = startTime == null ? this.startTime : startTime;
        LocalDateTime newEndTime = endTime == null ? this.endTime : endTime;

        if(newStartTime.isAfter(newEndTime)) {
            throw new InvalidInputException("Start time must be before end time");
        }

        Optional.ofNullable(semesterId).ifPresent(this::setSemesterId);
        Optional.ofNullable(startTime).ifPresent(this::setStartTime);
        Optional.ofNullable(endTime).ifPresent(this::setEndTime);
        Optional.ofNullable(maxCredits).ifPresent(this::setMaxCredits);
    }
}
