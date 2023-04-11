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
package com.smband.kafka.schedule;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.smband.commons.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 개요:
 * </pre>
 * @author ytkim
 * @create 2023. 4. 3.
 * @version 
 * @since 
 */
@Slf4j
public class SmbProducerSchedule {
	
	@Autowired
	@Qualifier("kafkaTemplate")
	private KafkaTemplate<String, String> kafkaTemplate;
	
	private String smbTopic = "usersRegistrations";
	//, timeUnit = TimeUnit.SECONDS
	@Scheduled(fixedRate = 1000, initialDelay = 0)
	private void sendSchedule() {
		log.info("schedule time: {}", System.currentTimeMillis());
		sendMessage();
	}

	/**
	 * <pre>
	 * 1. 함수 개요: kafka topic에 메시지 발급.
	 * 2. 처리 내용:
	 * </pre>
	 * @Create 작성자:ytkim, 작성일시: 2023. 4. 3. 오후 3:06:20
	 */
	private void sendMessage() {
		final String value = getRandomStrMsg();
		final String key = "key-"+random.nextInt(2);
		Message<String> message = MessageBuilder
				.withPayload(value)
				.setHeader(KafkaHeaders.TOPIC, smbTopic)
				.setHeader(KafkaHeaders.MESSAGE_KEY, key)
				.build();
		ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(message);
		future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

			@Override
			public void onSuccess(SendResult<String, String> result) {
				log.info("send success: {}", value);
			}

			@Override
			public void onFailure(Throwable ex) {
				log.warn("send fail data:{}, exception:{}", value, StringUtil.exceptionMessage(ex));
			}
			
		});
		/*
		CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(message);
		future.whenComplete((result, ex)->{
			if(ex==null) {
				// 성공 처리.
				log.info("send success: {}", value);
			}else {
				// 실패 처리.
				log.warn("send fail data:{}, exception:{}", value, StringUtil.exceptionMessage(ex));
			}
		});
		*/
	}

	final String[] messages = {
			"이날 일본 도쿄증시에서 닛케이225지수는 전일 대비 0.39% 상승한 2만8149.89에 오전 거래를 마쳤다.", 
			"일본 대형 제조업체들의 체감 경기를 나타내는 단기경제관측조사(단칸) 업황 지수가 올해 1분기 1을 기록,",
			"중화권도 소폭의 오름세다. 중국 본토 상하이종합지수는 한국시간 오전 11시",
			"한편 대만 증시는 이날부터 5일까지 어린이날과 청명절로 휴장한",
			"이번주 국내 증시는 1·4분기 실적 발표에 투자자 관심이 집중될 전망이다. 미국 은행권을 둘러싼 불확실성은 여전하다. "};
	final Random random = new Random(System.currentTimeMillis());
	/**
	 * <pre>
	 * 1. 함수 개요: 
	 * 2. 처리 내용:
	 * </pre>
	 * @return
	 * @Create 작성자:ytkim, 작성일시: 2023. 4. 3. 오후 3:26:26
	 */
	private String getRandomStrMsg() {
		return messages[random.nextInt(messages.length)];
	}
}
