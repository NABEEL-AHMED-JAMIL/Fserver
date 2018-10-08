package com.ballistic.fserver.properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;


public interface IFProperties {

    public static final Logger logger = LogManager.getLogger(IFProperties.class);

    @ConfigurationProperties(prefix = "file")
    public class LocalStoreProperties {

        private String uploadDir;

        public LocalStoreProperties() {
            logger.info("local-store-properties-constructor");
        }

        public String getUploadDir() {
            logger.debug("Fetching Local UploadDir Properties => " + this.uploadDir);
            return this.uploadDir;
        }

        public void setUploadDir(String uploadDir) {
            logger.debug("Local UploadDir Properties => " + uploadDir);
            this.uploadDir = uploadDir;
        }

        @Override
        public String toString() {
            return "LocalStoreProperties{" + "uploadDir='" + uploadDir + '\'' + '}';
        }
    }

    public class ServerStoreProperties {}

    @ConfigurationProperties(prefix = "mongodb")
    public class MongoDbStoreProperties {

        /**
         * For a clustered Mongo environment we would want to load multiple
         * hosts. This will work if we use a single host or clustered.
         *
         * If the mongo.hosts key could not be found defaults to localhost
         *
         * One of the following would exist in our Spring properties file
         * 1) mongo.hosts = localhost
         * 2) mongo.hosts = 10.1.1.1,10.1.1.2,10.1.1.3
         */
        private MongoProperties fserver = new MongoProperties();
        private MongoProperties server = new MongoProperties();

        public MongoProperties getFserver() {
            logger.debug("Fetching Fserver Properties => " + this.fserver);
            return fserver;
        }

        public void setFserver(MongoProperties fserver) {
            logger.debug("Fserver MongoDb Properties => " + fserver);
            this.fserver = fserver;
        }

        public MongoProperties getServer() {
            logger.debug("Fetching Server Properties => " + this.fserver);
            return server;
        }

        public void setServer(MongoProperties server) {
            logger.debug("Server MongoDb Properties => " + fserver);
            this.server = server;
        }

        @Override
        public String toString() {
            return "MongoDbStoreProperties{" + "fserver=" + fserver + ", server=" + server + '}';
        }
    }

}
