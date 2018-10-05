package com.ballistic.fserver.service;

import com.ballistic.fserver.pojo.Account;
import com.ballistic.fserver.repository.IAccountRepository;
import com.mongodb.client.result.UpdateResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class AccountService implements IAccountService {

    private static final Logger logger = LogManager.getLogger(AccountService.class);

    @Autowired
    MongoTemplate template;

    @Autowired
    private IAccountRepository iAccountRepository;

    public Account saveAccount(Account account) {
        logger.info("Save- {} to Db ", account);
        return this.iAccountRepository.save(account);
    }

    // handle error
    // both save && delete
    @Override
    public List<Account> findAllAccountByStatus(String status) {
        logger.info("fetch Account by {} from Db ", status);
        return this.iAccountRepository.findByStatus(status);
    }

    // handle error
    public Optional<Account> fetchAccount(String id) {
        logger.info("fetch {} from Db " , id);
        return this.iAccountRepository.findById(id);
    }

    // handle error
    public List<Account> fetchAllAccount(){
        logger.info("fetch all account from Db ");
        return this.iAccountRepository.findAll();
    }

    // handle error
    public Account deleteAccount(String id) {
        logger.info("Delete- {} from Db ", id);
        Account account = this.fetchAccount(id).orElseThrow(null);
        // update the status of the file
        account.setStatus("delete");
        logger.warn("account-delete process..");
        account = this.saveAccount(account);
        logger.info("account-delete done");

        return account;
    }

    // handle error
    public List<Account> deleteAccounts(List<String> ids) {
        List<Account> accounts = null;

        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(ids).
                andOperator(Criteria.where("status").ne("save")));
        logger.info("Json Query :- "+ query.toString());

        Update update = new Update();
        update.set("status", "delete");

        logger.info("Json Update Value :- " + update);
        UpdateResult result = this.template.updateMulti(query,update, Account.class);
        if(result.getModifiedCount() > 0) {
            this.iAccountRepository.findAllById(ids).forEach(account -> {
                logger.info("value send-back :- " + account.toString());
                accounts.add(account);
            });
        }
        return accounts;
    }

}
