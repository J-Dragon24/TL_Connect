package com.tl_connect.dev.modules.major.repository;


import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.major.entity.Major;

@Repository
public interface MajorRepository extends JpaRepository<Major, Long>{

    List<Major> findByMajorCodeIn(Set<String> majorCodes);

    Optional<Major> findByMajorCode(String majorCode);
}
