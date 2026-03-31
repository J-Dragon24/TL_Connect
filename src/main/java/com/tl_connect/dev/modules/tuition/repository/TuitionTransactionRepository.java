package com.tl_connect.dev.modules.tuition.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.tuition.entity.TuitionTransaction;

@Repository
public interface TuitionTransactionRepository extends JpaRepository<TuitionTransaction, Long> {
    
}
