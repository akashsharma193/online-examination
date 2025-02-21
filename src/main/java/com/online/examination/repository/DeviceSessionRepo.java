package com.online.examination.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.examination.entity.DeviceSession;

@Repository
public interface DeviceSessionRepo extends JpaRepository<DeviceSession, Long>{
	
	Optional<DeviceSession> findByDeviceIdAndUserId(String deviceId, String userId);

	DeviceSession findByUserIdAndIsActive(String userId, boolean isActive);
	
	DeviceSession findByDeviceIdAndIsActive(String userId, boolean isActive);

}
