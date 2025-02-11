package com.online.examination.service;

import java.util.List;

import com.online.examination.dto.AnswerPaperDto;
import com.online.examination.dto.FinalResultDto;

public interface AnswerPaperService {

	AnswerPaperDto saveAnswePaper(AnswerPaperDto dto);

	List<AnswerPaperDto> getAllTest(AnswerPaperDto dto);

	FinalResultDto getResult(AnswerPaperDto dto);

}
