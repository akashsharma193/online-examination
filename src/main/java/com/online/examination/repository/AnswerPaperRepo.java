package com.online.examination.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.online.examination.entity.AnswerPaper;

public interface AnswerPaperRepo extends JpaRepository<AnswerPaper, Long>{

	AnswerPaper findByQuestionId(String questionId);
	
	List<AnswerPaper> findByUserId(String userId);

}
