package com.online.examination.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.online.examination.entity.ComplianceEntity;
import com.online.examination.response.Response;
import com.online.examination.service.ComplianceService;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@RequestMapping("compliance")
public class ComplianceController {

	@Autowired
	private ComplianceService complianceService;
	
	@PostMapping("saveCompliance")
	public ResponseEntity<Response> saveCompliance(@RequestBody ComplianceEntity dto) {
		Response response = new Response();
		response.succeed();
		response.setData(complianceService.saveCompliance(dto));
		return ResponseEntity.ok().body(response);
	}
	
	@GetMapping("getCompliance")
	public ResponseEntity<Response> getCompliance() {
		Response response = new Response();
		response.succeed();
		response.setData(complianceService.getCompliance());
		return ResponseEntity.ok().body(response);
	}
}
