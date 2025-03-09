package com.online.examination.service;

import com.online.examination.dto.UserDto;

import jakarta.servlet.http.HttpServletRequest;

public interface AdminService {

	UserDto saveUser(UserDto dto);

	UserDto login(UserDto dto);

	void resetPassword(UserDto dto);

	void enableDisableUser(UserDto dto);

	void logOut(UserDto dto, String deviceId);

	void forceLogOutRequest(UserDto dto);

	String forceLogOut(String userId, String sessionId);

	String activateUser(String userId);

	UserDto checkLoggedInUser(String deviceId, HttpServletRequest request);

}
