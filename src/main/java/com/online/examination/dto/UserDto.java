package com.online.examination.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

	String name;
	
	String mobile;
	
	String email;
	
	String batch;
	
	String password;
	
	String orgCode;	
	
	Boolean isActive;
}
