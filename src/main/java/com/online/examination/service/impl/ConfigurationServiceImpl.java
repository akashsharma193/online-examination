package com.online.examination.service.impl;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.online.examination.entity.Configuration;
import com.online.examination.exception.InvalidArgumentException;
import com.online.examination.repository.ConfigurationRepo;
import com.online.examination.service.ConfigurationService;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {
	
	@Autowired
	private ConfigurationRepo configurationRepo;

	@Override
	public Configuration saveUser(Configuration dto) {
		if(ObjectUtils.isNotEmpty(dto) && ObjectUtils.isNotEmpty(dto.getId())) {
			return configurationRepo.save(dto);
		}
		if(ObjectUtils.isNotEmpty(configurationRepo.findAll())) {
			throw new InvalidArgumentException();
		}
		return configurationRepo.save(dto);
	}

	@Override
	public Configuration getConfiguration() {
		return configurationRepo.findAll().get(0);
	}

}
