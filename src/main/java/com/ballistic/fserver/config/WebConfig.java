package com.ballistic.fserver.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// done test 100%
@Configuration
public class WebConfig {

    private static final Logger logger = LogManager.getLogger(WebConfig.class);

    @Bean
    public ModelMapper modelMapper() { return new ModelMapper(); }
}
