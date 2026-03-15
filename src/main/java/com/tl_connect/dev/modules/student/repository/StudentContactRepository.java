package com.tl_connect.dev.modules.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.student.entity.StudentContact;

@Repository
public interface StudentContactRepository extends JpaRepository<StudentContact, Long> {

}
