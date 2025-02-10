package com.online.examination.service;

import java.util.List;

import com.online.examination.dto.AnswerPaperDto;
import com.online.examination.dto.ResultPaperDto;

public interface AnswerPaperService {

	AnswerPaperDto saveAnswePaper(AnswerPaperDto dto);

	List<AnswerPaperDto> getAllTest(AnswerPaperDto dto);

	List<ResultPaperDto> getResult(AnswerPaperDto dto);

}
