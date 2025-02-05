package com.online.examination.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "student")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	String name;

	String mobile;

	String email;

	String batch;

	String password;

	String orgCode;
	
	Boolean isActive;

}
