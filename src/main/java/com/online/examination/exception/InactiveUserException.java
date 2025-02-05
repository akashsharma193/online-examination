package com.online.examination.exception;

import org.springframework.http.HttpStatus;

import com.online.examination.enums.ErrorCode;


public class InactiveUserException extends CustomException{

	public InactiveUserException() {
		super(ErrorCode.INACTIVE_USER, HttpStatus.BAD_REQUEST);
	}

}
