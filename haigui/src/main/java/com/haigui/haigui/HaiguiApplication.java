package com.haigui.haigui;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.haigui.haigui.mapper")
public class HaiguiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HaiguiApplication.class, args);
	}

}
