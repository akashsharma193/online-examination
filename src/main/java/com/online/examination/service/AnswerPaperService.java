package com.online.examination.service;

import java.util.List;

import com.online.examination.dto.AnswerPaperDto;

public interface AnswerPaperService {

	AnswerPaperDto saveAnswePaper(AnswerPaperDto dto);

	List<AnswerPaperDto> getAnswerPaper(AnswerPaperDto dto);

}
