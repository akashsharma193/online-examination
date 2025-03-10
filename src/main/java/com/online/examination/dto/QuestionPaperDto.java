package com.online.examination.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class QuestionPaperDto {

	List<AnswerDto> questionList;
	
	String subjectName; 
	
	String teacherName; 
	
	String orgCode;
	
	String batch;
	
	String userId;
	
	String questionId;
	
	@NotEmpty(message = "user name is required")
	String examDuration;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	LocalDateTime startTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	LocalDateTime endTime;
	
}


