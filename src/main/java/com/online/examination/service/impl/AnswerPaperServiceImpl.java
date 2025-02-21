package com.online.examination.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.examination.dto.AnswerDto;
import com.online.examination.dto.AnswerPaperDto;
import com.online.examination.dto.FinalResultDto;
import com.online.examination.dto.QuestionPaperDto;
import com.online.examination.dto.ResultPaperDto;
import com.online.examination.entity.AnswerPaper;
import com.online.examination.entity.QuestionPaper;
import com.online.examination.exception.AlreadySubmittedTestException;
import com.online.examination.exception.InvalidArgumentException;
import com.online.examination.repository.AnswerPaperRepo;
import com.online.examination.repository.QuestionPaperRepo;
import com.online.examination.service.AnswerPaperService;

import jakarta.transaction.Transactional;

@Service
public class AnswerPaperServiceImpl implements AnswerPaperService {

	@Autowired
	private AnswerPaperRepo answerPaperRepo;
	
	@Autowired 
	private QuestionPaperRepo questionPaperRepo;

	@Override
	@Transactional
	public AnswerPaperDto saveAnswePaper(AnswerPaperDto dto) {
		if(this.isAlreadySubmitTest(dto)) {
			throw new AlreadySubmittedTestException();
		}
		AnswerPaper answerPaper = new AnswerPaper();
		answerPaper.setBatch(dto.getBatch());
		answerPaper.setEndTime(dto.getEndTime());
		answerPaper.setOrgCode(dto.getOrgCode());
		answerPaper.setStratTime(dto.getStratTime());
		answerPaper.setSubjectName(dto.getSubjectName());
		answerPaper.setTeacherName(dto.getTeacherName());
		answerPaper.setQuestionId(dto.getQuestionId());
		answerPaper.setUserId(dto.getUserId());
		this.convertMapToJsonString(dto, answerPaper);
		answerPaperRepo.save(answerPaper);

		return dto;
	}
	
	private Boolean isAlreadySubmitTest(AnswerPaperDto dto) {
		AnswerPaper answerPaperList = answerPaperRepo.findByUserIdAndQuestionId(dto.getUserId(), dto.getQuestionId());
		if(ObjectUtils.isEmpty(answerPaperList)) {
			return false;
		}
		return true;
	}

	private void convertMapToJsonString(AnswerPaperDto dto,AnswerPaper answerPaper) {
		ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonString = mapper.writeValueAsString(dto.getAnswerPaper());
            answerPaper.setQuestionList(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}


	@Override
	public List<AnswerPaperDto> getAllTest(AnswerPaperDto dto) {
		List<AnswerPaperDto> dataList = new ArrayList<>();
		List<AnswerPaper> answerPaperList = answerPaperRepo.findByUserId(dto.getUserId());
		if (ObjectUtils.isNotEmpty(answerPaperList)) {
			for (AnswerPaper answerPaper : answerPaperList) {
					dataList.add(this.convertEntityIntoDtowithoutAnswer(answerPaper));
			}

		}
		return dataList;
	}
	
	private AnswerPaperDto convertEntityIntoDtowithoutAnswer(AnswerPaper answerPaper) {
		AnswerPaperDto data = new AnswerPaperDto();
		data.setBatch(answerPaper.getBatch());
		data.setEndTime(answerPaper.getEndTime());
		data.setOrgCode(answerPaper.getOrgCode());
		data.setStratTime(answerPaper.getStratTime());
		data.setSubjectName(answerPaper.getSubjectName());
		data.setTeacherName(answerPaper.getTeacherName());
		data.setQuestionId(answerPaper.getQuestionId());
		data.setUserId(answerPaper.getUserId());
		
		return data;

	}

	private AnswerPaperDto convertEntityIntoDto(AnswerPaper answerPaper) {
		AnswerPaperDto data = new AnswerPaperDto();
		List<AnswerDto> questionList = new ArrayList<>();
		String input = answerPaper.getQuestionList();

		
		try {
			ObjectMapper mapper = new ObjectMapper();
			questionList = mapper.readValue(input, new TypeReference<List<AnswerDto>>() {});
		}catch (IOException e) {
            e.printStackTrace();
        }
		

		data.setBatch(answerPaper.getBatch());
		data.setEndTime(answerPaper.getEndTime());
		data.setOrgCode(answerPaper.getOrgCode());
		data.setAnswerPaper(questionList);
		data.setStratTime(answerPaper.getStratTime());
		data.setSubjectName(answerPaper.getSubjectName());
		data.setTeacherName(answerPaper.getTeacherName());

		return data;

	}

	@Override
	public FinalResultDto getResult(AnswerPaperDto dto) {
		FinalResultDto finalResultDto = new FinalResultDto();
		List<AnswerDto> result = new ArrayList<>();
		
		Integer correct =0;
		Integer incorrect = 0;
		AnswerPaper answerPaper = answerPaperRepo.findByUserIdAndQuestionId(dto.getUserId(), dto.getQuestionId());
		
		if(ObjectUtils.isEmpty(answerPaper)) {
			return finalResultDto;
		}
		
		AnswerPaperDto answerPaperDto = this.convertEntityIntoDto(answerPaper);
		
		if(ObjectUtils.isNotEmpty(answerPaperDto)) {
			for(AnswerDto answer : answerPaperDto.getAnswerPaper()) {
				result.add(answer);
				if(answer.getCorrectAnswer().equals(answer.getUserAnswer())) {
					correct++;
				}else {
					incorrect++;
				}
			} 
		}
		
		
		
		
		finalResultDto.setFinalResult(result);
		finalResultDto.setTotalQuestion(answerPaperDto.getAnswerPaper().size());
		finalResultDto.setCorrectAnswer(correct);
		finalResultDto.setIncorrectAnswer(incorrect);
		finalResultDto.setTotalMarks(answerPaperDto.getAnswerPaper().size() - incorrect);

		return finalResultDto;
		
	}
	
	
	private QuestionPaperDto convertEntityIntoDto(QuestionPaper questionPaper, Boolean isFrontEnd) {
		QuestionPaperDto data = new QuestionPaperDto();
		String input = questionPaper.getQuestionList();

		
		ObjectMapper mapper = new ObjectMapper();
		List<AnswerDto> tempMap = new ArrayList<>();
		try {
			tempMap = mapper.readValue(input,
			         new TypeReference<List<AnswerDto>>() {});
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}


		data.setBatch(questionPaper.getBatch());
		data.setEndTime(questionPaper.getEndTime());
		data.setOrgCode(questionPaper.getOrgCode());
		data.setQuestionList(tempMap);
		data.setStratTime(questionPaper.getStratTime());
		data.setSubjectName(questionPaper.getSubjectName());
		data.setTeacherName(questionPaper.getTeacherName());
		data.setQuestionId(questionPaper.getQuestionId());
		data.setExamDuration(questionPaper.getExamDuration());

		return data;

	}



}
