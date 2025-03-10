package com.online.examination.exception;

import org.springframework.http.HttpStatus;

import com.online.examination.enums.ErrorCode;


public class UserAlredayLoggedInException extends CustomException{
	
	public final String message;

	public UserAlredayLoggedInException() {
		super(ErrorCode.USER_ALREADY_LOGGEDIN, HttpStatus.BAD_REQUEST);
		this.message="";
	}
	
	public UserAlredayLoggedInException(String message) {
		super(ErrorCode.USER_ALREADY_LOGGEDIN, HttpStatus.BAD_REQUEST);
		this.message=message;
	}

}
