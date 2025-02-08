package com.online.examination.service.impl;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.examination.dto.UserDto;
import com.online.examination.entity.Configuration;
import com.online.examination.entity.User;
import com.online.examination.exception.AlreadyExistException;
import com.online.examination.exception.InactiveUserException;
import com.online.examination.exception.InvalidArgumentException;
import com.online.examination.exception.InvalidPasswordException;
import com.online.examination.exception.UserNotFoundException;
import com.online.examination.repository.UserRepo;
import com.online.examination.service.ConfigurationService;
import com.online.examination.service.UserService;
import com.online.examination.utility.MailUtils;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private ConfigurationService configurationService;
	
	@Autowired
	private MailUtils mailUtils;

	@Override
	public UserDto saveUser(UserDto dto) {
		if(isUserExist(dto)) {
			throw new AlreadyExistException();
		}
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		User user = userRepo.save(mapper.convertValue(dto, User.class));
		
		this.activateUser(user);
		
		return mapper.convertValue(user, UserDto.class);
	}
	
	@Override
	public UserDto login(UserDto dto) {
		User user = userRepo.findByMobileOrEmail(dto.getMobile(), dto.getEmail());
		if (ObjectUtils.isEmpty(user)) {
		    throw new UserNotFoundException();
		}
		
		if (ObjectUtils.isEmpty(user.getIsActive()) || BooleanUtils.isFalse(user.getIsActive())) {
		    throw new InactiveUserException();
		}

		if (!user.getPassword().equals(dto.getPassword())) {
		    throw new InvalidPasswordException();
		}

		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.convertValue(user, UserDto.class);

	}
	
	@Override
	public void resetPassword(UserDto dto) {
		User user = userRepo.findByMobileOrEmail(dto.getMobile(), dto.getEmail());
		if (ObjectUtils.isEmpty(user)) {
		    throw new UserNotFoundException();
		}
		user.setPassword(dto.getPassword());
		userRepo.save(user);
	}
	
	@Override
	public void enableDisableUser(UserDto dto) {
		if(ObjectUtils.isEmpty(dto) || ObjectUtils.isEmpty(dto.getIsActive())) {
			throw new InvalidArgumentException();
		}
		User user = userRepo.findByMobileOrEmail(dto.getMobile(), dto.getEmail());
		if (ObjectUtils.isEmpty(user)) {
		    throw new UserNotFoundException();
		}
		user.setIsActive(dto.getIsActive());
		userRepo.save(user);
	}

	boolean isUserExist(UserDto dto) {
		User user = userRepo.findByMobileOrEmail(dto.getMobile(), dto.getEmail());
		return ObjectUtils.isNotEmpty(user);

	}

	private void activateUser(User user) {
		Configuration configuration = configurationService.getConfiguration();
		if (ObjectUtils.isNotEmpty(configuration)) {
		    switch (configuration.getActivationMode()) {
		        case EMAIL_OTP:
		        	mailUtils.sendEmail(user.getEmail(), "Welcome! Complete Your Registration with Our Activation Link", mailUtils.activationMail(user));
		            break;
		        case MOBILE_OTP:
		            // send mobile link
		            break;
		        default:
		            // do nothing
		            break;
		    }
		}

	}

}
