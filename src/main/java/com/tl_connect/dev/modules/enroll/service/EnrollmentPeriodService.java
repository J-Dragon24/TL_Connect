package com.tl_connect.dev.modules.enroll.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl_connect.dev.modules.enroll.dto.CreateEnrollPeriodDTO;
import com.tl_connect.dev.modules.enroll.dto.UpdateEnrollPeriodDTO;
import com.tl_connect.dev.modules.enroll.entity.EnrollmentPeriod;
import com.tl_connect.dev.modules.enroll.repository.EnrollmentPeriodRepository;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ConflictException;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentPeriodService {

    private final EnrollmentPeriodRepository enrollmentPeriodRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_KEY_PREFIX = "enrollment:period:";

    public EnrollmentPeriod getPeriod(Long semesterId) {
        String cacheKey = CACHE_KEY_PREFIX + semesterId;

        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return objectMapper.convertValue(cached, EnrollmentPeriod.class);
            }
        } catch (Exception e) {
            log.warn("Redis unavailable, fallback to DB", e);
        }

        EnrollmentPeriod period = enrollmentPeriodRepository.findLatestBySemesterId(semesterId)
            .orElseThrow(() -> new NotFoundException("Enrollment period not found"));

        long secondsUntilEnd = ChronoUnit.SECONDS.between(
            LocalDateTime.now(), period.getEndTime()
        );
        redisTemplate.opsForValue().set(cacheKey, period, secondsUntilEnd, TimeUnit.SECONDS);

        return period;
    }

    public PagedResponse<EnrollmentPeriod> getAllPeriods(Pageable pageable, String semesterCode) {
        if (semesterCode == null || semesterCode.trim().isEmpty()) {
            semesterCode = null;
        }

        Page<EnrollmentPeriod> page = enrollmentPeriodRepository.findAllPeriods(pageable, semesterCode);
        return new PagedResponse<>(
            page.getContent(), 
            page.getNumber(), 
            page.getSize(), 
            page.getTotalElements(), 
            page.getTotalPages(), 
            page.isLast(), 
            page.isFirst()
        );
    }

    @Transactional
    public EnrollmentPeriod createPeriod(CreateEnrollPeriodDTO dto) {
        EnrollmentPeriod period = EnrollmentPeriod.create(dto.getSemesterId(), dto.getStartTime(), dto.getEndTime(), dto.getMaxCredits());

        try {
            EnrollmentPeriod saved = enrollmentPeriodRepository.save(period);
            return saved;
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Failed to create enrollment period: " + e.getMessage());
        }
    }

    @Transactional
    public EnrollmentPeriod updatePeriod(Long id, UpdateEnrollPeriodDTO dto) {
        EnrollmentPeriod period = enrollmentPeriodRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Enrollment period not found"));

        period.update(dto.getSemesterId(), dto.getStartTime(), dto.getEndTime(), dto.getMaxCredits());

        try {
            EnrollmentPeriod saved = enrollmentPeriodRepository.save(period);
            return saved;
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Failed to update enrollment period: " + e.getMessage());
        }
    }

    @Transactional
    public void deletePeriod(Long id) {
        EnrollmentPeriod period = enrollmentPeriodRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Enrollment period not found"));

        try {
            enrollmentPeriodRepository.delete(period);
        } catch (DataIntegrityViolationException e) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"The enrollment period has been used and cannot be deleted");
        }
    }

    public void invalidate(Long semesterId) {
        redisTemplate.delete(CACHE_KEY_PREFIX + semesterId);
    }
}
