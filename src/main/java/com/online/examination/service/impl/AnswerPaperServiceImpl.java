package com.online.examination.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.online.examination.dto.AnswerPaperDto;
import com.online.examination.entity.AnswerPaper;
import com.online.examination.repository.AnswerPaperRepo;
import com.online.examination.service.AnswerPaperService;

@Service
public class AnswerPaperServiceImpl implements AnswerPaperService {

	@Autowired
	private AnswerPaperRepo answerPaperRepo;

	@Override
	public AnswerPaperDto saveAnswePaper(AnswerPaperDto dto) {
		AnswerPaper answerPaper = new AnswerPaper();
		answerPaper.setBatch(dto.getBatch());
		answerPaper.setEndTime(dto.getEndTime());
		answerPaper.setOrgCode(dto.getOrgCode());
		answerPaper.setQuestionList(StringUtils.join(dto.getQuestionList()));
		answerPaper.setStratTime(dto.getStratTime());
		answerPaper.setSubjectName(dto.getSubjectName());
		answerPaper.setTeacherName(dto.getTeacherName());
		answerPaper.setQuestionId(dto.getQuestionId());
		answerPaper.setUserId(dto.getUserId());
		answerPaperRepo.save(answerPaper);

		return convertEntityIntoDto(answerPaper);
	}


	@Override
	public List<AnswerPaperDto> getAnswerPaper(AnswerPaperDto dto) {
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
		Map<String, List<String>> questionList = new HashMap<>();
		String input = answerPaper.getQuestionList();

		// Manually parse the input string
		String[] entries = input.substring(1, input.length() - 1).split("], ");
		for (String entry : entries) {
			String[] keyValue = entry.split("=\\[");
			String key = keyValue[0].trim();
			List<String> values = Arrays.asList(keyValue[1].replace("]", "").split(", "));
			questionList.put(key, values);
		}

		data.setBatch(answerPaper.getBatch());
		data.setEndTime(answerPaper.getEndTime());
		data.setOrgCode(answerPaper.getOrgCode());
		data.setQuestionList(questionList);
		data.setStratTime(answerPaper.getStratTime());
		data.setSubjectName(answerPaper.getSubjectName());
		data.setTeacherName(answerPaper.getTeacherName());

		return data;

	}



}
