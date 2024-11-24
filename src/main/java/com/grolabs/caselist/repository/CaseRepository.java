package com.grolabs.caselist.repository;

import com.grolabs.caselist.entity.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {

    List<Case> findAllByUserId(Long Id);
}
