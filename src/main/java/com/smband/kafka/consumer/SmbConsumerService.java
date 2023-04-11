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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.smband.kafka.model.SmsBodyVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 개요:
 * </pre>
 * @author ytkim
 * @create 2023. 3. 31.
 * @version 
 * @since 
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SmbConsumerService {
	
	private final SmbAsyncTask asyncTask;
	//private String groupId = "smb-group";
	
	@KafkaListener(topics = {"${smband.topics.users-registrations}"}, groupId = "${smband.kafka.group-id}", containerFactory = "smbKafkaListenerContainerFactory")
	public void consume(String message) throws InterruptedException {
		log.info("consumer1 receive message: {}", message);
		
		//new Exception().printStackTrace();
		//Thread.sleep(3000);
	}
	
	@KafkaListener(topics = {"${smband.topics.users-registrations}"} , groupId = "${smband.kafka.group-id}", containerFactory = "smbKafkaListenerContainer2Factory")
	public void consume2(String message) throws InterruptedException {
		//log.info("consumer2 receive message: {}", messages.stream().collect(Collectors.joining("\n")));
		log.info("consumer2 receive message: {}", message);
		//new Exception().printStackTrace();
		
		//Thread.sleep(4000);
	}
	
	private final AtomicInteger atomicIndex = new AtomicInteger(1);
	
	@KafkaListener(topics = {"${smband.topics.sms-send-data}"}, groupId="${smband.kafka.group-id}", containerFactory="smbSendListenerContainerFactory")
	public void smsConsume(List<SmsBodyVO> smsBodyList) throws InterruptedException {
		log.info("smsConsumer receive body: {}, list size: {}", smsBodyList.get(0), smsBodyList.size());
		int index = atomicIndex.getAndIncrement();
		asyncTask.asyncProcess("receive smsBody "+ index);
		log.info("after async process: "+index);
	}
}
