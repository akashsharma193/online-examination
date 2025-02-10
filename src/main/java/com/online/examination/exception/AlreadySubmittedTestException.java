package com.online.examination.exception;

import org.springframework.http.HttpStatus;

import com.online.examination.enums.ErrorCode;


public class AlreadySubmittedTestException extends CustomException {


	public AlreadySubmittedTestException() {
		super(ErrorCode.ALREADY_SUBMITTED_TEST, HttpStatus.UNAUTHORIZED);
	}


}
