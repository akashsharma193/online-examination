package com.online.examination.service;

import com.online.examination.dto.UserDto;

public interface UserService {

	UserDto saveUser(UserDto dto);

	UserDto login(UserDto dto, String deviceId);

	void resetPassword(UserDto dto);

	void enableDisableUser(UserDto dto);

	void logOut(UserDto dto, String deviceId);

	void forceLogOutRequest(UserDto dto);

	String forceLogOut(String userId);

}
