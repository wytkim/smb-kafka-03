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
package com.smband.kafka.rest;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smband.kafka.common.SimpleUtil;
import com.smband.kafka.model.SmsBodyVO;
import com.smband.kafka.model.SmsHistVO;
import com.smband.kafka.producer.SmsProducerService;
import com.smband.kafka.service.KafkaProcessService;

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
@RestController
@RequestMapping("api")
public class ApiController {

	@Autowired
	private SmsProducerService smsProdService;
	@Autowired
	private KafkaProcessService procService;
	
	@RequestMapping("sendSms")
	public ResponseEntity<Map<String, Object>> sendSms(@RequestBody SmsBodyVO smsBodyVo){
		this.smsProdService.sendSms(smsBodyVo);
		log.info("send sms!");
		return ResponseEntity.ok(SimpleUtil.newMap(
				"result", "success"));
	}
	
	@RequestMapping("addSmsHist")
	public ResponseEntity<String> insertSmsHist(){
		SmsHistVO histVo = genSampleSmsHistVo();
		this.procService.insertSmsHist(histVo);
		return ResponseEntity.ok("ok");
	}
	
	@RequestMapping("addSmsHistList")
	public ResponseEntity<String> insertSmsHistList(){
		
		List<SmsHistVO> list = IntStream.range(0, 10)
		.mapToObj((idx)-> genSampleSmsHistVo())
		.collect(Collectors.toList());
		
		// 예외 데이터 생성하기
		//list.get(list.size()-3).setTo("예외발생할 수 있는 이름");
		
		this.procService.insertSmsHistList(list);
		return ResponseEntity.ok("ok");
	}

	
	
	/**
	 * <pre>
	 * 1. 함수 개요: 
	 * 2. 처리 내용:
	 * </pre>
	 * @return
	 * @Create 작성자:ytkim, 작성일시: 2023. 4. 9. 오후 10:32:10
	 */
	private SmsHistVO genSampleSmsHistVo() {
		return new SmsHistVO(1, "홍보팀", "고객", messages[random.nextInt(messages.length)]);
	}
	
	final String[] messages = {
			"이날 일본 도쿄증시에서 닛케이225지수는 전일 대비 0.39% 상승한 2만8149.89에 오전 거래를 마쳤다.", 
			"일본 대형 제조업체들의 체감 경기를 나타내는 단기경제관측조사(단칸) 업황 지수가 올해 1분기 1을 기록,",
			"중화권도 소폭의 오름세다. 중국 본토 상하이종합지수는 한국시간 오전 11시",
			"한편 대만 증시는 이날부터 5일까지 어린이날과 청명절로 휴장한",
			"이번주 국내 증시는 1·4분기 실적 발표에 투자자 관심이 집중될 전망이다. 미국 은행권을 둘러싼 불확실성은 여전하다. "};
	final Random random = new Random(System.currentTimeMillis());
}
