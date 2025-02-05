package com.online.examination.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.examination.entity.User;

@Repository
public interface UserRepo extends JpaRepository<User, Object> {

    User findByMobileOrEmail(String mobile, String email);

    User findByMobileOrEmailAndIsActive(String mobile, String email, boolean isActive);

}

