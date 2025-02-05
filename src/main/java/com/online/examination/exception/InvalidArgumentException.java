package com.online.examination.exception;

import org.springframework.http.HttpStatus;

import com.online.examination.enums.ErrorCode;


public class InvalidArgumentException extends CustomException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final String message;

	public InvalidArgumentException() {
		super(ErrorCode.INVALID_ARGUMENT, HttpStatus.BAD_REQUEST);
		this.message = "";
	}

	public InvalidArgumentException(String message) {
		super(ErrorCode.INVALID_ARGUMENT, HttpStatus.BAD_REQUEST);
		this.message = message;
	}

	public InvalidArgumentException(ErrorCode error, HttpStatus statusCode) {
		super(ErrorCode.USER_NOT_FOUND, statusCode.BAD_REQUEST);
		this.message = "";	
	}

}
