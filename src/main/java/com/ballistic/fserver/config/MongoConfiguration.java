package com.ballistic.fserver.config;

import com.ballistic.fserver.properties.IFProperties;
import com.mongodb.MongoClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@Configuration
public class MongoConfiguration {

    public static final Logger logger = LogManager.getLogger(MongoConfiguration.class);

    private final IFProperties.MongoDbStoreProperties mongoProperties;

    @Autowired
    public MongoConfiguration(IFProperties.MongoDbStoreProperties mongoProperties) {
        logger.info("Mongo-Configuration....");
        this.mongoProperties = mongoProperties;
    }

    @Primary
    @Bean(name = "fserverMongoTemplate")
    public MongoTemplate fserverMongoTemplate() throws Exception {
        logger.debug("fserver-mongo-template object init");
        return new MongoTemplate(fserverFactory(this.mongoProperties.getFserver()));
    }

    @Bean(name = "serverMongoTemplate")
    public MongoTemplate serverMongoTemplate() throws Exception {
        logger.debug("server-mongo-template object init");
        return new MongoTemplate(secondaryFactory(this.mongoProperties.getServer()));
    }

    @Bean
    @Primary
    public MongoDbFactory fserverFactory(final MongoProperties mongo) throws Exception {
        logger.debug("Fserver Mongodb Properties ==> " + mongo.getHost() + "," + mongo.getPort() + "," + mongo.getDatabase());
        return new SimpleMongoDbFactory(new MongoClient(mongo.getHost(), mongo.getPort()), mongo.getDatabase());
    }

    @Bean
    public MongoDbFactory secondaryFactory(final MongoProperties mongo) throws Exception {
        logger.debug("Server Mongodb Properties ==> " + mongo.getHost() + "," + mongo.getPort() + "," + mongo.getDatabase());
        return new SimpleMongoDbFactory(new MongoClient(mongo.getHost(), mongo.getPort()), mongo.getDatabase());
    }

    @EnableMongoRepositories(basePackages = "com.ballistic.fserver.repository", mongoTemplateRef = "fserverMongoTemplate")
    public class PrimaryMongoConfig {

    }

}
