package com.ballistic.fserver;

import com.ballistic.fserver.properties.IFProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
	IFProperties.LocalStoreProperties.class,
	IFProperties.MongoDbStoreProperties.class
})
public class FserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(FserverApplication.class, args);
	}
}
