package com.online.examination.aop;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.examination.dto.UserDto;
import com.online.examination.response.Response;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class LoggingAspect {

	@Before("execution(* com.online.examination.controller..*(..))")
	public void beforeControllerAdvice(JoinPoint joinPoint) {
		log.info("Inside Controller Method :::" + joinPoint.getSignature().toShortString());
		log.info("Controller Payload :::" + Arrays.asList(joinPoint.getArgs()));
	}

	@Before("execution(* com.online.examination.service..*(..))")
	public void beforeServiceAdvice(JoinPoint joinPoint) {
		log.info("Inside service Method :::" + joinPoint.getSignature().toShortString());
		log.info("Service Payload :::" + Arrays.asList(joinPoint.getArgs()));
	}

	@Pointcut("execution(* com.online.examination.controller..*(..))")
	public void controllerMethods() {
	}

	@AfterReturning(pointcut = "controllerMethods()", returning = "result")
	public void logControllerResponse(Object result) {
		ResponseEntity<Response> responseEntity = (ResponseEntity<Response>) result;
		log.info("Controller response: " + responseEntity.getBody());
	}

}
