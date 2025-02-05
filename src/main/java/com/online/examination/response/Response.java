package com.online.examination.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.online.examination.enums.ErrorCode;
import com.online.examination.exception.CustomException;

public class Response {

	private Boolean success;

	private Object data;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MMM-yyyy HH:mm:ss")
	private Date timestamp;

	private String message;

	private String errorCode;

	@JsonInclude(Include.NON_EMPTY)
	private Object errorMeta;

	public Response() {
		this.timestamp = new Date();
	}

	public Response(Throwable exception) {
		this();
		this.fail();

		String errorCodeString = ErrorCode.UNKNOWN.code();
		this.setMessage(exception.getMessage());

		if (exception instanceof CustomException) {

			CustomException ex = (CustomException) exception;
			ErrorCode errorCode = ex.getErrorCode();

			errorCodeString = errorCode.code();
			this.setMessage(ex.getMesage());
		}

		this.setErrorCode(errorCodeString);

	}

	public Response(Throwable exception, String message) {
		this(exception);
		this.setMessage(message);
	}

	public Boolean getSuccess() {
		return this.success;
	}

	private void setSuccess(Boolean success) {
		this.success = success;
	}

	public Object getData() {
		return this.data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public Date getTimestamp() {
		return this.timestamp;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void fail() {
		this.setSuccess(Boolean.FALSE);
	}

	public void succeed() {
		this.setSuccess(Boolean.TRUE);
	}

	public void setErrorMeta(Object errorMeta) {
		this.errorMeta = errorMeta;
	}

	public Object getErrorMeta() {
		return this.errorMeta;
	}

}
