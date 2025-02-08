package com.online.examination.service;

import java.util.List;

import com.online.examination.dto.QuestionPaperDto;

public interface QuestionPaperService {

	QuestionPaperDto createQuestionPaper(QuestionPaperDto dto);

	List<QuestionPaperDto> getExam(QuestionPaperDto dto);

}
