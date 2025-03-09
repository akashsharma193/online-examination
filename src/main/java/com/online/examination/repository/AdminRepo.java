package com.online.examination.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.examination.entity.Admin;
import com.online.examination.entity.User;

@Repository
public interface AdminRepo extends JpaRepository<Admin, Long> {

	Admin findByMobileOrEmail(String mobile, String email);

	Admin findByMobileOrEmailAndIsActive(String mobile, String email, boolean isActive);
    
	Admin findByUserId(String userId);

}

