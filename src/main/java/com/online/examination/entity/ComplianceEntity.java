package com.online.examination.entity;

import com.online.examination.enums.ActivationMode;

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
@Table(name = "compliance")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComplianceEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	String compliance;
	
	Boolean isActive;

}
