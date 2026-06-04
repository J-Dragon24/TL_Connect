package com.tl_connect.dev.modules.tuition.scheduler;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tl_connect.dev.modules.tuition.repository.TuitionInvoiceRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TuitionScheduler {

    private final TuitionInvoiceRepository tuitionInvoiceRepository;

    @Scheduled(cron = "0 5 0 * * *")
    public void updateOverdueTuition() {
        int updated = tuitionInvoiceRepository.updateOverdueInvoice(LocalDate.now());

        System.out.println("Updated overdue tuition: " + updated);
    }
}