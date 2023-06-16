package com.pocketbudget.service.impl;

import com.pocketbudget.model.binding.AccountAddBindingModel;
import com.pocketbudget.model.entity.Account;
import com.pocketbudget.repository.AccountRepository;
import com.pocketbudget.service.AccountService;
import com.pocketbudget.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, UserService userService, ModelMapper modelMapper) {
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AccountAddBindingModel createAccount(AccountAddBindingModel accountAddBindingModel) {
        Account account = this.modelMapper.map(accountAddBindingModel, Account.class);
        try {
            account.setUser(this.userService.getUserByUUID(accountAddBindingModel.getUserUUID()));
        } catch (Exception e) {
            log.error("Failed to retrieve user ->" + e.getMessage());
            return null;
        }
        account.setCreatedDateTime(LocalDateTime.now());

        this.accountRepository.saveAndFlush(account);
        return accountAddBindingModel;
    }
}
