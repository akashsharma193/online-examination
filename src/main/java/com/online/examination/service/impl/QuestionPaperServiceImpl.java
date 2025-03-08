package com.online.examination.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.examination.dto.AnswerDto;
import com.online.examination.dto.QuestionPaperDto;
import com.online.examination.entity.AnswerPaper;
import com.online.examination.entity.QuestionPaper;
import com.online.examination.repository.AnswerPaperRepo;
import com.online.examination.repository.QuestionPaperRepo;
import com.online.examination.service.QuestionPaperService;

import jakarta.transaction.Transactional;

@Service
public class QuestionPaperServiceImpl implements QuestionPaperService {

	@Autowired
	private QuestionPaperRepo questionPaperRepo;

	@Autowired
	private AnswerPaperRepo answerPaperRepo;

	@Override
	@Transactional
	public QuestionPaperDto createQuestionPaper(QuestionPaperDto dto) {
		QuestionPaper questionPaper = new QuestionPaper();
		questionPaper.setBatch(dto.getBatch());
		questionPaper.setEndTime(dto.getEndTime());
		questionPaper.setOrgCode(dto.getOrgCode());
		questionPaper.setStratTime(dto.getStratTime());
		questionPaper.setSubjectName(dto.getSubjectName());
		questionPaper.setTeacherName(dto.getTeacherName());
		questionPaper.setExamDuration(dto.getExamDuration());
		questionPaper.setQuestionId(this.createQuestionId(dto.getOrgCode(), dto.getBatch(), dto.getSubjectName(),
				LocalDate.now().toString()));
		questionPaper.setIsActive(true);
		this.convertMapToJsonString(dto, questionPaper);
		questionPaperRepo.save(questionPaper);
		return dto;// convertEntityIntoDto(questionPaper, true);
	}

	private void convertMapToJsonString(QuestionPaperDto dto, QuestionPaper questionPaper) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonString = mapper.writeValueAsString(dto.getQuestionList());
			questionPaper.setQuestionList(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String createQuestionId(String orgCode, String batch, String subjectName, String date) {
	    String questionId = String.join("", orgCode, batch, subjectName, date);
	    while (questionPaperRepo.findByQuestionId(questionId) != null) {
	        questionId = String.join("", orgCode, batch, subjectName,
	                LocalDate.now().toString(), "_", RandomStringUtils.randomAlphanumeric(2));
	    }
	    return questionId;
	}


	@Override
	public List<QuestionPaperDto> getExam(QuestionPaperDto dto) {
	    List<QuestionPaperDto> dataList = new ArrayList<>();
	    List<QuestionPaper> questionPaperList = questionPaperRepo.findByOrgCodeAndBatch(dto.getOrgCode(), dto.getBatch());

	    if (ObjectUtils.isNotEmpty(questionPaperList)) {

//	    	LocalDateTime localDateTime = LocalDateTime.now();
//			ZoneId istZoneId = ZoneId.of("Asia/Kolkata");
//			ZonedDateTime istDateTime = localDateTime.atZone(ZoneId.systemDefault()) // Convert LocalDateTime to ZonedDateTime using system default time zone
//                    .withZoneSameInstant(istZoneId); // Convert it to IST
			
			
//	        questionPaperList.stream()
//	            .filter(qp -> isTimeBetween(istDateTime.toLocalDateTime(), qp.getStratTime(), qp.getEndTime()))
//	            .forEach(qp -> dataList.add(convertEntityIntoDto(qp, false)));
	    }

	    if (ObjectUtils.isNotEmpty(dataList) && StringUtils.isNotBlank(dto.getUserId())) {
	        List<AnswerPaper> answerPaperList = answerPaperRepo.findByUserId(dto.getUserId());

	        if (!CollectionUtils.isEmpty(answerPaperList)) {
	            Set<String> answeredQuestionIds = answerPaperList.stream()
	                .map(AnswerPaper::getQuestionId)
	                .collect(Collectors.toSet());

	            dataList.removeIf(questionPaperDto -> answeredQuestionIds.contains(questionPaperDto.getQuestionId()));
	        }
	    }

	    return dataList;
	}


	private QuestionPaperDto convertEntityIntoDto(QuestionPaper questionPaper, Boolean isFrontEnd) {
	    QuestionPaperDto data = new QuestionPaperDto();
	    String input = questionPaper.getQuestionList();

	    ObjectMapper mapper = new ObjectMapper();
	    List<AnswerDto> tempMap = new ArrayList<>();
	    try {
	        tempMap = mapper.readValue(input, new TypeReference<List<AnswerDto>>() {});
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	    }

	   // tempMap.forEach(x -> x.setCorrectAnswer(null));

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


	public static boolean isTimeBetween(LocalDateTime currentTime, LocalDateTime startTime, LocalDateTime endTime) {
	    return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
	}


}
