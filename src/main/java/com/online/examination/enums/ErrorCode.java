package com.online.examination.enums;

public enum ErrorCode {

	USER_ALREADY_EXIST,

	DATA_NOT_EXIST,

	ROLE_DOES_NOT_EXIST,

	FIELD_ALREADY_EXIST,

	INVALID_ARGUMENT,

	USER_NOT_FOUND,

	ACCED_DENIED,

	INVALID_PASSWORD,

	DATA_ALREADY_EXIST,
	
	INACTIVE_USER,

	UNKNOWN;

	private final String prefix = "ONLINE-EXAMINATION.";

	public String code() {
		StringBuilder code = new StringBuilder();
		code.append(prefix).append(this);
		return code.toString();
	}

}
