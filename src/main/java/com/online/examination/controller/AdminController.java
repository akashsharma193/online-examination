package com.online.examination.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.online.examination.dto.UserDto;
import com.online.examination.response.Response;
import com.online.examination.service.AdminService;
import com.online.examination.service.UserService;


@CrossOrigin
@RestController
@RequestMapping("admin")
public class AdminController {
	
	@Autowired
	private AdminService adminService;

	@PostMapping("registration")
	public ResponseEntity<Response> saveUser(@RequestBody UserDto dto) {
		Response response = new Response();
		response.succeed();
		response.setData(adminService.saveUser(dto));
		return ResponseEntity.ok().body(response);
	}
	
	@PostMapping("login")
	public ResponseEntity<Response> login( @RequestBody UserDto dto) {
		Response response = new Response();
		response.succeed();
		response.setData(adminService.login(dto));
		return ResponseEntity.ok().body(response);
	}
	
	@PostMapping("resetPassword")
	public ResponseEntity<Response> resetPassword(@RequestBody UserDto dto) {
		Response response = new Response();
		response.succeed();
		adminService.resetPassword(dto);
		return ResponseEntity.ok().body(response);
	}
	
	@PostMapping("enableDisableUser")
	public ResponseEntity<Response> enableDisableUser(@RequestBody UserDto dto) {
		Response response = new Response();
		response.succeed();
		adminService.enableDisableUser(dto);
		return ResponseEntity.ok().body(response);
	}
	
	
	@PostMapping("logOut")
	public ResponseEntity<Response> logOut(@RequestHeader(value = "deviceId") String deviceId, @RequestBody UserDto dto) {
		Response response = new Response();
		response.succeed();
		adminService.logOut(dto, deviceId);
		return ResponseEntity.ok().body(response);
	}
	
	@PostMapping("forceLogOutRequest")
	public ResponseEntity<Response> forceLogOutRequest(@RequestBody UserDto dto) {
		Response response = new Response();
		response.succeed();
		adminService.forceLogOutRequest(dto);
		return ResponseEntity.ok().body(response);
	}
	
	
	
}
