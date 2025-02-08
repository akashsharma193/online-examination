package com.online.examination.service;

import com.online.examination.entity.Configuration;

public interface ConfigurationService {

	Configuration saveUser(Configuration dto);

	Configuration getConfiguration();

}
