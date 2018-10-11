package com.ballistic.fserver;

import com.ballistic.fserver.properties.IFProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class FserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(FserverApplication.class, args);
	}
}
