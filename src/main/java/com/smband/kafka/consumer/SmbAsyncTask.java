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
package com.smband.kafka.consumer;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 개요:
 * </pre>
 * @author ytkim
 * @create 2023. 4. 5.
 * @version 
 * @since 
 */
@Slf4j
@Component
public class SmbAsyncTask {

	@Async
	public void asyncProcess(String msg) throws InterruptedException {
		log.info("async process start: {}", msg);
		Thread.sleep(1000);
		log.info("async process end: {}", msg);
	}
}
