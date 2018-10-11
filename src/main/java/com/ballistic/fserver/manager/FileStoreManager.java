package com.ballistic.fserver.manager;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.ballistic.fserver.exception.FileStorageException;
import com.ballistic.fserver.exception.MyFileNotFoundException;
import com.ballistic.fserver.properties.IFProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Component
public class FileStoreManager {

    private static final Logger logger = LogManager.getLogger(FileStoreManager.class);

    @Autowired
    private LocalFileStoreManager localFileStoreManager;
    @Autowired
    private ServerFileStoreManager serverFileStoreManager;

    public LocalFileStoreManager getLocalFileStoreManager() { return localFileStoreManager; }
    public ServerFileStoreManager getServerFileStoreManager() { return serverFileStoreManager; }


    @Component
    public static class LocalFileStoreManager {

        private static Path localFileStorePath;
        private static volatile Boolean localPathInit = false;
        private FileInfoStore fileInfoStore = null;

        // done test 99.99%
        @Autowired
        public LocalFileStoreManager(IFProperties.LocalStoreProperties localStoreProperties) throws FileStorageException {
            /**
             * Note:- localPathInit boolean volatile will look-up condition
             * until the value change of that variable
             * */
            if(!this.localPathInit) {
                logger.info("LocalFileStoreManager-->LocalStoreProperties-->Init-Value");
                this.localFileStorePath = createPath(localStoreProperties.getUploadDir());
                this.localPathInit = true;
            }
        }

        // done test 99.99%
        public ManagerResponse<FileInfoStore> storeFile(MultipartFile file) throws FileStorageException {
            logger.info("file :- FileInfo{"+  "{}" + "}" , file.getOriginalFilename() + "," + file.getSize() + "," + file.getContentType());
            String fileName = StringUtils.cleanPath(file.getOriginalFilename()); // file name
            try {
                if(fileName.contains("..")){
                    logger.error("filename contains invalid path :- " + fileName + ".");
                    throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
                }
                fileName = new Date().getTime() + "-" + UUID.randomUUID() + "-" + fileName;
                Path targetLocation = this.localFileStorePath.resolve(fileName);
                logger.debug("try....save...file");
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                logger.debug("try....save...file...done");
                // File Store success
                BufferedImage bimg = ImageIO.read(file.getInputStream());
                this.fileInfoStore = new FileInfoStore();
                this.fileInfoStore.setFileName(fileName);
                this.fileInfoStore.setFileUrl(downloadUrl(String.valueOf(targetLocation.getFileName())));
                this.fileInfoStore.setContentType(file.getContentType());
                this.fileInfoStore.setSize(file.getSize());
                this.fileInfoStore.setHeight(String.valueOf(bimg.getHeight()));
                this.fileInfoStore.setWidth(String.valueOf(bimg.getWidth()));
                return new ManagerResponse<>("File Store in local success fully", HttpStatus.OK, this.fileInfoStore);
            }catch (IOException ex) {
                logger.error("bucket not found pls try again :- {} " , this.localFileStorePath);
                HashMap<String, Object> error = new HashMap<String, Object>();
                error.put("message", "bucket not found pls try again.");
                error.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
                // will try to create the path again
                this.localFileStorePath = createPath(this.localFileStorePath.toUri().toString());
                throw new FileStorageException(String.valueOf(error), ex);
            }
        }

        /**
         * Handles LoadFileAsResource.
         * help method to load the file-resource
         *
         * @param fileName the String
         * @return the Resource object
         */
        public Resource loadFileAsResource(String fileName) {
            try {
                Path filePath = this.localFileStorePath.resolve(fileName).normalize();
                Resource resource = new UrlResource(filePath.toUri()); // will get the resouce
                if(resource.exists() || resource.isReadable()) {
                    logger.info("Resource found..." + resource.getFilename());
                    return resource;
                } else {
                    logger.error("File not found " + fileName + ".");
                    throw new MyFileNotFoundException("File not found " + fileName);
                }
            }catch (MalformedURLException ex) {
                logger.error("File not found " + fileName + ".");
                throw new MyFileNotFoundException("File not found " + fileName, ex);
            }
        }

        // handle error
        public Boolean deleteFile(String fileName) throws IOException {
            Path path = this.localFileStorePath.resolve(fileName).normalize();
            // check the file contain or not
            if(Files.exists(path)) {
                try {
                    logger.warn("File delete process");
                    Files.delete(path);
                    logger.info("file {} delete", path.getFileName());
                    return true;
                } catch (IOException ex) {
                    throw new IOException(ex.getMessage());
                }
            }
            throw new MyFileNotFoundException("File not found " + fileName);
        }

