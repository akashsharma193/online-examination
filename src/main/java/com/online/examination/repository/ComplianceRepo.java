package com.online.examination.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.examination.entity.ComplianceEntity;

@Repository
public interface ComplianceRepo extends JpaRepository<ComplianceEntity, Long>{
	
	List<ComplianceEntity> findAllByIsActiveTrue();

}
