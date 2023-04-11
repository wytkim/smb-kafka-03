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
package com.smband.kafka.mapper;

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
public interface KafkaProcessMapper {

	void insertSmsHist(SmsHistVO smsHist);
}