        /**
         * Note:- Path ==> Create-Path
         * Create the local-path on init the project
         * if the user delete the folder of path it will create the on the request but file
         * not save on next request will able to save again file
         * */
        private static Path createPath(String uploadDir) {
            try {
                // check is path null then init
                if(StringUtils.isEmpty(localFileStorePath)) {
                    localFileStorePath = Paths.get(uploadDir).toAbsolutePath().normalize();
                }
                logger.info("file path init. => " + uploadDir);
                Files.createDirectories(localFileStorePath);
                logger.info("file directories create successfully");
            }catch (Exception ex) {
                logger.error("not able to create directory.");
                HashMap<String, Object> error = new HashMap<String, Object>();
                error.put("message", "Could not create the directory where the uploaded files will be stored.");
                error.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
                throw new FileStorageException(String.valueOf(error), ex);
            }
            return localFileStorePath;
        }

    }

    @Component
    public static class ServerFileStoreManager {

        private static volatile Boolean serverPathInit = false;
        private String defaultBucket;
        private String accessKeyId;
        private String accessKeySecret;
        private Credentials sessionCredentials;


        /**
         * Create a new instance of the {@link ServerFileStoreManager} with the bucket name and access credentials
         **/
        @Autowired
        public ServerFileStoreManager(IFProperties.AmazonProperties awsProp) {
            /**
             * Note:- serverPathInit boolean volatile will look-up condition
             * until the value change of that variable
             * */
            if(!this.serverPathInit) {
                logger.info("ServerFileStoreManager-->AmazonProperties-->Init-Value");
                this.defaultBucket = awsProp.getS3().getDefaultBucket();
                this.accessKeyId = awsProp.getAws().getAccessKeyId();
                this.accessKeySecret = awsProp.getAws().getAccessKeySecret();
                this.serverPathInit = true;
            }
        }


        // Creating, Listing, and Deleting |S3| Buckets
        public void createBucket() {}
        public void listingBucket() {}
        public void deletingBucket() {}

        public void saveFileTOBucket() {}
        public void saveFilesToBucket() {}
        public void deleteFileFromBucket() {}
        public void deleteFilesFromBucket() {}
        public void fetchFileFromBucket() {}
        public void fetchFilesFromBucket() {
        }

        /**
         * Get an Amaxon S3 client from basic session credentials
         *
         * */
        public AmazonS3 getAmazonS3Client() {
            BasicSessionCredentials basicSessionCredentials = getBasicSessionCredentials();
            // create a new s3 usign the basic session of the service instace
            return new AmazonS3Client(basicSessionCredentials);
        }

        /**
         * Get the basic session credential for the template's configured IAM authentication keys
         *
         * */
        private BasicSessionCredentials getBasicSessionCredentials() {

            // Create a new Session token if the session is expireed or not initialized
            if(ObjectUtils.isEmpty(this.sessionCredentials) || this.sessionCredentials.getExpiration().before(new Date())) {
                this.sessionCredentials = getSessionCredentials();
            }
            return new BasicSessionCredentials(this.sessionCredentials.getAccessKeyId(),
                    this.sessionCredentials.getSecretAccessKey(),
                    this.getSessionCredentials().getSessionToken());
        }

        /**
         * Creates a new session credential that is valid for 12 hours
         */
        private Credentials getSessionCredentials () {
            // Create a new session with the user credentials for the service instance
            AWSSecurityTokenServiceClient stsClient = new AWSSecurityTokenServiceClient(new BasicAWSCredentials(this.accessKeyId, this.accessKeySecret));
            // Get-Session-Tokent
            GetSessionTokenRequest getSessionTokenRequest = new GetSessionTokenRequest().withDurationSeconds(43200);
            // Get the session token for the service instance's bucket
            this.sessionCredentials = stsClient.getSessionToken(getSessionTokenRequest).getCredentials();

            return sessionCredentials;
        }

        /**
         * Convert MultiPartFile to ordinary File
         *
         * @param multipartFile
         * @return
         * @throws IOException
         */
        private CompletableFuture<File> convertFromMultiPart(MultipartFile multipartFile) throws IOException {

            File file = new File(multipartFile.getOriginalFilename());
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();
            file.getAbsolutePath();
            //return file;
            return null;
        }
    }

    /**
     * Common method for both manager
     * @param fileName
     * @return the String object
     * */
    private static String downloadUrl(String fileName) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/download/image/file-name=").path(fileName).toUriString();
    }

}
