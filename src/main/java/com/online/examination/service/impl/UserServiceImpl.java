package com.online.examination.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import com.online.examination.constants.ErrorMessage;
import com.online.examination.dto.FcmToken;
import com.online.examination.dto.UserDto;
import com.online.examination.entity.Configuration;
import com.online.examination.entity.DeviceSession;
import com.online.examination.entity.IpRateLimit;
import com.online.examination.entity.User;
import com.online.examination.exception.AlreadyExistException;
import com.online.examination.exception.InactiveUserException;
import com.online.examination.exception.InvalidArgumentException;
import com.online.examination.exception.InvalidPasswordException;
import com.online.examination.exception.UserAlredayLoggedInException;
import com.online.examination.exception.UserNotFoundException;
import com.online.examination.repository.DeviceSessionRepo;
import com.online.examination.repository.IpRateLimitRepo;
import com.online.examination.repository.UserRepo;
import com.online.examination.service.AdminService;
import com.online.examination.service.ConfigurationService;
import com.online.examination.service.UserService;
import com.online.examination.utility.MailUtils;
import com.online.examination.utility.PasswordUtility;

import jakarta.servlet.http.HttpServletRequest;
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
	
	@Autowired
	private IpRateLimitRepo ipRateLimitRepo;
	
	@Autowired
	private AdminService adminService;

	@Override
	@Transactional
	public UserDto saveUser(UserDto dto) {
		if (this.isUserExist(dto)) {
			throw new AlreadyExistException(ErrorMessage.USER_ALREADY_EXIST);
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
	@Transactional
	public UserDto login(UserDto dto, String deviceId) {
		if(ObjectUtils.isEmpty(dto) || (ObjectUtils.isEmpty(dto.getEmail()) && ObjectUtils.isEmpty(dto.getMobile()))){
			throw new InvalidArgumentException(ErrorMessage.INVALID_ARGUMENT);
		}
		
		User user = userRepo.findByMobileOrEmail(dto.getEmail(), dto.getEmail());
		if (ObjectUtils.isEmpty(user)) {
			UserDto adminUser = adminService.login(dto);
			if(ObjectUtils.isNotEmpty(adminUser)) {
				return adminUser;
			}
			throw new UserNotFoundException(ErrorMessage.USER_NOT_FOUND);
		}

		if (ObjectUtils.isEmpty(user.getIsActive()) || BooleanUtils.isFalse(user.getIsActive())) {
			throw new InactiveUserException(ErrorMessage.INACTIVE_USER);
		}

		if (!user.getPassword().equals(PasswordUtility.encryptOrHashPassword(user.getUserId(), dto.getPassword(), user.getMobile()))) {
			throw new InvalidPasswordException(ErrorMessage.INVALID_PASSWORD);
		}
		
		if(StringUtils.isBlank(deviceId)) {
			throw new InvalidArgumentException(ErrorMessage.DEVICE_ID_EMPTY);
		}
		
		DeviceSession deviceSession = deviceSessionRepo.findByUserIdAndIsActive(user.getUserId(), true);
		
		if (ObjectUtils.isEmpty(deviceSession)) {
			DeviceSession deviceSessionByDevice = deviceSessionRepo.findByDeviceIdAndIsActive(deviceId, true);
			if(ObjectUtils.isNotEmpty(deviceSessionByDevice)) {
				deviceSessionRepo.delete(deviceSessionByDevice);
			}
			
			
			LocalDateTime localDateTime = LocalDateTime.now();
			ZoneId istZoneId = ZoneId.of("Asia/Kolkata");
			ZonedDateTime istDateTime = localDateTime.atZone(ZoneId.systemDefault()) // Convert LocalDateTime to ZonedDateTime using system default time zone
                    .withZoneSameInstant(istZoneId); // Convert it to IST


			
			deviceSessionRepo
					.save(DeviceSession.builder().userId(user.getUserId()).deviceId(deviceId).isActive(true)
							.lastLoginTime(istDateTime.toLocalDateTime()).sessionId(UUID.randomUUID().toString()).build());
			
			
		} else if (!deviceSession.getDeviceId().equals(deviceId)) {
			throw new UserAlredayLoggedInException(ErrorMessage.USER_ALREADY_LOGGEDIN);
		}

		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		dto = mapper.convertValue(user, UserDto.class);
		dto.setPassword(null);
		return dto;

	}

	@Override
	@Transactional
	public void resetPassword(UserDto dto) {
		if(ObjectUtils.isEmpty(dto) || ObjectUtils.isEmpty(dto.getPassword())) {
			throw new InvalidArgumentException(ErrorMessage.INVALID_ARGUMENT);
		}
		User user = userRepo.findByMobileOrEmail(dto.getMobile(), dto.getEmail());
		if (ObjectUtils.isEmpty(user)) {
			throw new UserNotFoundException(ErrorMessage.USER_NOT_FOUND);
		}
		user.setPassword(dto.getPassword());
		userRepo.save(user);
	}

	@Override
	@Transactional
	public void enableDisableUser(UserDto dto) {
		if (ObjectUtils.isEmpty(dto) || ObjectUtils.isEmpty(dto.getIsActive())) {
			throw new InvalidArgumentException(ErrorMessage.INVALID_ARGUMENT);
		}
		User user = userRepo.findByMobileOrEmail(dto.getMobile(), dto.getEmail());
		if (ObjectUtils.isEmpty(user)) {
			throw new UserNotFoundException(ErrorMessage.USER_NOT_FOUND);
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
	    	throw new InvalidArgumentException(ErrorMessage.INVALID_ARGUMENT);
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
			throw new InvalidArgumentException(ErrorMessage.INVALID_ARGUMENT);
		}
		
		Optional<DeviceSession> deviceSessionContainer = deviceSessionRepo.findByDeviceIdAndUserId(deviceId, dto.getUserId());
		if(deviceSessionContainer.isEmpty()) {
			throw new UserNotFoundException(ErrorMessage.USER_NOT_FOUND);
		}
		if(deviceSessionContainer.isPresent()) {
			if(deviceSessionContainer.get().getIsActive()) {
				deviceSessionRepo.delete(deviceSessionContainer.get());
			}else {
				throw new UserNotFoundException(ErrorMessage.USER_NOT_FOUND);
			}
		}
		
	}

	@Override
	public void forceLogOutRequest(UserDto dto) {
		if(ObjectUtils.isEmpty(dto) || (ObjectUtils.isEmpty(dto.getEmail()) && ObjectUtils.isEmpty(dto.getMobile()))){
			throw new InvalidArgumentException(ErrorMessage.INVALID_ARGUMENT);
		}
		User user = userRepo.findByMobileOrEmail(dto.getMobile(), dto.getEmail());
		if (ObjectUtils.isEmpty(user)) {
			throw new UserNotFoundException(ErrorMessage.USER_NOT_FOUND);
		}

		if (ObjectUtils.isEmpty(user.getIsActive()) || BooleanUtils.isFalse(user.getIsActive())) {
			throw new InactiveUserException(ErrorMessage.INACTIVE_USER);
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

	@Override
	public UserDto checkLoggedInUser(String deviceId,HttpServletRequest request) {
		DeviceSession deviceSession = deviceSessionRepo.findByDeviceIdAndIsActive(deviceId, true);
		if(ObjectUtils.isNotEmpty(deviceSession) && StringUtils.isNoneBlank(deviceSession.getUserId())) {
			User user = userRepo.findByUserId(deviceSession.getUserId());
			
			if(ObjectUtils.isNotEmpty(user)) {
				String ipAddress = request.getHeader("X-FORWARDED-FOR");
				if (ipAddress == null || ipAddress.isEmpty()) {
					ipAddress = request.getRemoteAddr();
				}
				
				IpRateLimit ipRateLimit = ipRateLimitRepo.findByIpAddress(ipAddress);
				
				if(ObjectUtils.isNotEmpty(ipRateLimit)) {
					ipRateLimitRepo.delete(ipRateLimit);
				}
				
				
				ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				
				LocalDateTime localDateTime = LocalDateTime.now();
				ZoneId istZoneId = ZoneId.of("Asia/Kolkata");
				ZonedDateTime istDateTime = localDateTime.atZone(ZoneId.systemDefault()) // Convert LocalDateTime to ZonedDateTime using system default time zone
	                    .withZoneSameInstant(istZoneId); // Convert it to IST
				
		        deviceSession.setLastLoginTime(istDateTime.toLocalDateTime());
		        deviceSessionRepo.save(deviceSession);
		        
				
				return mapper.convertValue(user, UserDto.class);
			}
		}
		this.checkIpRate(request, deviceId);
		return null;
	}

	private void checkIpRate(HttpServletRequest request, String deviceId) {
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null || ipAddress.isEmpty()) {
			ipAddress = request.getRemoteAddr();
		}
		IpRateLimit ipRateLimit = ipRateLimitRepo.findByIpAddress(ipAddress);
		if(ObjectUtils.isNotEmpty(ipRateLimit)) {
			if(ipRateLimit.getLimitEndTime().isAfter(LocalDateTime.now())) {
				throw new InvalidArgumentException(ErrorMessage.INVALID_ARGUMENT);
			}
			
			if(ipRateLimit.getFailCount()>=4) {
				ipRateLimit.setFailCount(0);
				ipRateLimit.setLimitEndTime(LocalDateTime.now().plusMinutes(1));
			}else {
				ipRateLimit.setFailCount(ipRateLimit.getFailCount()+1);
			}
			ipRateLimitRepo.save(ipRateLimit);
		}else {
			IpRateLimit ipRateLimitObj = new IpRateLimit();
			ipRateLimitObj.setDeviceId(deviceId);
			ipRateLimitObj.setFailCount(1);
			ipRateLimitObj.setIpAddress(ipAddress);
			ipRateLimitObj.setLimitEndTime(LocalDateTime.now());
			ipRateLimitRepo.save(ipRateLimitObj);
		}
		
		
	}

	@Transactional
	@Override
	public void saveFcmToken(FcmToken dto) {
		
		if (ObjectUtils.isEmpty(dto) || ObjectUtils.isEmpty(dto.getFcmToken()) || ObjectUtils.isEmpty(dto.getUserId())) {
		    throw new InvalidArgumentException(
		        ObjectUtils.isEmpty(dto) ? null :
		        ObjectUtils.isEmpty(dto.getFcmToken()) ? ErrorMessage.HTM_TOKEN_IS_EMPTY :
		        ErrorMessage.USER_ID_IS_EMPTY
		    );
		}
		
		User user = userRepo.findByUserId(dto.getUserId());
		
		if(ObjectUtils.isEmpty(user)) {
			throw new UserNotFoundException(ErrorMessage.USER_NOT_FOUND);
		}
		
		user.setFcmToken(dto.getFcmToken());
		userRepo.save(user);
		
	}


}
