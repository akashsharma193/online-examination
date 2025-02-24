package com.online.examination.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.examination.dto.AnswerDto;
import com.online.examination.dto.QuestionPaperDto;
import com.online.examination.entity.AnswerPaper;
import com.online.examination.entity.QuestionPaper;
import com.online.examination.repository.AnswerPaperRepo;
import com.online.examination.repository.QuestionPaperRepo;
import com.online.examination.service.AnswerPaperService;
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
		String questionId = "";
		questionId = questionId.concat(orgCode).concat(batch).concat(subjectName).concat(date);
		QuestionPaper questionPaper = questionPaperRepo.findByQuestionId(questionId);
		if (ObjectUtils.isNotEmpty(questionPaper)) {
			questionId = this.createQuestionId(orgCode, batch, subjectName,
					LocalDate.now().toString() + "_" + RandomStringUtils.randomAlphanumeric(2));
		}
		return questionId;
	}

	@Override
	public List<QuestionPaperDto> getExam(QuestionPaperDto dto) {
		List<QuestionPaperDto> dataList = new ArrayList<>();
		List<QuestionPaper> questionPaperList = questionPaperRepo.findByOrgCodeAndBatch(dto.getOrgCode(),
				dto.getBatch());
		if (ObjectUtils.isNotEmpty(questionPaperList)) {
			LocalDateTime localDateTime = LocalDateTime.now();
			ZoneId istZoneId = ZoneId.of("Asia/Kolkata");
			ZonedDateTime istZonedDateTime = localDateTime.atZone(istZoneId);
			for (QuestionPaper questionPaper : questionPaperList) {
				if (isTimeBetween(istZonedDateTime.toLocalDateTime(), questionPaper.getStratTime(),
						questionPaper.getEndTime())) {
					dataList.add(convertEntityIntoDto(questionPaper, false));
				}
			}

		}
		CopyOnWriteArrayList<QuestionPaperDto> list = new CopyOnWriteArrayList<>(dataList);
		if (ObjectUtils.isNotEmpty(dataList)) {
			if (StringUtils.isNoneBlank(dto.getUserId())) {
				List<AnswerPaper> answerPaperList = answerPaperRepo.findByUserId(dto.getUserId());
				if (!CollectionUtils.isEmpty(answerPaperList)) {
					for (AnswerPaper answerPaper : answerPaperList) {
						for (QuestionPaperDto questionPaperDto : list) {
							if (answerPaper.getQuestionId().equals(questionPaperDto.getQuestionId())) {
								list.remove(questionPaperDto);
							}

						}
					}
				}
			}
		}

		return list;
	}

	private QuestionPaperDto convertEntityIntoDto(QuestionPaper questionPaper, Boolean isFrontEnd) {
		QuestionPaperDto data = new QuestionPaperDto();
		String input = questionPaper.getQuestionList();

		ObjectMapper mapper = new ObjectMapper();
		List<AnswerDto> tempMap = new ArrayList<>();
		try {
			tempMap = mapper.readValue(input, new TypeReference<List<AnswerDto>>() {
			});
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		tempMap = tempMap.stream().peek(x -> x.setCorrectAnswer(null)).collect(Collectors.toList());

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
		return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
	}

}
