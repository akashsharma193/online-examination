package com.online.examination.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.online.examination.entity.QuestionPaper;

public interface QuestionPaperRepo extends JpaRepository<QuestionPaper, Long>{

	List<QuestionPaper> findByOrgCodeAndBatch(String orgCode, String batch);

	QuestionPaper findByQuestionId(String questionId);

}
