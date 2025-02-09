package com.online.examination.service.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.examination.dto.UserDto;
import com.online.examination.entity.Configuration;
import com.online.examination.entity.DeviceSession;
import com.online.examination.entity.User;
import com.online.examination.exception.AlreadyExistException;
import com.online.examination.exception.InactiveUserException;
import com.online.examination.exception.InvalidArgumentException;
import com.online.examination.exception.InvalidPasswordException;
import com.online.examination.exception.UserAlredayLoggedInException;
import com.online.examination.exception.UserNotFoundException;
import com.online.examination.repository.DeviceSessionRepo;
import com.online.examination.repository.UserRepo;
import com.online.examination.service.ConfigurationService;
import com.online.examination.service.UserService;
import com.online.examination.utility.MailUtils;
import com.online.examination.utility.PasswordUtility;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ConfigurationService configurationService;
	
	@Autowired
	private DeviceSessionRepo deviceSessionRepo;

	@Autowired
	private MailUtils mailUtils;

	@Override
	@Transactional
	public UserDto saveUser(UserDto dto) {
		if (this.isUserExist(dto)) {
			throw new AlreadyExistException();
		}
		this.checkRegistrationInfo(dto);
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		dto.setUserId(this.generateUserId(dto));
		dto.setPassword(PasswordUtility.encryptOrHashPassword(dto.getUserId(), dto.getPassword(), dto.getMobile()));
		User user = userRepo.save(mapper.convertValue(dto, User.class));

		this.activateUser(user);

		return mapper.convertValue(user, UserDto.class);
	}

	@Override
	public UserDto login(UserDto dto, String deviceId) {
		if(ObjectUtils.isEmpty(dto) || (ObjectUtils.isEmpty(dto.getEmail()) && ObjectUtils.isEmpty(dto.getMobile())) || StringUtils.isBlank(deviceId)){
			throw new InvalidArgumentException();
		}
		
		User user = userRepo.findByMobileOrEmail(dto.getEmail(), dto.getEmail());
		if (ObjectUtils.isEmpty(user)) {
			throw new UserNotFoundException();
		}

		if (ObjectUtils.isEmpty(user.getIsActive()) || BooleanUtils.isFalse(user.getIsActive())) {
			throw new InactiveUserException();
		}

		if (!user.getPassword().equals(PasswordUtility.encryptOrHashPassword(user.getUserId(), dto.getPassword(), user.getMobile()))) {
			throw new InvalidPasswordException();
		}
		
		DeviceSession deviceSession = deviceSessionRepo.findByUserIdAndIsActive(user.getUserId(), true);
		
		if (ObjectUtils.isEmpty(deviceSession)) {
			deviceSessionRepo
					.save(DeviceSession.builder().userId(user.getUserId()).deviceId(deviceId).isActive(true).sessionId(UUID.randomUUID().toString()).build());
		} else if (!deviceSession.getDeviceId().equals(deviceId)) {
			throw new UserAlredayLoggedInException();
		}

		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.convertValue(user, UserDto.class);

	}

	@Override
	@Transactional
	public void resetPassword(UserDto dto) {
		if(ObjectUtils.isEmpty(dto) || ObjectUtils.isEmpty(dto.getPassword())) {
			throw new InvalidArgumentException();
		}
		User user = userRepo.findByMobileOrEmail(dto.getMobile(), dto.getEmail());
		if (ObjectUtils.isEmpty(user)) {
			throw new UserNotFoundException();
		}
		user.setPassword(dto.getPassword());
		userRepo.save(user);
	}

	@Override
	@Transactional
	public void enableDisableUser(UserDto dto) {
		if (ObjectUtils.isEmpty(dto) || ObjectUtils.isEmpty(dto.getIsActive())) {
			throw new InvalidArgumentException();
		}
		User user = userRepo.findByMobileOrEmail(dto.getMobile(), dto.getEmail());
		if (ObjectUtils.isEmpty(user)) {
			throw new UserNotFoundException();
		}
		user.setIsActive(dto.getIsActive());
		userRepo.save(user);
	}

	private boolean isUserExist(UserDto dto) {
		User user = userRepo.findByMobileOrEmail(dto.getMobile(), dto.getEmail());
		return ObjectUtils.isNotEmpty(user);

	}

	private void activateUser(User user) {
		Configuration configuration = configurationService.getConfiguration();
		if (ObjectUtils.isNotEmpty(configuration)) {
			switch (configuration.getActivationMode()) {
			case EMAIL_OTP:
				mailUtils.sendEmail(user.getEmail(), "Welcome! Complete Your Registration with Our Activation Link",
						mailUtils.activationMail(user));
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

	private void checkRegistrationInfo(UserDto dto) {
	    if (ObjectUtils.isEmpty(dto)) {
	        throw new InvalidArgumentException();
	    }

	    Map<String, String> fields = new LinkedHashMap<>();
	    fields.put("Batch", dto.getBatch());
	    fields.put("Email", dto.getEmail());
	    fields.put("Mobile", dto.getMobile());
	    fields.put("Name", dto.getName());
	    fields.put("Organization", dto.getOrgCode());
	    fields.put("Password", dto.getPassword());

	    for (Map.Entry<String, String> entry : fields.entrySet()) {
	        if (ObjectUtils.isEmpty(entry.getValue())) {
	            throw new InvalidArgumentException(entry.getKey() + " can't be empty");
	        }
	    }
	}
	
	private String generateUserId(UserDto dto) {
		StringBuilder userid = new StringBuilder();

		if (dto.getEmail().length() > 4) {
			userid.append(dto.getEmail(), 0, 4);
		}
		userid.append(dto.getMobile());

		return userid.toString();
	}

	@Override
	public void logOut(UserDto dto, String deviceId) {
		
		if(ObjectUtils.isEmpty(deviceId) || ObjectUtils.isEmpty(dto) || ObjectUtils.isEmpty(dto.getUserId())) {
			throw new InvalidArgumentException();
		}
		
		Optional<DeviceSession> deviceSessionContainer = deviceSessionRepo.findByDeviceIdAndUserId(deviceId, dto.getUserId());
		if(deviceSessionContainer.isEmpty()) {
			throw new UserNotFoundException();
		}
		if(deviceSessionContainer.isPresent()) {
			if(deviceSessionContainer.get().getIsActive()) {
				deviceSessionRepo.delete(deviceSessionContainer.get());
			}else {
				throw new UserNotFoundException();
			}
		}
		
	}

	@Override
	public void forceLogOutRequest(UserDto dto) {
		if(ObjectUtils.isEmpty(dto) || (ObjectUtils.isEmpty(dto.getEmail()) && ObjectUtils.isEmpty(dto.getMobile()))){
			throw new InvalidArgumentException();
		}
		User user = userRepo.findByMobileOrEmail(dto.getMobile(), dto.getEmail());
		if (ObjectUtils.isEmpty(user)) {
			throw new UserNotFoundException();
		}

		if (ObjectUtils.isEmpty(user.getIsActive()) || BooleanUtils.isFalse(user.getIsActive())) {
			throw new InactiveUserException();
		}
		
		DeviceSession deviceSession = deviceSessionRepo.findByUserIdAndIsActive(user.getUserId(), true);
		
		mailUtils.sendEmail(user.getEmail(), "Logout Link for Your Account",
				mailUtils.logoutMail(user, deviceSession));
		
	}

	@Override
	public String forceLogOut(String userId, String sessionId) {
		if (StringUtils.isBlank(userId)) {
			return "Invalid request";
		}
		User user = userRepo.findByUserId(userId);
		if (ObjectUtils.isEmpty(user)) {
			return "Invalid request";
		}

		if (ObjectUtils.isEmpty(user.getIsActive()) || BooleanUtils.isFalse(user.getIsActive())) {
			return "Invalid request";
		}
		DeviceSession deviceSession = deviceSessionRepo.findByUserIdAndIsActive(user.getUserId(), true);

		if (ObjectUtils.isEmpty(deviceSession) || !sessionId.equals(deviceSession.getSessionId())) {
			return "Invalid request";
		}

		if (deviceSession.getIsActive()) {
			deviceSessionRepo.delete(deviceSession);
		} else {
			return "Invalid request";
		}
		return "User has successfully logged out";

	}

	@Override
	public String activateUser(String userId) {
		User user = userRepo.findByUserId(userId);
		if (ObjectUtils.isEmpty(user)) {
			return "Invalid request";
		}
		
		if (BooleanUtils.isTrue(user.getIsActive())) {
			return "Invalid request";
		}
		user.setIsActive(true);
		userRepo.save(user);
		return "User Activated Successfully";
		


	}


}
