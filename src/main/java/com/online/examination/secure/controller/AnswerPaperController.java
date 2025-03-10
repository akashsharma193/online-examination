package com.online.examination.secure.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.online.examination.dto.AnswerPaperDto;
import com.online.examination.response.Response;
import com.online.examination.service.AnswerPaperService;

@CrossOrigin
@RestController
@RequestMapping("answerPaper")
public class AnswerPaperController {
	
	@Autowired
	private AnswerPaperService answerPaperService;
	
	@PostMapping("saveAnswePaper")
	public ResponseEntity<Response> saveAnswePaper(@RequestBody AnswerPaperDto dto) {
		Response response = new Response();
		response.succeed();
		answerPaperService.saveAnswePaper(dto);
		return ResponseEntity.ok().body(response);
	}
	
	@PostMapping("getAllTest")
	public ResponseEntity<Response> getAllTest(@RequestBody AnswerPaperDto dto) {
		Response response = new Response();
		response.succeed();
		response.setData(answerPaperService.getAllTest(dto));
		return ResponseEntity.ok().body(response);
	}
	
	@PostMapping("getResult")
	public ResponseEntity<Response> getResult(@RequestBody AnswerPaperDto dto) {
		Response response = new Response();
		response.succeed();
		response.setData(answerPaperService.getResult(dto));
		return ResponseEntity.ok().body(response);
	}

	
	
	
}
