package com.tl_connect.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.entity.Lecturer;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
    
}
