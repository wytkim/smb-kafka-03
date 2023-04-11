/*
 * Copyright (c) 2015 SM band, Inc.
 * All right reserved.
 *
 * This software is the confidential and proprietary information of SM band
 * , Inc. You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with SM band.
 *
 */
package com.smband.kafka.dao;

import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.apache.ibatis.executor.BatchExecutorException;
import org.apache.ibatis.executor.BatchResult;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.smband.kafka.model.SmsHistVO;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 개요:
 * </pre>
 * @author ytkim
 * @create 2023. 4. 9.
 * @version 
 * @since 
 */
@Slf4j
@Repository
public class KafkaProcessDao {

	@Autowired
	@Qualifier("sqlSessionBatchTemplate")
	private SqlSessionTemplate sqlSessionBatchTemplate;
	
	@PostConstruct
	private void initAndShowProperties() {
		log.info("sqlSession ExecutorType: {}", sqlSessionBatchTemplate.getExecutorType());
	}
	
	/** 이력 목록 입력 */
	public void insertSmsHistList(List<SmsHistVO> list) {
		
		try {
			for(SmsHistVO hist: list) {
				sqlSessionBatchTemplate.insert("com.smband.kafka.mapper.KafkaProcessMapper.insertSmsHist", hist);
			}

			List<BatchResult> results = sqlSessionBatchTemplate.flushStatements();
			log.info("batch result size: {}", results.size());
			if(!results.isEmpty()) {
				int sum = IntStream.of(results.get(0).getUpdateCounts()).sum();
				log.info("update count-{}, sum count-{}", results.get(0).getUpdateCounts().length, sum);
			}
			//		sqlSessionBatchTemplate.flushStatements()
		}catch(BatchExecutorException e) {
			List<BatchResult> results = e.getSuccessfulBatchResults();
			log.info("exception batch result size: {}", results.size());
		}catch(DataIntegrityViolationException e) {
			Throwable ex = e.getCause();
			while(!(ex instanceof BatchExecutorException)) {
				ex = ex.getCause();
				if(ex==null) {
					break;
				}
				log.info("search exception : {}", ex.getClass());
			}
			
			if(ex!=null) {
				log.info("exception violation : {}", ex.getClass());
			}
//			if(e.contains(BatchExecutorException.class)) {
//				//e.
//				
//			}
		}catch(RuntimeException e) {
			log.error("batch exception class name: {}", e.getClass());
			 //org.springframework.dao.DataIntegrityViolationException
			//throw e;
			
		}
	}
}
