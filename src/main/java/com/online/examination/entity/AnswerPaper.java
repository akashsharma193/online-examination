package com.online.examination.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "answerpaper")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerPaper {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(columnDefinition = "TEXT")
	String questionList;

	String subjectName;

	String teacherName;

	String orgCode;

	String batch;
	
	String questionId;
	
	String userId;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	LocalDateTime stratTime;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	LocalDateTime endTime;
	
}
