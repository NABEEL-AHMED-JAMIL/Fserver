package com.ballistic.fserver.properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;


public interface IFProperties {

    public static final Logger logger = LogManager.getLogger(IFProperties.class);

    @ConfigurationProperties(prefix = "file")
    public class LocalStoreProperties {

        private String uploadDir;

        public LocalStoreProperties() {
            logger.info("LocalStoreProperties-Constructor");
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

    @ConfigurationProperties(prefix = "mongodb")
    public class MongoDbStoreProperties {

        public MongoDbStoreProperties() {
            logger.info("MongoDbStoreProperties-Constructor");
        }

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

    @ConfigurationProperties(prefix = "amazon")
    public class AmazonProperties {

        @NestedConfigurationProperty
        private Aws aws = new Aws();

        @NestedConfigurationProperty
        private S3 s3 = new S3();

        public AmazonProperties() {
            logger.info("AmazonProperties-Constructor");
        }

        /**
         * A property group for Amazon Web Service (AWS) configurations
         *
         * @return a property group for AWS configurations
         */
        public Aws getAws() {
            logger.debug("Fetching AWS-Credential Properties => " + aws.toString());
            return aws;
        }

        /**
         * A property group for Amazon Web Service (AWS) configurations
         *
         * @param aws is a property group for AWS configurations
         */
        public void setAws(Aws aws) {
            logger.debug("AWS-Credential Properties => " + aws.toString());
            this.aws = aws;
        }

        /**
         * A property group for Amazon S3 configurations
         *
         * @return a property group for Amazon S3 configurations
         */
        public S3 getS3() {
            logger.debug("Fetching S3-Bucket Properties => " + s3.toString());
            return s3;
        }

        /**
         * A property group for Amazon S3 configurations
         *
         * @param s3 is a property group for Amazon S3 configurations
         */
        public void setS3(S3 s3) {
            logger.debug("S3-Bucket Properties => " + s3.toString());
            this.s3 = s3;
        }

        /**
         * A property group for Amazon Web Service (AWS) configurations
         */
        public static class Aws {

            private String accessKeyId;
            private String accessKeySecret;

            public Aws() {
                logger.info("Aws-Constructor");
            }

            /**
             * A valid AWS account's access key id.
             *
             * @return an AWS access key id
             */
            public String getAccessKeyId() {
                logger.debug("AWS-Credential AccessKeyId Properties => " + accessKeyId);
                return accessKeyId;
            }

            /**
             * A valid AWS account's access key id.
             *
             * @param accessKeyId is a valid AWS account's access key id.
             */
            public void setAccessKeyId(String accessKeyId) {
                logger.debug("AWS-Credential AccessKeyId Properties => " + accessKeyId);
                this.accessKeyId = accessKeyId;
            }

            /**
             * A valid AWS account's secret access token.
             *
             * @return an AWS account's secret access key
             */
            public String getAccessKeySecret() {
                logger.debug("AWS-Credential AccessKeySecret Properties => " + accessKeySecret);
                return accessKeySecret;
            }

            /**
             * A valid AWS account's secret access token.
             *
             * @param accessKeySecret is a valid AWS account's secret access token.
             */
            public void setAccessKeySecret(String accessKeySecret) {
                logger.debug("AWS-Credential AccessKeySecret Properties => " + accessKeySecret);
                this.accessKeySecret = accessKeySecret;
            }

            @Override
            public String toString() {
                return "Aws{" + "accessKeyId='" + accessKeyId + '\'' + ", accessKeySecret='" + accessKeySecret + '\'' + '}';
            }
        }

        /**
         * A property group for Amazon Web Service (AWS) configurations
         */
        public static class S3 {

            private String defaultBucket;

            public S3() {
                logger.info("S3-Constructor");
            }

            /**
             * The Amazon S3 bucket name for this application.
             *
             * @return a default Amazon S3 bucket name for this application.
             */
            public String getDefaultBucket() {
                logger.debug("AS3 DefaultBucket Properties => " + defaultBucket);
                return defaultBucket;
            }

            /**
             * The Amazon S3 bucket name for this application.
             *
             * @param defaultBucket is the default Amazon S3 bucket name for this application.
             */
            public void setDefaultBucket(String defaultBucket) {
                logger.debug("AS3 DefaultBucket Properties => " + defaultBucket);
                this.defaultBucket = defaultBucket;
            }

            @Override
            public String toString() {
                return "S3{" + "defaultBucket='" + defaultBucket + '\'' + '}';
            }
        }

        @Override
        public String toString() {
            return "AmazonProperties{" + "aws=" + aws + ", amazon.s3=" + s3 + '}';
        }
    }

}
