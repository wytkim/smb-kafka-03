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
package com.smband.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.smband.commons.util.StringUtil;
import com.smband.kafka.model.SmsBodyVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 개요:
 * </pre>
 * @author ytkim
 * @create 2023. 4. 4.
 * @version 
 * @since 
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsProducerService {
	private final KafkaTemplate<String, SmsBodyVO> kafkaSmsSendTemplate;
	
	@Value("${smband.topics.sms-send-data}")
	private String smbTopic = "sms-send-data";
	
	
	public void sendSms(final SmsBodyVO smsBody) {
		Message<SmsBodyVO> message = MessageBuilder
				.withPayload(smsBody)
				.setHeader(KafkaHeaders.TOPIC, smbTopic)
				//.setHeader(KafkaHeaders.KEY, key)
				.build();
		
		ListenableFuture<SendResult<String, SmsBodyVO>> future = kafkaSmsSendTemplate.send(message);
		future.addCallback(new ListenableFutureCallback<SendResult<String, SmsBodyVO>>() {

			@Override
			public void onSuccess(SendResult<String, SmsBodyVO> result) {
				// 성공 처리.
				log.info("send success: {}", smsBody);
			}

			@Override
			public void onFailure(Throwable ex) {
				// 실패 처리.
				log.warn("send fail data:{}, exception:{}", smsBody, StringUtil.exceptionMessage(ex));
			}
		});
		
	}
}
