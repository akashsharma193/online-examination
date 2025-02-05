package com.online.examination.exception;

import org.springframework.http.HttpStatus;

import com.online.examination.enums.ErrorCode;


public class UserNotFoundException extends CustomException {

	public final String message;

	public UserNotFoundException() {
		super(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
		this.message = "";
	}

	public UserNotFoundException(String message) {
		super(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND, message);
		this.message = message;
	}

}
