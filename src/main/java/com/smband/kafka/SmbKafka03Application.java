package com.smband.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@PropertySource(value= {
		"classpath:property/${server.mode}/jdbc.properties",
}, encoding="UTF-8")
public class SmbKafka03Application {

	public static void main(String[] args) {
		// default app mode 설정
		String serverMode = (String)System.getProperty("server.mode", "debug");
		System.setProperty("server.mode", serverMode);

		SpringApplication.run(SmbKafka03Application.class, args);
	}

}
