package com.ballistic.fserver.repository;

import com.ballistic.fserver.pojo.FileInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IFileInfoRepository extends MongoRepository<FileInfo,String> {

    public Optional<FileInfo> findByFileName(String fileName);
}
