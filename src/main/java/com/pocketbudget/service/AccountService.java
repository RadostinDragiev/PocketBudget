package com.pocketbudget.service;

import com.pocketbudget.model.binding.AccountAddBindingModel;

public interface AccountService {
    AccountAddBindingModel createAccount(AccountAddBindingModel accountAddBindingModel);
}
