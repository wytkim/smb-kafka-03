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
package com.smband.kafka.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 * 개요:
 * </pre>
 * @author ytkim
 * @create 2022. 12. 21.
 * @version 
 * @since 
 */
public class SimpleUtil {

	/** read resource text file */
	public static String readResource(String path) {
		InputStream inputStream = SimpleUtil.class.getClassLoader().getResourceAsStream(path);
		if(inputStream != null) {
			try {
				return new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))
				.lines().collect(Collectors.joining());
			}catch(Exception err) {
				throw new RuntimeException(null, err);
			}
		}
		
		throw new RuntimeException("not found resource!");
	}
	
	/**
	 * <pre>
	 * 1. 함수 개요: 
	 *
	 * 2. 처리 내용:
	 *	key,value 형태로 입력된 데어티를 map으로 구성한다.
	 * </pre>
	 * @Create 작성자:ytkim, 작성일시: 2020. 3. 8. 오후 6:28:43
	 */
	@SuppressWarnings("unchecked")
	public static <T> Map<String, T> newMap(Object... items) {
		if(items.length%2 ==1) throw new RuntimeException("매개변수 개수 오류. key/value 쌍으로 매개변수를 사용해야 합니다.");
		
		int end = items.length/2;
		Map<String, T> map = new LinkedHashMap<>();
		for(int idx=0; idx<end; idx++) {
			map.put((String)items[idx*2], (T)items[idx*2+1]);
		}
		return map;
	}
}
