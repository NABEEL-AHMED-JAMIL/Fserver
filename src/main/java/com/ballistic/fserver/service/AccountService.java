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
        logger.debug("Save - {} to Db ", account);
        return this.iAccountRepository.save(account);
    }

    // handle error
    // both save && deletek8
    @Override
    public List<Account> fetchAllAccountByStatus(String status) {
        logger.debug("Fetch - Account by {} from Db ", status);
        return this.iAccountRepository.findByStatus(status);
    }

    // handle error
    public Optional<Account> fetchAccountById(String id) {
        logger.debug("Fetch - {} from Db " , id);
        return this.iAccountRepository.findById(id);
    }

    // handle error
    public List<Account> fetchAllAccount(){
        logger.debug("Fetch - all account from Db ");
        return this.iAccountRepository.findAll();
    }

    // handle error
    public Account deleteAccount(Account account) throws NullPointerException {
        logger.debug("Delete - AccountId {} from Db ", account.getId());
        // update the status of the file
        logger.debug("file-delete process..");
        account.setStatus("Delete");
        logger.warn("account-delete process..");
        account = this.saveAccount(account);
        logger.debug("account-delete done");
        return account;
    }

    // use @Query
    // handle error
    public List<Account> deleteAccounts(List<String> ids) {

        List<Account> accounts = null;
        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(ids).andOperator(Criteria.where("status").ne("save")));
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
