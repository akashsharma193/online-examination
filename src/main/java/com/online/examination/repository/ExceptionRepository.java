package com.online.examination.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.examination.entity.ExceptionEntity;


@Repository
public interface ExceptionRepository extends JpaRepository<ExceptionEntity, Object> {

}
