package com.ballistic.fserver.service;

import com.ballistic.fserver.pojo.FileInfo;
import com.ballistic.fserver.repository.IFileInfoRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class FileStoreService {

    private static final Logger logger = LogManager.getLogger(FileStoreService.class);

    @Autowired
    private LocalFileStoreService localFileStoreService;
    @Autowired
    private ServerFileStoreService serverFileStoreService;
    @Autowired
    private IFileInfoRepository iFileInfoRepository;


    public LocalFileStoreService getLocalFileStoreService() { return localFileStoreService; }

    public ServerFileStoreService getServerFileStoreService() { return serverFileStoreService; }


    @Service
    public class LocalFileStoreService implements IFileStoreService.ILocalFileStoreService {

        @Override
        public FileInfo storeFile(FileInfo fileInfo) {
            logger.debug("Save- {} to Db ", fileInfo);
            return iFileInfoRepository.save(fileInfo);
        }

        @Override
        public Optional<FileInfo> loadFileAsResource(String id) {
            logger.debug("Load- File for {} from Db ", id);
            return iFileInfoRepository.findById(id);
        }

        @Override
        public FileInfo deleteFile(String fileName) {
            logger.debug("Delete- File {} from Db ", fileName);
            FileInfo fileInfo = this.loadFileAsResource(fileName).
                    orElseThrow(null);

            // update the status of the file
            fileInfo.setStatus("delete");
            logger.warn("file-delete process..");
            fileInfo = this.storeFile(fileInfo);
            logger.info("file-delete done");
            return fileInfo;
        }


    }

    @Service
    public class ServerFileStoreService implements IFileStoreService.IServerFileStoreService {


        @Override
        public void storeFile() {

        }

        @Override
        public void loadFileAsResource() {

        }
    }

}
