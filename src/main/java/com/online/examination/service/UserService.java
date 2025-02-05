package com.online.examination.service;

import com.online.examination.dto.UserDto;

public interface UserService {

	UserDto saveUser(UserDto dto);

	UserDto login(UserDto dto);

	void resetPassword(UserDto dto);

	void enableDisableUser(UserDto dto);

}
