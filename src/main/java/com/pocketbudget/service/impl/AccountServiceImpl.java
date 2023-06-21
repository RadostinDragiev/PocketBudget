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
    public Optional<AccountAddBindingModel> createAccount(AccountAddServiceModel accountAddServiceModel) {
        User user = this.userService.getUserByUsername(accountAddServiceModel.getUsername());
        Account account = this.modelMapper.map(accountAddServiceModel, Account.class);
        account.setUser(user);
        this.dateTimeApplier.applyDateTIme(account);

        Optional<Account> createdAccount = Optional.of(this.accountRepository.saveAndFlush(account));

        return Optional.ofNullable(this.modelMapper.map(createdAccount, AccountAddBindingModel.class));
    }

    @Override
    public Optional<AccountDetailsBindingModel> getAccountBindingModelByUUID(String uuid, String username) {
        User userByUsername = this.userService.getUserByUsername(username);
        Optional<Account> byId = this.accountRepository.getAccountByUUIDAndUser_UUID(uuid, userByUsername.getUUID());
        return Optional.ofNullable(this.modelMapper.map(byId, AccountDetailsBindingModel.class));
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

    @Transactional
    @Override
    public boolean deleteAccount(String accountUUID, String username) {
        int b;
        try {
            b = this.accountRepository.deleteAccountByUUIDAndUser_Username(accountUUID, username);
            System.out.println();
        } catch (Exception e) {
            log.error("Failed to delete account with id: " + accountUUID);
            log.error(e.getMessage());
            return false;
        }
        return b != 0;
    }

    @Override
    public Optional<AccountAddBindingModel> updateAccount(String accountUUID, AccountAddServiceModel accountAddServiceModel) {
        // FIXME: Fix error handling
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
        return Optional.ofNullable(this.modelMapper.map(updatedAccount, AccountAddBindingModel.class));
    }

    @Override
    public Account getAccountByUUID(String accountUUID) {
        // FIXME: Fix when user is not present
        return this.accountRepository.findById(accountUUID).orElseThrow(UnsupportedOperationException::new);
    }

    @Override
    public boolean isUserOwner(String username, String accountUUID) {
        Optional<AccountDetailsBindingModel> account = getAccountBindingModelByUUID(accountUUID, username);
        return account.isPresent();
    }
}
