package com.tl_connect.dev.modules.tuition.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.core.common.enums.StudentCourseClassStatus;
import com.tl_connect.dev.core.common.enums.TuitionStatus;
import com.tl_connect.dev.modules.enroll.entity.StudentCourseClass;
import com.tl_connect.dev.modules.enroll.repository.StudentCourseClassRepository;
import com.tl_connect.dev.modules.subject.entity.Subject;
import com.tl_connect.dev.modules.subject.repository.SubjectRepository;
import com.tl_connect.dev.modules.tuition.entity.TuitionFeeConfig;
import com.tl_connect.dev.modules.tuition.entity.TuitionInvoice;
import com.tl_connect.dev.modules.tuition.entity.TuitionInvoiceItem;
import com.tl_connect.dev.modules.tuition.repository.TuitionFeeConfigRepository;
import com.tl_connect.dev.modules.tuition.repository.TuitionInvoiceItemRepository;
import com.tl_connect.dev.modules.tuition.repository.TuitionInvoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TuitionModifyService {
    
    private final TuitionInvoiceRepository tuitionInvoiceRepository;
    private final TuitionInvoiceItemRepository tuitionInvoiceItemRepository;
    private final StudentCourseClassRepository studentCourseClassRepository;
    private final TuitionFeeConfigRepository tuitionFeeConfigRepository;
    private final SubjectRepository subjectRepository;

    public void createInvoicesForSemester(Long semesterId) {

        List<StudentCourseClass> all = studentCourseClassRepository.findAll();

        Map<Long, List<StudentCourseClass>> grouped =
                all.stream()
                        .filter(s -> s.getSemesterId().equals(semesterId))
                        .filter(s -> StudentCourseClassStatus.ENROLLED.equals(s.getStatus()))
                        .collect(Collectors.groupingBy(StudentCourseClass::getStudentId));

        for (Long studentId : grouped.keySet()) {
            createInvoiceForStudent(studentId, semesterId);
        }
    }

    public void createInvoiceForStudent(Long studentId, Long semesterId) {

        Optional<TuitionInvoice> existing =
                tuitionInvoiceRepository.findByStudentIdAndSemesterId(studentId, semesterId);

        if (existing.isPresent()) {
            throw new RuntimeException("Invoice already exists");
        }

        createInvoice(studentId, semesterId);
    }

    @Transactional
    public void regenerateInvoice(Long studentId, Long semesterId) {

        tuitionInvoiceRepository.findByStudentIdAndSemesterId(studentId, semesterId)
                .ifPresent(invoice -> {
                    invoice.setStatus(TuitionStatus.CANCELLED);
                    tuitionInvoiceRepository.save(invoice);
                });

        createInvoice(studentId, semesterId);
    }

    @Transactional
    private void createInvoice(Long studentId, Long semesterId) {

        List<StudentCourseClass> enrollments = studentCourseClassRepository.findByStudentIdAndSemesterIdAndStatus(
                        studentId, semesterId, StudentCourseClassStatus.ENROLLED
                );

        if (enrollments.isEmpty()) return;

        List<Long> subjectIds = enrollments.stream().map(StudentCourseClass::getSubjectId).collect(Collectors.toList());

        Map<Long, Subject> mapSubjects = subjectRepository.findAllById(subjectIds).stream().collect(Collectors.toMap(Subject::getId, s -> s));

        TuitionFeeConfig config = tuitionFeeConfigRepository
                .getActiveConfig(LocalDate.now())
                .orElseThrow(() -> new RuntimeException("No tuition config found"));

        BigDecimal total = BigDecimal.ZERO;

        TuitionInvoice invoice = new TuitionInvoice();
        invoice.setStudentId(studentId);
        invoice.setSemesterId(semesterId);
        invoice.setDueDate(LocalDate.now().plusDays(15));
        invoice.setStatus(TuitionStatus.UNPAID);
        invoice.setTotalAmount(total);
        invoice.setFinalAmount(total);

        invoice = tuitionInvoiceRepository.save(invoice);

        for (StudentCourseClass scc : enrollments) {
            Long subjectId = scc.getSubjectId();
            Subject subject = mapSubjects.get(subjectId);

            BigDecimal coefficient = subject.getCoefficient();
            int credits = subject.getCredits();

            BigDecimal price = config.getBasePricePerCredit();
            BigDecimal amount = price.multiply(BigDecimal.valueOf(credits)).multiply(coefficient);

            TuitionInvoiceItem item = new TuitionInvoiceItem();
            item.setInvoiceId(invoice.getId());
            item.setCourseClassId(scc.getCourseClassId());
            item.setCredits(credits);
            item.setPricePerCredit(price);
            item.setAmount(amount);

            tuitionInvoiceItemRepository.save(item);

            total = total.add(amount);
        }

        invoice.setTotalAmount(total);
        invoice.setFinalAmount(total);

        tuitionInvoiceRepository.save(invoice);
    }
}
