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
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.examination.dto.AnswerDto;
import com.online.examination.dto.QuestionPaperDto;
import com.online.examination.entity.QuestionPaper;
import com.online.examination.repository.QuestionPaperRepo;
import com.online.examination.service.QuestionPaperService;

import jakarta.transaction.Transactional;

@Service
public class QuestionPaperServiceImpl implements QuestionPaperService {

	@Autowired
	private QuestionPaperRepo questionPaperRepo;

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
		questionPaper.setQuestionId(this.createQuestionId(dto.getOrgCode(), dto.getBatch(), dto.getSubjectName(), LocalDate.now().toString()));
		questionPaper.setIsActive(true);
		this.convertMapToJsonString(dto, questionPaper);
		questionPaperRepo.save(questionPaper);
		return  convertEntityIntoDto(questionPaper, true);
	}

	private void convertMapToJsonString(QuestionPaperDto dto,QuestionPaper questionPaper) {
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
		if(ObjectUtils.isNotEmpty(questionPaper)) {
			questionId = this.createQuestionId(orgCode, batch, subjectName, LocalDate.now().toString() +"_"  + RandomStringUtils.randomAlphanumeric(2));
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
				if (isTimeBetween(istZonedDateTime.toLocalDateTime(), questionPaper.getStratTime(), questionPaper.getEndTime())) {
					dataList.add(convertEntityIntoDto(questionPaper, false));
				}
			}

		}
		return dataList;
	}

	private QuestionPaperDto convertEntityIntoDto(QuestionPaper questionPaper, Boolean isFrontEnd) {
		QuestionPaperDto data = new QuestionPaperDto();
		Map<String, List<AnswerDto>> questionList = new HashMap<>();
		String input = questionPaper.getQuestionList();

		
		ObjectMapper mapper = new ObjectMapper();

        try {
            Map<String, List<Map<String, Object>>> tempMap = mapper.readValue(input,
                    new TypeReference<Map<String, List<Map<String, Object>>>>() {});

            // Convert temporary map to the desired map structure
            questionList = tempMap.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(obj -> {
                        AnswerDto answerDto = new AnswerDto();
                        answerDto.setOption((List<String>) obj.get("option"));
                        if(BooleanUtils.isTrue(isFrontEnd)) {
                        	answerDto.setCorrectAnswer((String) obj.get("correctAnswer"));
                        }
                        
                        return answerDto;
                    }).collect(Collectors.toList())));

        } catch (IOException e) {
            e.printStackTrace();
        }

		data.setBatch(questionPaper.getBatch());
		data.setEndTime(questionPaper.getEndTime());
		data.setOrgCode(questionPaper.getOrgCode());
		data.setQuestionList(questionList);
		data.setStratTime(questionPaper.getStratTime());
		data.setSubjectName(questionPaper.getSubjectName());
		data.setTeacherName(questionPaper.getTeacherName());
		data.setQuestionId(questionPaper.getQuestionId());

		return data;

	}

	public static boolean isTimeBetween(LocalDateTime currentTime, LocalDateTime startTime, LocalDateTime endTime) {
		return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
	}
	

}
