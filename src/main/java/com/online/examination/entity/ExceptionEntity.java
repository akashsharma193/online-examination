package com.online.examination.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "exception")
public class ExceptionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@Column(name = "statusCode", nullable = false)
	private String statusCode;

	@Column(name = "error", nullable = false)
	private String error;

	@Column(name = "trac", columnDefinition = "text", nullable = false)
	private String trac;
}
