package com.online.examination.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResultPaperDto {
	
	String question;

	AnswerDto answerDto;
	
	String yourAnser;
	
	Boolean status;
	
	
}
