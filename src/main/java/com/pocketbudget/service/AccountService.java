package com.pocketbudget.service;

import com.pocketbudget.model.binding.AccountAddBindingModel;
import com.pocketbudget.model.binding.AccountDetailsBindingModel;
import com.pocketbudget.model.entity.Account;
import com.pocketbudget.model.service.AccountAddServiceModel;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    Optional<AccountAddBindingModel> createAccount(AccountAddServiceModel accountAddBindingModel);

    Optional<AccountDetailsBindingModel> getAccountBindingModelByUUID(String uuid, String username);

    List<AccountDetailsBindingModel> getAllAccounts(String username);

    boolean deleteAccount(String accountUUID, String username);

    Optional<AccountAddBindingModel> updateAccount(String accountUUID, AccountAddServiceModel accountAddServiceModel);

    Account getAccountByUUID(String accountUUID);

    boolean isUserOwner(String username, String accountUUID);
}
