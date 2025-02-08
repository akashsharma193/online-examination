package com.online.examination.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionData {

	String question;
	
	List<String> answer;
}
