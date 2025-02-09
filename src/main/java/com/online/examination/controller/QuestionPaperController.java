package com.online.examination.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.online.examination.dto.QuestionPaperDto;
import com.online.examination.response.Response;
import com.online.examination.service.QuestionPaperService;


@CrossOrigin
@RestController
@RequestMapping("questionPaper")
public class QuestionPaperController {
	
	@Autowired
	private QuestionPaperService questionPaperService;

	@PostMapping("createQuestionPaper")
	public ResponseEntity<Response> createQuestionPaper(@RequestBody QuestionPaperDto dto) {
		Response response = new Response();
		response.succeed();
		questionPaperService.createQuestionPaper(dto);
		return ResponseEntity.ok().body(response);
	}
	
	@PostMapping("getExam")
	public ResponseEntity<Response> getExam(@RequestBody QuestionPaperDto dto) {
		Response response = new Response();
		response.succeed();
		response.setData(questionPaperService.getExam(dto));
		return ResponseEntity.ok().body(response);
	}
	
	
	
}
