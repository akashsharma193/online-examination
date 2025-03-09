package com.online.examination.exception;

import org.springframework.http.HttpStatus;

import com.online.examination.enums.ErrorCode;


public class InactiveUserException extends CustomException{
	
	public final String message;

	public InactiveUserException() {
		super(ErrorCode.INACTIVE_USER, HttpStatus.BAD_REQUEST);
		this.message = "";
	}
	
	public InactiveUserException(String message) {
		super(ErrorCode.INACTIVE_USER, HttpStatus.BAD_REQUEST, message);
		this.message = message;
	}

}
