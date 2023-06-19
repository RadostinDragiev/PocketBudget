package com.pocketbudget.service.impl;

import com.pocketbudget.model.binding.AccountAddBindingModel;
import com.pocketbudget.model.binding.AccountDetailsBindingModel;
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

import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<AccountDetailsBindingModel> getAllAccounts(String username) {
        User user = this.userService.getUserByUsername(username);
        List<AccountDetailsBindingModel> accounts = this.accountRepository
                .getAllByUser_UUID(user.getUUID())
                .stream()
                .map(account -> this.modelMapper.map(account, AccountDetailsBindingModel.class))
                .collect(Collectors.toList());
        accounts.forEach(accountDetailsBindingModel -> accountDetailsBindingModel.setCurrencyName(Currency.getInstance(accountDetailsBindingModel.getCurrency()).getDisplayName()));

        return accounts;
    }

    @Override
    public boolean deleteAccount(String accountUUID) {
        try {
            this.accountRepository.deleteById(accountUUID);
        } catch (Exception e) {
            log.error("Failed to delete account with id: " + accountUUID);
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public AccountAddBindingModel updateAccount(String accountUUID, AccountAddServiceModel accountAddServiceModel) {
        Account account = this.accountRepository.findById(accountUUID).orElseThrow(UnsupportedOperationException::new);
        if (!account.getName().equals(accountAddServiceModel.getName())) {
            account.setName(accountAddServiceModel.getName());
        }

        if (!account.getCurrency().equals(accountAddServiceModel.getCurrency())) {
            account.setCurrency(accountAddServiceModel.getCurrency());
        }

        if (!account.getBalance().equals(accountAddServiceModel.getBalance())) {
            account.setBalance(accountAddServiceModel.getBalance());
        }

        Account updatedAccount = this.accountRepository.save(account);
        return this.modelMapper.map(updatedAccount, AccountAddBindingModel.class);
    }
}
