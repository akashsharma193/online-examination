package com.online.examination.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "deviceSession")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSession {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	String userId;
	
	String deviceId;
	
	Boolean isActive;
	
	String sessionId;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
	LocalDateTime lastLoginTime;

}
