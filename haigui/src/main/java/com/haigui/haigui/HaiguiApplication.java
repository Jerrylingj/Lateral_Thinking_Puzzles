package com.haigui.haigui;

import com.haigui.haigui.config.AiConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties
@EnableTransactionManagement
@EnableAsync
@MapperScan("com.haigui.haigui.mapper")
public class HaiguiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HaiguiApplication.class, args);
	}

}
