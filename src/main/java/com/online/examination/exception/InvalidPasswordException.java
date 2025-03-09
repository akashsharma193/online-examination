package com.online.examination.exception;

import org.springframework.http.HttpStatus;

import com.online.examination.enums.ErrorCode;


public class InvalidPasswordException extends CustomException {
	
	public final String message;


	public InvalidPasswordException() {
		super(ErrorCode.INVALID_PASSWORD, HttpStatus.UNAUTHORIZED);
		this.message = "";
	}
	
	public InvalidPasswordException(String message) {
		super(ErrorCode.INVALID_PASSWORD, HttpStatus.UNAUTHORIZED);
		this.message = message;
	}


}
