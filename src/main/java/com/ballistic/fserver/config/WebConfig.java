package com.ballistic.fserver.config;
//https://howtodoinjava.com/spring-boot2/enableasync-async-controller/
import com.ballistic.fserver.properties.IFProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

// done test 100%
@Configuration
//@ConditionalOnMissingBean(AmazonS3Template.class)
@EnableAsync
@EnableConfigurationProperties({
    IFProperties.LocalStoreProperties.class,
    IFProperties.MongoDbStoreProperties.class,
    IFProperties.AmazonProperties.class
})
public class WebConfig {

    private static final Logger logger = LogManager.getLogger(WebConfig.class);

    @Bean
    public ModelMapper modelMapper() { return new ModelMapper(); }

//    @Autowired
//    private AmazonProperties amazonProperties;
//
//    @Bean
//    AmazonS3Template amazonS3Template() {
//        return new AmazonS3Template(amazonProperties.getS3().getDefaultBucket(),
//                amazonProperties.getAws().getAccessKeyId(),
//                amazonProperties.getAws().getAccessKeySecret());
//    }

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(6);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AsynchThread-");
        executor.initialize();
        return executor;
    }
}
