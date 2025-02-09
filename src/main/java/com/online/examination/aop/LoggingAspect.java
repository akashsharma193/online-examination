package com.online.examination.aop;

import java.util.Arrays;
import java.util.Enumeration;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.online.examination.response.Response;

import jakarta.servlet.http.HttpServletRequest;
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

	@Before("execution(* com.online.examination.secure.controller..*(..))")
	public void beforeSecureControllerAdvice(JoinPoint joinPoint) {
		log.info("Inside Controller Method :::" + joinPoint.getSignature().toShortString());
		log.info("Controller Payload :::" + Arrays.asList(joinPoint.getArgs()));
		
		 ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
	        if (attributes != null) {
	            HttpServletRequest request = attributes.getRequest();
	            HttpHeaders headers = new HttpHeaders();

	            Enumeration<String> headerNames = request.getHeaderNames();
	            if (headerNames != null) {
	                while (headerNames.hasMoreElements()) {
	                    String headerName = headerNames.nextElement();
	                    headers.add(headerName, request.getHeader(headerName));
	                }
	            }

	            String userId = headers.getFirst("userId");
	            String deviceId = headers.getFirst("deviceId");
	            System.out.println("Headers: " + userId +" : " +deviceId);
	        }
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
