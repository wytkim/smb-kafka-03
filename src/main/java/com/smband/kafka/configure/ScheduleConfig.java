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
package com.smband.kafka.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.smband.kafka.schedule.SmbProducerSchedule;
import com.smband.kafka.schedule.SmsSendProdSchedule;

import lombok.Setter;
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
@Setter
@Configuration
@ConfigurationProperties(prefix = "app-scheduler")
@EnableScheduling
public class ScheduleConfig implements SchedulingConfigurer {
	private int poolSize = 10;
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();
		
		threadPool.setPoolSize(poolSize);
		threadPool.setThreadNamePrefix("kafka-");
		threadPool.initialize();

		taskRegistrar.setTaskScheduler(threadPool);
	}

	//@Bean
	public SmbProducerSchedule smbProcSchedule() {
		log.info("smb producer schedule bean 생성.");
		return new SmbProducerSchedule();
	}
	//@Bean
	public SmsSendProdSchedule smsSendProdSchedule() {
		log.info("smb send producer schedule bean 생성.");
		return new SmsSendProdSchedule();
	}
}
