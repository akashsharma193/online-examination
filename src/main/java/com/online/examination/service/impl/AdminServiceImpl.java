package com.online.examination.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
import com.online.examination.entity.Admin;
import com.online.examination.entity.Configuration;
import com.online.examination.entity.DeviceSession;
import com.online.examination.entity.IpRateLimit;
import com.online.examination.exception.AlreadyExistException;
import com.online.examination.exception.InactiveUserException;
import com.online.examination.exception.InvalidArgumentException;
import com.online.examination.exception.InvalidPasswordException;
import com.online.examination.exception.UserAlredayLoggedInException;
import com.online.examination.exception.UserNotFoundException;
import com.online.examination.repository.AdminRepo;
import com.online.examination.repository.DeviceSessionRepo;
import com.online.examination.repository.IpRateLimitRepo;
import com.online.examination.service.AdminService;
import com.online.examination.service.ConfigurationService;
import com.online.examination.utility.MailUtils;
import com.online.examination.utility.PasswordUtility;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private AdminRepo adminRepo;

	@Autowired
	private ConfigurationService configurationService;
	
	@Autowired
	private DeviceSessionRepo deviceSessionRepo;

	@Autowired
	private MailUtils mailUtils;
	
	@Autowired
	private IpRateLimitRepo ipRateLimitRepo;

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
		Admin admin = adminRepo.save(mapper.convertValue(dto, Admin.class));

		this.activateUser(admin);

		return mapper.convertValue(admin, UserDto.class);
	}

	@Override
	@Transactional
	public UserDto login(UserDto dto) {
		if(ObjectUtils.isEmpty(dto) || (ObjectUtils.isEmpty(dto.getEmail()) && ObjectUtils.isEmpty(dto.getMobile()))){
			throw new InvalidArgumentException();
		}
		
		Admin admin = adminRepo.findByMobileOrEmail(dto.getEmail(), dto.getEmail());
		if (ObjectUtils.isEmpty(admin)) {
			throw new UserNotFoundException();
		}

		if (ObjectUtils.isEmpty(admin.getIsActive()) || BooleanUtils.isFalse(admin.getIsActive())) {
			throw new InactiveUserException();
		}

		if (!admin.getPassword().equals(PasswordUtility.encryptOrHashPassword(admin.getUserId(), dto.getPassword(), admin.getMobile()))) {
			throw new InvalidPasswordException();
		}
		

		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		dto = mapper.convertValue(admin, UserDto.class);
		dto.setPassword(null);
		dto.setIsAdmin(true);
		return dto;

	}

	@Override
	@Transactional
	public void resetPassword(UserDto dto) {
		if(ObjectUtils.isEmpty(dto) || ObjectUtils.isEmpty(dto.getPassword())) {
			throw new InvalidArgumentException();
		}
		Admin admin = adminRepo.findByMobileOrEmail(dto.getMobile(), dto.getEmail());
		if (ObjectUtils.isEmpty(admin)) {
			throw new UserNotFoundException();
		}
		admin.setPassword(dto.getPassword());
		adminRepo.save(admin);
	}

	@Override
	@Transactional
	public void enableDisableUser(UserDto dto) {
		if (ObjectUtils.isEmpty(dto) || ObjectUtils.isEmpty(dto.getIsActive())) {
			throw new InvalidArgumentException();
		}
		Admin admin = adminRepo.findByMobileOrEmail(dto.getMobile(), dto.getEmail());
		if (ObjectUtils.isEmpty(admin)) {
			throw new UserNotFoundException();
		}
		admin.setIsActive(dto.getIsActive());
		adminRepo.save(admin);
	}

	private boolean isUserExist(UserDto dto) {
		Admin admin = adminRepo.findByMobileOrEmail(dto.getMobile(), dto.getEmail());
		return ObjectUtils.isNotEmpty(admin);

	}

	private void activateUser(Admin user) {
		Configuration configuration = configurationService.getConfiguration();
		if (ObjectUtils.isNotEmpty(configuration)) {
			switch (configuration.getActivationMode()) {
			case EMAIL_OTP:
//				mailUtils.sendEmail(user.getEmail(), "Welcome! Complete Your Registration with Our Activation Link",
//						mailUtils.activationMail(user));
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
		Admin admin = adminRepo.findByMobileOrEmail(dto.getMobile(), dto.getEmail());
		if (ObjectUtils.isEmpty(admin)) {
			throw new UserNotFoundException();
		}

		if (ObjectUtils.isEmpty(admin.getIsActive()) || BooleanUtils.isFalse(admin.getIsActive())) {
			throw new InactiveUserException();
		}
		
		DeviceSession deviceSession = deviceSessionRepo.findByUserIdAndIsActive(admin.getUserId(), true);
		
//		mailUtils.sendEmail(admin.getEmail(), "Logout Link for Your Account",
//				mailUtils.logoutMail(admin, deviceSession));
		
	}

	@Override
	public String forceLogOut(String userId, String sessionId) {
		if (StringUtils.isBlank(userId)) {
			return "Invalid request";
		}
		Admin admin = adminRepo.findByUserId(userId);
		if (ObjectUtils.isEmpty(admin)) {
			return "Invalid request";
		}

		if (ObjectUtils.isEmpty(admin.getIsActive()) || BooleanUtils.isFalse(admin.getIsActive())) {
			return "Invalid request";
		}
		DeviceSession deviceSession = deviceSessionRepo.findByUserIdAndIsActive(admin.getUserId(), true);

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
		Admin admin = adminRepo.findByUserId(userId);
		if (ObjectUtils.isEmpty(admin)) {
			return "Invalid request";
		}
		
		if (BooleanUtils.isTrue(admin.getIsActive())) {
			return "Invalid request";
		}
		admin.setIsActive(true);
		adminRepo.save(admin);
		return "User Activated Successfully";
		


	}

	@Override
	public UserDto checkLoggedInUser(String deviceId,HttpServletRequest request) {
		DeviceSession deviceSession = deviceSessionRepo.findByDeviceIdAndIsActive(deviceId, true);
		if(ObjectUtils.isNotEmpty(deviceSession) && StringUtils.isNoneBlank(deviceSession.getUserId())) {
			Admin admin = adminRepo.findByUserId(deviceSession.getUserId());
			
			if(ObjectUtils.isNotEmpty(admin)) {
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
		        
				
				return mapper.convertValue(admin, UserDto.class);
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
				throw new InvalidArgumentException();
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


}
