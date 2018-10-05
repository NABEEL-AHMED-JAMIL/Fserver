package com.ballistic.fserver.service;

import com.ballistic.fserver.pojo.Account;

import java.util.List;
import java.util.Optional;

public interface IAccountService {

    public Account saveAccount(Account account);

    public List<Account> findAllAccountByStatus(String status);

    public Optional<Account> fetchAccount(String id);
    // both save && delete
    public List<Account> fetchAllAccount();

    public Account deleteAccount(String id);

    public List<Account> deleteAccounts(List<String> ids);
}
