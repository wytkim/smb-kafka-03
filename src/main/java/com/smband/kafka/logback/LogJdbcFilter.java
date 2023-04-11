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
package com.smband.kafka.logback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * <pre>
 * 개요:
 * </pre>
 * @author ytkim
 * @create 2022. 11. 9.
 * @version 
 * @since 
 */
public class LogJdbcFilter extends Filter<ILoggingEvent> {

	private List<String> sqltimingList = new ArrayList<>();
	private List<String> resultsettableList = new ArrayList<>();
	
	@Override
	public FilterReply decide(ILoggingEvent event) {
		if(!isStarted()) {
			return FilterReply.NEUTRAL;
		}
		
		String message = event.getMessage();
		String name = event.getLoggerName();
		if(StringUtils.isEmpty(message)==false) {
			if("jdbc.sqltiming".equals(name)) {
				if(!sqltimingList.isEmpty()) {
					for(String sqlId: sqltimingList) {
						if(message.contains(sqlId)) return FilterReply.DENY;
					}
				}
			}
			else if("jdbc.resultsettable".equals(name)) {
				// 1. 컬럼 라인 식별.
				int idx = message.indexOf('\n', 2);
				if(idx>0) {
					int start = idx+1;
					idx = message.indexOf('\n', start);
					if(idx>0) {
						int end = idx;
						String columnRow = message.substring(start, end).replaceAll("[\\|\\s]", "");
						for(String columns: resultsettableList) {
							if(columnRow.contains(columns)) return FilterReply.DENY;
						}
						
					}
				}
			}
		}
		return FilterReply.NEUTRAL;
	}
	
	/** 제외 sql id 목록 */
	public void setSqltiming(String strSqlIds) {
		String[] tokens = strSqlIds.split(",");
		if(tokens.length>0) {
			sqltimingList = Arrays.stream(tokens)
					.map(token->token.trim())
					.filter(token->!token.isEmpty())
					.collect(Collectors.toList());
		}
	}
	
	/** 제외 컬럼 목록 */
	public void setResultsettable(String strColumns) {
		String[] tokens = strColumns.split(",");
		if(tokens.length>0) {
			resultsettableList = Arrays.stream(tokens)
			.map(token->token.trim())
			.filter(token->!token.isEmpty())
			.map(token->Arrays.stream(token.split("[\\|]"))
							.map(column->column.trim())
							.filter(column->!column.isEmpty())
							.collect(Collectors.joining("")))
			.filter(list->!list.isEmpty())
			.collect(Collectors.toList());
		}
	}
}
