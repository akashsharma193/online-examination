package com.online.examination.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.online.examination.entity.ComplianceEntity;
import com.online.examination.repository.ComplianceRepo;
import com.online.examination.service.ComplianceService;

@Service
public class ComplianceServiceImpl implements ComplianceService {
	
	@Autowired
	private ComplianceRepo complianceRepo;

	@Override
	public ComplianceEntity saveCompliance(ComplianceEntity dto) {
		return complianceRepo.save(dto);
	}

	@Override
	public List<ComplianceEntity> getCompliance() {
		return complianceRepo.findAllByIsActiveTrue();
	}

}
