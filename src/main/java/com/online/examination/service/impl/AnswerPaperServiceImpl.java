package com.online.examination.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.examination.constants.Constant;
import com.online.examination.constants.ErrorMessage;
import com.online.examination.dto.AnswerDto;
import com.online.examination.dto.AnswerPaperDto;
import com.online.examination.dto.FinalResultDto;
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
			throw new AlreadySubmittedTestException(ErrorMessage.ALREADY_SUBMITTED_TEST);
		}
		QuestionPaper questionPaper = questionPaperRepo.findByQuestionId(dto.getQuestionId());
		if(ObjectUtils.isEmpty(questionPaper)) {
			throw new InvalidArgumentException(ErrorMessage.INVALID_ARGUMENT);
		}
		AnswerPaper answerPaper = new AnswerPaper();
		answerPaper.setBatch(questionPaper.getBatch());
		answerPaper.setEndTime(questionPaper.getEndTime());
		answerPaper.setOrgCode(questionPaper.getOrgCode());
		answerPaper.setStartTime(questionPaper.getStartTime());
		answerPaper.setSubjectName(questionPaper.getSubjectName());
		answerPaper.setTeacherName(questionPaper.getTeacherName());
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
		data.setStartTime(answerPaper.getStartTime());
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
		data.setStartTime(answerPaper.getStartTime());
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
		Integer unattempted = 0;
		AnswerPaper answerPaper = answerPaperRepo.findByUserIdAndQuestionId(dto.getUserId(), dto.getQuestionId());
		
		if(ObjectUtils.isEmpty(answerPaper)) {
			return finalResultDto;
		}
		
		AnswerPaperDto answerPaperDto = this.convertEntityIntoDto(answerPaper);
		
		if(ObjectUtils.isNotEmpty(answerPaperDto)) {
			for(AnswerDto answer : answerPaperDto.getAnswerPaper()) {
				
				
				if(StringUtils.isBlank(answer.getUserAnswer())) {
					unattempted++;
					answer.setColor(Constant.YELLOW);
				}
				else if(answer.getCorrectAnswer().equals(answer.getUserAnswer())) {
					correct++;
					answer.setColor(Constant.GREEN);
				}else {
					incorrect++;
					answer.setColor(Constant.RED);
				}
				result.add(answer);
			} 
		}
		
		finalResultDto.setFinalResult(result);
		finalResultDto.setTotalQuestion(answerPaperDto.getAnswerPaper().size());
		finalResultDto.setCorrectAnswer(correct);
		finalResultDto.setIncorrectAnswer(incorrect);
		finalResultDto.setTotalMarks(answerPaperDto.getAnswerPaper().size() - incorrect);
		finalResultDto.setUnAttempted(unattempted);

		return finalResultDto;
		
	}
	
}
