package com.online.examination.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.examination.entity.Configuration;

@Repository
public interface ConfigurationRepo extends JpaRepository<Configuration, Long>{

}
