package com.online.examination.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinalResultDto {
	
	List<AnswerDto> finalResult;
	
	Integer totalQuestion;
	
	Integer correctAnswer;
	
	Integer incorrectAnswer;
	
	Integer totalMarks;
	
	
}
