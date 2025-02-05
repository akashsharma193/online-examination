package com.online.examination.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.online.examination.entity.ExceptionEntity;
import com.online.examination.repository.ExceptionRepository;
import com.online.examination.response.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	private ExceptionRepository exceptionRepository;

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> exception(Exception ex) {
		log.error("Exception: ", ex);
		ExceptionEntity data = new ExceptionEntity();
		data.setError(ex.getMessage());
		data.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		data.setTrac(ExceptionUtils.getStackTrace(ex));
		exceptionRepository.save(data);
		return buildResponseEntity(new Response(ex), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<Object> customException(CustomException ex) {
		log.error("Exception: ", ex);
		return buildResponseEntity(new Response(ex), ex.getHttpStatus());
	}

	private ResponseEntity<Object> buildResponseEntity(Response response, HttpStatus status) {
		return new ResponseEntity<>(response, status);
	}

}
