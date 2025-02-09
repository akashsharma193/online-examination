package com.online.examination.secure.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.online.examination.service.UserService;

@CrossOrigin
@RestController
@RequestMapping("secure-user")
public class UserSecureController {
	
	@Autowired
	private UserService userService;

	
	
	
}
