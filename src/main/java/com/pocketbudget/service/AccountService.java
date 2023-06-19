package com.pocketbudget.service;

import com.pocketbudget.model.binding.AccountAddBindingModel;
import com.pocketbudget.model.binding.AccountDetailsBindingModel;
import com.pocketbudget.model.entity.Account;
import com.pocketbudget.model.service.AccountAddServiceModel;

import java.util.List;

public interface AccountService {
    AccountAddBindingModel createAccount(AccountAddServiceModel accountAddBindingModel);

    List<AccountDetailsBindingModel> getAllAccounts(String username);

    boolean deleteAccount(String accountUUID);

    AccountAddBindingModel updateAccount(String accountUUID, AccountAddServiceModel accountAddServiceModel);

    Account getAccountByUUID(String accountUUID);

    boolean isUserOwner(String username, String accountUUID);
}
