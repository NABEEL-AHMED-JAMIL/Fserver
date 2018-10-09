package com.ballistic.fserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FserverApplicationTests {

	private static final Logger logger = LogManager.getLogger(FserverApplicationTests.class);

	public static final String REST_SERVICE_URI = "http://localhost:9191";
	public RestTemplate restTemplate;

	private RestTemplate getRestTemplate(){ return  new RestTemplate(); }

	@Test
	public void contextLoads() { }


	/**
	 * Request Type:- POST
	 * Note:- uploadSingleFile Test help to test the file upload or not
	 * Response Type :- APIResponse
	 * */
	@Test
	public void uploadSingleFileTest() {
		//
		try {
			//
			logger.info("Single File Upload Test Case");
			this.restTemplate = getRestTemplate();
	//		this.restTemplate.post

		}catch (Exception e){
			logger.error(e.getMessage());
		}
	}


}
