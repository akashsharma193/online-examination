package com.online.examination.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerPaperDto {

	Map<String, String> answerPaper;
	
	String subjectName; 
	
	String teacherName; 
	
	String orgCode;
	
	String batch;
	
	String userId;
	
	String questionId;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	LocalDateTime stratTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	LocalDateTime endTime;
	
}
