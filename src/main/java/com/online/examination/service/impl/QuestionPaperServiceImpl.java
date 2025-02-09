package com.online.examination.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.online.examination.dto.QuestionPaperDto;
import com.online.examination.entity.QuestionPaper;
import com.online.examination.repository.QuestionPaperRepo;
import com.online.examination.service.QuestionPaperService;

@Service
public class QuestionPaperServiceImpl implements QuestionPaperService {

	@Autowired
	private QuestionPaperRepo questionPaperRepo;

	@Override
	public QuestionPaperDto createQuestionPaper(QuestionPaperDto dto) {
		QuestionPaper questionPaper = new QuestionPaper();
		questionPaper.setBatch(dto.getBatch());
		questionPaper.setEndTime(dto.getEndTime());
		questionPaper.setOrgCode(dto.getOrgCode());
		questionPaper.setQuestionList(StringUtils.join(dto.getQuestionList()));
		questionPaper.setStratTime(dto.getStratTime());
		questionPaper.setSubjectName(dto.getSubjectName());
		questionPaper.setTeacherName(dto.getTeacherName());
		questionPaperRepo.save(questionPaper);

		return convertEntityIntoDto(questionPaper);
	}

	@Override
	public List<QuestionPaperDto> getExam(QuestionPaperDto dto) {
		List<QuestionPaperDto> dataList = new ArrayList<>();
		List<QuestionPaper> questionPaperList = questionPaperRepo.findByOrgCodeAndBatch(dto.getOrgCode(),
				dto.getBatch());
		if (ObjectUtils.isNotEmpty(questionPaperList)) {
			for (QuestionPaper questionPaper : questionPaperList) {
				if (isTimeBetween(LocalDateTime.now(), questionPaper.getStratTime(), questionPaper.getEndTime())) {
					dataList.add(convertEntityIntoDto(questionPaper));
				}
			}

		}
		return dataList;
	}

	private QuestionPaperDto convertEntityIntoDto(QuestionPaper questionPaper) {
		QuestionPaperDto data = new QuestionPaperDto();
		Map<String, List<String>> questionList = new HashMap<>();
		String input = questionPaper.getQuestionList();

		// Manually parse the input string
		String[] entries = input.substring(1, input.length() - 1).split("], ");
		for (String entry : entries) {
			String[] keyValue = entry.split("=\\[");
			String key = keyValue[0].trim();
			List<String> values = Arrays.asList(keyValue[1].replace("]", "").split(", "));
			questionList.put(key, values);
		}

		data.setBatch(questionPaper.getBatch());
		data.setEndTime(questionPaper.getEndTime());
		data.setOrgCode(questionPaper.getOrgCode());
		data.setQuestionList(questionList);
		data.setStratTime(questionPaper.getStratTime());
		data.setSubjectName(questionPaper.getSubjectName());
		data.setTeacherName(questionPaper.getTeacherName());

		return data;

	}

	public static boolean isTimeBetween(LocalDateTime currentTime, LocalDateTime startTime, LocalDateTime endTime) {
		return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
	}

}
