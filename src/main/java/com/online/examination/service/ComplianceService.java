package com.online.examination.service;

import java.util.List;

import com.online.examination.entity.ComplianceEntity;

public interface ComplianceService {

	ComplianceEntity saveCompliance(ComplianceEntity dto);

	List<ComplianceEntity> getCompliance();

}
