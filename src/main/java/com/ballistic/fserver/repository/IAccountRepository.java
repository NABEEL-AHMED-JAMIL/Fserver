package com.ballistic.fserver.repository;

import com.ballistic.fserver.pojo.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAccountRepository extends MongoRepository<Account, String> {

    public List<Account> findByStatus(String status);

}
