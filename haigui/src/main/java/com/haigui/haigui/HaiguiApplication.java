package com.haigui.haigui;

import com.haigui.haigui.config.AiConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class HaiguiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HaiguiApplication.class, args);
	}

}
