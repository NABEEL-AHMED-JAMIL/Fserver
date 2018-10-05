package com.ballistic.fserver.properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

public interface IFProperties {

    public static final Logger logger = LogManager.getLogger(IFProperties.class);

    @ConfigurationProperties(prefix = "file")
    public class LocalStoreProperties {

        private String uploadDir;

        public LocalStoreProperties() {
            logger.info("local-store-properties-constructor");
        }

        public String getUploadDir() { return uploadDir; }

        public void setUploadDir(String uploadDir) {
            logger.info("Local-upload-Dir => " + uploadDir);
            this.uploadDir = uploadDir;
        }

        @Override
        public String toString() {
            return "LocalStoreProperties{" + "uploadDir='" + uploadDir + '\'' + '}';
        }
    }

    public class ServerStoreProperties {}

    @ConfigurationProperties(prefix = "spring.data.mongodb")
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
        private List<String> hosts;

        /**
         * The port our Mongo hosts are running on.
         * Defaults to 27017
         */
        private Integer port;

        private String database;

        public MongoDbStoreProperties() {
            logger.info("mongo-db-store-properties-constructor");
        }

        public List<String> getHosts() {
            logger.info("get-hosts");
            return hosts;
        }

        public void setHosts(List<String> hosts) {
            logger.info("set-hosts");
            this.hosts = hosts;
        }

        public Integer getPort() {
            logger.info("get-port");
            return port;
        }

        public void setPort(Integer port) {
            logger.info("set-port");
            this.port = port;
        }

        public String getDatabase() {
            logger.info("get-database");
            return database;
        }

        public void setDatabase(String database) {
            logger.info("set-database");
            this.database = database;
        }

        @Override
        public String toString() {
            return "MongoDbStoreProperties{" + "hosts=" + hosts + ", port=" + port + ", database='" + database + '\'' + '}';
        }
    }

}
