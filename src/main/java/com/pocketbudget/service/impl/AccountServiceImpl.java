package com.pocketbudget.service.impl;

import com.pocketbudget.model.binding.AccountAddBindingModel;
import com.pocketbudget.model.binding.AccountDetailsBindingModel;
import com.pocketbudget.model.binding.AccountDetailsWithRecordsBindingModel;
import com.pocketbudget.model.entity.Account;
import com.pocketbudget.model.entity.User;
import com.pocketbudget.model.service.AccountAddServiceModel;
import com.pocketbudget.repository.AccountRepository;
import com.pocketbudget.service.AccountService;
import com.pocketbudget.service.RecordService;
import com.pocketbudget.service.UserService;
import com.pocketbudget.util.DateTimeApplier;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final RecordService recordService;
    private final ModelMapper modelMapper;
    private final DateTimeApplier dateTimeApplier;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, UserService userService, @Lazy RecordService recordService, ModelMapper modelMapper, DateTimeApplier dateTimeApplier) {
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.recordService = recordService;
        this.modelMapper = modelMapper;
        this.dateTimeApplier = dateTimeApplier;
    }

    @Override
    public AccountAddBindingModel createAccount(AccountAddServiceModel accountAddServiceModel) {
        User user = this.userService.getUserByUsername(accountAddServiceModel.getUsername());
        Account account = this.modelMapper.map(accountAddServiceModel, Account.class);
        account.setUser(user);
        this.dateTimeApplier.applyDateTIme(account);

        Account createdAccount = this.accountRepository.saveAndFlush(account);

        return Optional
                .ofNullable(this.modelMapper.map(createdAccount, AccountAddBindingModel.class))
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public AccountDetailsBindingModel getAccountBindingModelByUUID(String uuid, String username) {
        Account account = this.accountRepository.getAccountByUUIDAndUser_Username(uuid, username)
                .orElseThrow(EntityNotFoundException::new);
        return this.modelMapper.map(account, AccountDetailsBindingModel.class);
    }


    @Override
    public AccountDetailsWithRecordsBindingModel getAccountBindingModelWithRecordByUUID(String accountUUID, String username) {
        AccountDetailsWithRecordsBindingModel account = this.modelMapper.map(getAccountBindingModelByUUID(accountUUID, username), AccountDetailsWithRecordsBindingModel.class);
        account.setCurrencyName(Currency.getInstance(account.getCurrency()).getDisplayName());
        account.setRecords(this.recordService.getAllRecordsByAccountUUID(accountUUID, username));
        return account;
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

        if (accounts.isEmpty()) {
            throw new EntityNotFoundException();
        }
        return accounts;
    }

    @Transactional
    @Override
    public boolean deleteAccount(String accountUUID, String username) {
        int deleteResult;
        try {
            deleteResult = this.accountRepository.deleteAccountByUUIDAndUser_Username(accountUUID, username);
        } catch (Exception e) {
            log.error("Failed to delete account with id: " + accountUUID);
            log.error(e.getMessage());
            return false;
        }
        return deleteResult != 0;
    }

    @Override
    public AccountAddBindingModel updateAccount(String accountUUID, AccountAddServiceModel accountAddServiceModel) {
        Account account = this.accountRepository.findById(accountUUID)
                .orElseThrow(EntityNotFoundException::new);
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

    @Override
    public Account getAccountByUUID(String accountUUID) {
        return this.accountRepository.findById(accountUUID)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public boolean isUserOwner(String username, String accountUUID) {
        AccountDetailsBindingModel account = getAccountBindingModelByUUID(accountUUID, username);
        return account != null;
    }
}
