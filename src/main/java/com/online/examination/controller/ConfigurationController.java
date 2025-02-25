package com.online.examination.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.online.examination.entity.Configuration;
import com.online.examination.response.Response;
import com.online.examination.service.ConfigurationService;

@CrossOrigin
@RestController
@RequestMapping("configuration")
public class ConfigurationController {

	@Autowired
	private ConfigurationService configurationService;
	
	@PostMapping("saveConfiguration")
	public ResponseEntity<Response> saveConfiguration(@RequestBody Configuration dto) {
		Response response = new Response();
		response.succeed();
		response.setData(configurationService.saveUser(dto));
		return ResponseEntity.ok().body(response);
	}
	
	@GetMapping("getConfiguration")
	public ResponseEntity<Response> getConfiguration() {
		Response response = new Response();
		response.succeed();
		response.setData(configurationService.getConfiguration());
		return ResponseEntity.ok().body(response);
	}
}
