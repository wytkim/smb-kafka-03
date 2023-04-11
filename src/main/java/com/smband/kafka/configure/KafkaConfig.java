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

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

//import com.fasterxml.jackson.databind.JsonSerializer;
import com.smband.kafka.common.SimpleUtil;
import com.smband.kafka.model.SmsBodyVO;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 개요:
 * </pre>
 * @author ytkim
 * @create 2023. 4. 2.
 * @version 
 * @since 
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "smband.kafka")
@EnableKafka
public class KafkaConfig {

	private String bootstrapServers = "127.0.0.1:9092";
	private String groupId = "smb-group";
	private String autoOffsetReset = "earliest";
	//private String groupId2 = "";
	
	@Bean
	public ConsumerFactory<String, String> smbConsumerFactory(){
		StringDeserializer deserializer = new StringDeserializer();
		return new DefaultKafkaConsumerFactory<>(
				smbConsumerFactoryConfig(deserializer),
				new StringDeserializer(),
				deserializer
				);
	}
	@Bean
	public ConsumerFactory<String, String> smbConsumer2Factory(){
		StringDeserializer deserializer = new StringDeserializer();
		return new DefaultKafkaConsumerFactory<>(
				smbConsumerFactoryConfig(deserializer),
				new StringDeserializer(),
				deserializer
				);
	}
	
	@Bean
	public ConsumerFactory<String, SmsBodyVO> smsSendConsumerFactory(){
		JsonDeserializer<SmsBodyVO> deserializer = smsBodyJsonDeserializer();
		return new DefaultKafkaConsumerFactory<>(
				smbConsumerFactoryConfig(deserializer),
				new StringDeserializer(),
				deserializer
				);
	}
	
	private Map<String, Object> smbConsumerFactoryConfig(Deserializer<? extends Object> deserializer){
		Map<String, Object> props = SimpleUtil.newMap(
				ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
				ConsumerConfig.GROUP_ID_CONFIG, groupId,
				ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset,
				ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10,
				ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
				ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer
				);
		//ConsumerConfig.AUTO_OFFSET_RESET_CONFIG
		return props;
	}
	
	private JsonDeserializer<SmsBodyVO> smsBodyJsonDeserializer() {
        JsonDeserializer<SmsBodyVO> deserializer = new JsonDeserializer<>(SmsBodyVO.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*"); // producer에서 사용하는 package명과 다르다면 사용한다. 
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
	
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> smbKafkaListenerContainerFactory(
			ConsumerFactory<String, String> smbConsumerFactory
			){
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(smbConsumerFactory);
		return factory;
	}
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> smbKafkaListenerContainer2Factory(
			ConsumerFactory<String, String> smbConsumer2Factory
			){
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(smbConsumer2Factory);
		return factory;
	}
	
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, SmsBodyVO> smbSendListenerContainerFactory(
			ConsumerFactory<String, SmsBodyVO> smsSendConsumerFactory
			){
		ConcurrentKafkaListenerContainerFactory<String, SmsBodyVO> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(smsSendConsumerFactory);
		factory.setBatchListener(true);
		return factory;
	}
	
	// Producer 구성하기.
	
	@Bean
	public ProducerFactory<String, String> producerFactory(){
		return new DefaultKafkaProducerFactory<>(producerFactoryConfig());
	}
	
	@Bean
	public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String>  producerFactory){
		return new KafkaTemplate<>(producerFactory);
	}
	
	private Map<String, Object> producerFactoryConfig(){
		return SimpleUtil.newMap(
				ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
				ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
				ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
				);
	}
	
	@Bean
	public ProducerFactory<String, SmsBodyVO> smsSendProducerFactory(){
		return new DefaultKafkaProducerFactory<>(smsSendProducerFactoryConfig());
	}
	@Bean
	public KafkaTemplate<String, SmsBodyVO> kafkaSmsSendTemplate(ProducerFactory<String, SmsBodyVO>  smsSendProducerFactory){
		return new KafkaTemplate<>(smsSendProducerFactory);
	}
	private Map<String, Object> smsSendProducerFactoryConfig(){
		return SimpleUtil.newMap(
				ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
				ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
				ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
				);
	}
}
