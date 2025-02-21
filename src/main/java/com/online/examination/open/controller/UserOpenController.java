package com.online.examination.open.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.online.examination.dto.UserDto;
import com.online.examination.response.Response;
import com.online.examination.service.UserService;

@CrossOrigin
@RestController
@RequestMapping("open-user")
public class UserOpenController {
	
	@Autowired
	private UserService userService;

	@GetMapping("forceLogOut/{userId}/{sessionId}")
	public ResponseEntity<String> forceLogOut(@PathVariable String userId, @PathVariable String sessionId) {
		String result =  userService.forceLogOut(userId, sessionId);
		
		String htmlContent = "<html><body><h1>" +result + "</h1></body></html>";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);

        return new ResponseEntity<>(htmlContent, headers, HttpStatus.OK);
	}
	
	@GetMapping("activateUser/{userId}")
	public ResponseEntity<String> activateUser(@PathVariable String userId) {
		String result = userService.activateUser(userId);

		String htmlContent = "<html><body><h1>" + result + "</h1></body></html>";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_HTML);

		return new ResponseEntity<>(htmlContent, headers, HttpStatus.OK);
	}
	
	@PostMapping("checkLoggedInUser")
	public ResponseEntity<Response> checkLoggedInUser(@RequestHeader(value = "deviceId") String deviceId) {
		Response response = new Response();
		response.setData(userService.checkLoggedInUser(deviceId));
		response.succeed();
		return ResponseEntity.ok().body(response);
	}
}
