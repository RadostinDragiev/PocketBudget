package com.pocketbudget.service;

import com.pocketbudget.model.binding.AccountAddBindingModel;
import com.pocketbudget.model.service.AccountAddServiceModel;

public interface AccountService {
    AccountAddBindingModel createAccount(AccountAddServiceModel accountAddBindingModel);
}
