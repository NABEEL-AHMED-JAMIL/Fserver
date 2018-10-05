package com.ballistic.fserver.manager;

import com.ballistic.fserver.exception.FileStorageException;
import com.ballistic.fserver.exception.MyFileNotFoundException;
import com.ballistic.fserver.properties.IFProperties;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;


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
            if(!this.localPathInit) {
                this.localFileStorePath = createPath(localStoreProperties.getUploadDir());
                this.localPathInit = true;
            }
        }

        // done test 99.99%
        public ManagerResponse<FileInfoStore> storeFile(MultipartFile file) throws FileStorageException {
            logger.info("file :- FileInfo{"+  "{}" + "}" , file.getOriginalFilename() + "," + file.getSize() + "," + file.getContentType());
            String fileName = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename()); // file name
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

        // handle error
        public Resource loadFileAsResource(String fileName) {
            try {
                Path filePath = this.localFileStorePath.resolve(fileName).normalize();
                Resource resource = new UrlResource(filePath.toUri()); // will get the resouce
                if(resource.exists()) {
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
        public Boolean deleteFile(String fileName) {
            Path path = this.localFileStorePath.resolve(fileName).normalize();
            // check the file contain or not
            if(Files.exists(path)) {
                // delete-ing file
                try {
                    logger.warn("File delete process");
                    Files.delete(path);
                    logger.info("file {} delete", path.getFileName());
                    return true;
                } catch (IOException e) {
                    throw null;
                }
            }
            throw null;
        }

        private static Path createPath(String uploadDir) {
            try {
                // check is path null then init
                if(localFileStorePath == null) {
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

    }

    /**
     * Common method for both manager
     * @param fileName
     * @return the String object
     * */
    private static String downloadUrl(String fileName) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("xyz").path(fileName).toUriString();
    }

}
