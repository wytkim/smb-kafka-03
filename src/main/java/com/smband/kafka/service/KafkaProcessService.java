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
package com.smband.kafka.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.smband.kafka.dao.KafkaProcessDao;
import com.smband.kafka.mapper.KafkaProcessMapper;
import com.smband.kafka.model.SmsHistVO;

/**
 * <pre>
 * 개요:
 * </pre>
 * @author ytkim
 * @create 2023. 4. 9.
 * @version 
 * @since 
 */
@Service
public class KafkaProcessService {

	@Autowired
	private KafkaProcessDao processDao;
	@Autowired
	private KafkaProcessMapper processMapper;
	
	/** 이력 단건 입력 */
	public void insertSmsHist(SmsHistVO smsHist) {
		this.processMapper.insertSmsHist(smsHist);
	}
	
	/** 이력 목록 입력 */
	@Transactional(transactionManager = "tranManager", propagation = Propagation.REQUIRES_NEW)
	public void insertSmsHistList (List<SmsHistVO> list) {
		this.processDao.insertSmsHistList(list);
	}
}
