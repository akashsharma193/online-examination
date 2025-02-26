package com.online.examination.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
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
@Table(name = "ipratelimit")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IpRateLimit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	String ipAddress;

	String deviceId;

	Integer failCount;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	LocalDateTime limitEndTime;
	
}
