package com.pocketbudget.service.impl;

import com.pocketbudget.model.binding.AccountAddBindingModel;
import com.pocketbudget.model.entity.Account;
import com.pocketbudget.model.entity.User;
import com.pocketbudget.model.service.AccountAddServiceModel;
import com.pocketbudget.repository.AccountRepository;
import com.pocketbudget.service.AccountService;
import com.pocketbudget.service.UserService;
import com.pocketbudget.util.DateTimeApplier;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final DateTimeApplier dateTimeApplier;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, UserService userService, ModelMapper modelMapper, DateTimeApplier dateTimeApplier) {
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.dateTimeApplier = dateTimeApplier;
    }

    @Override
    public AccountAddBindingModel createAccount(AccountAddServiceModel accountAddServiceModel) {
        User user = this.userService.getUserByUsername(accountAddServiceModel.getUsername());
        Account account = this.modelMapper.map(accountAddServiceModel, Account.class);
        account.setUser(user);
        this.dateTimeApplier.applyDateTIme(account);

        return this.modelMapper.map(this.accountRepository.saveAndFlush(account), AccountAddBindingModel.class);
    }
}
