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

    
}
