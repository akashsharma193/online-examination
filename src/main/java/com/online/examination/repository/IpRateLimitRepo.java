package com.online.examination.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.online.examination.entity.IpRateLimit;

public interface IpRateLimitRepo extends JpaRepository<IpRateLimit, Long>{

	IpRateLimit findByIpAddress(String ipAddress);

	void deleteAllByIpAddress(String ipAddress);

}
