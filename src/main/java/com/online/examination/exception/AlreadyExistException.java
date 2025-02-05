package com.online.examination.exception;

import org.springframework.http.HttpStatus;

import com.online.examination.enums.ErrorCode;


public class AlreadyExistException extends CustomException {

	public final String message;

	public AlreadyExistException() {
		super(ErrorCode.USER_ALREADY_EXIST, HttpStatus.UNAUTHORIZED);
		this.message = "";
	}

	public AlreadyExistException(String message) {
		super(ErrorCode.USER_ALREADY_EXIST, HttpStatus.UNAUTHORIZED, message);
		this.message = message;
	}

}
