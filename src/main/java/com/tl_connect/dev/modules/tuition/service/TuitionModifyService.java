package com.tl_connect.dev.modules.tuition.service;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.tuition.entity.TuitionInvoice;
import com.tl_connect.dev.modules.tuition.entity.TuitionInvoiceItem;
import com.tl_connect.dev.modules.tuition.repository.TuitionInvoiceItemRepository;
import com.tl_connect.dev.modules.tuition.repository.TuitionInvoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TuitionModifyService {
    
    private final TuitionInvoiceRepository tuitionInvoiceRepository;
    private final TuitionInvoiceItemRepository tuitionInvoiceItemRepository;
}
