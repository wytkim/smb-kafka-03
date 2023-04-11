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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.smband.commons.util.StringUtil;
import com.smband.kafka.model.SmsBodyVO;

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
public class SmsSendProdSchedule {

	@Autowired
	@Qualifier("kafkaSmsSendTemplate")
	private KafkaTemplate<String, SmsBodyVO> kafkaTemplate;
	
	@Value("${smband.topics.sms-send-data}")
	private String smbTopic = "sms-send-data";
	//, timeUnit = TimeUnit.SECONDS
	@Scheduled(fixedRate = 5000, initialDelay = 0)
	private void sendSchedule() {
		//log.info("schedule time: {}", System.currentTimeMillis());
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
		final SmsBodyVO value = getRandomSmsBodyVO();
		//final String key = "key-"+random.nextInt(2);
		Message<SmsBodyVO> message = MessageBuilder
				.withPayload(value)
				.setHeader(KafkaHeaders.TOPIC, smbTopic)
				//.setHeader(KafkaHeaders.KEY, key)
				.build();
		
		ListenableFuture<SendResult<String, SmsBodyVO>> future = kafkaTemplate.send(message);
		future.addCallback(new ListenableFutureCallback<SendResult<String, SmsBodyVO>>() {

			@Override
			public void onSuccess(SendResult<String, SmsBodyVO> result) {
				// 성공 처리.
				log.info("send success: {}", value);
			}

			@Override
			public void onFailure(Throwable ex) {
				// 실패 처리.
				log.warn("send fail data:{}, exception:{}", value, StringUtil.exceptionMessage(ex));
			}
			
		});
		/*
		CompletableFuture<SendResult<String, SmsBodyVO>> future = kafkaTemplate.send(message);
		future.whenComplete((result, ex)->{
			if(ex==null) {
				// 성공 처리.
				log.info("send success: {}", value);
				//result.getProducerRecord().
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
	
	private SmsBodyVO getRandomSmsBodyVO() {
		String msg = messages[random.nextInt(messages.length)];
		return new SmsBodyVO("홍보팀", "고객명", "key-token", msg);
	}
}
