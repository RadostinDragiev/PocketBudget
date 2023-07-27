package com.pocketbudget.service.impl;

import com.pocketbudget.exception.WithdrawCreationException;
import com.pocketbudget.model.binding.AccountAddBindingModel;
import com.pocketbudget.model.binding.RecordAddBindingModel;
import com.pocketbudget.model.binding.RecordDetailsBindingModel;
import com.pocketbudget.model.entity.Account;
import com.pocketbudget.model.entity.Record;
import com.pocketbudget.model.entity.enums.Action;
import com.pocketbudget.model.service.AccountAddServiceModel;
import com.pocketbudget.model.service.RecordAddServiceModel;
import com.pocketbudget.repository.RecordRepository;
import com.pocketbudget.service.AccountService;
import com.pocketbudget.service.RecordService;
import com.pocketbudget.util.DateTimeApplier;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pocketbudget.constant.ErrorMessages.*;

@Service
public class RecordServiceImpl implements RecordService {
    private final RecordRepository recordRepository;
    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final DateTimeApplier dateTimeApplier;

    @Autowired
    public RecordServiceImpl(RecordRepository recordRepository, AccountService accountService, ModelMapper modelMapper, DateTimeApplier dateTimeApplier) {
        this.recordRepository = recordRepository;
        this.accountService = accountService;
        this.modelMapper = modelMapper;
        this.dateTimeApplier = dateTimeApplier;
    }

    @Override
    @Transactional
    public RecordAddBindingModel createRecord(String accountUUID, RecordAddServiceModel recordAddServiceModel) {
        Account account = this.accountService.getAccountByUUID(accountUUID);
        Record record = this.modelMapper.map(recordAddServiceModel, Record.class);
        record.setAccount(account);
        this.dateTimeApplier.applyDateTIme(record);

        Action action = recordAddServiceModel.getAction();
        BigDecimal accountBalance = account.getBalance();
        BigDecimal recordAmount = record.getAmount();
        switch (action) {
            case DEPOSIT:
                updateAccountBalance(account, accountBalance.add(recordAmount));
                break;
            case WITHDRAW:
                withdrawOperation(account, record);
                break;
            case TRANSFER:
                transferOperation(account, record, recordAddServiceModel.getTargetAccountUUID());
                break;
        }

        this.accountService.updateAccount(accountUUID, this.modelMapper.map(account, AccountAddServiceModel.class));
        Record savedRecord = this.recordRepository.saveAndFlush(record);

        return Optional
                .ofNullable(this.modelMapper.map(savedRecord, RecordAddBindingModel.class))
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public RecordDetailsBindingModel getRecordByUUID(String recordUUID, String accountUUID, String username) {
        Record record = this.recordRepository.getRecordByUUIDAndAccount_UUIDAndAccount_User_Username(recordUUID, accountUUID, username)
                .orElseThrow(EntityNotFoundException::new);
        return this.modelMapper.map(record, RecordDetailsBindingModel.class);
    }

    @Override
    public List<RecordDetailsBindingModel> getAllRecordsByAccountUUID(String accountUUID, String username) {
        List<Record> allByAccountUuid = this.recordRepository.getAllByAccount_UUIDAndAccount_User_UsernameOrderByCreatedDateTimeDesc(accountUUID, username);
        return Arrays.stream(this.modelMapper.map(allByAccountUuid, RecordDetailsBindingModel[].class)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteRecord(String accountUUID, String recordUUID, String username) {
        Record record = this.recordRepository.getRecordByUUIDAndAccount_UUIDAndAccount_User_Username(recordUUID, accountUUID, username)
                .orElseThrow(EntityNotFoundException::new);
        int deleteResult = this.recordRepository.deleteRecordByUUIDAndAccount_UUID(recordUUID, accountUUID);
        if (deleteResult != 0) {
            Account account = this.accountService.getAccountByUUID(accountUUID);
            // TODO: Add Transfer option
            switch (record.getAction()) {
                case DEPOSIT:
                    account.setBalance(account.getBalance().subtract(record.getAmount()));
                    break;
                case WITHDRAW:
                    account.setBalance(account.getBalance().add(record.getAmount()));
                    break;
            }
            AccountAddBindingModel updateAccount = this.accountService.updateAccount(accountUUID, this.modelMapper.map(account, AccountAddServiceModel.class));
            return updateAccount != null;
        }
        return false;
    }

    /**
     * compareTo : -1 (less); 0 (equal); 1 (greater);
     *
     * @param account Account
     * @param record  Record
     */
    private void withdrawOperation(Account account, Record record) {
        BigDecimal recordAmount = record.getAmount();
        BigDecimal accountBalance = account.getBalance();
        BigDecimal amountAfterWithdraw = accountBalance.subtract(recordAmount);
        int compareTo = amountAfterWithdraw.compareTo(BigDecimal.ZERO);
        if (compareTo == 1 || compareTo == 0) {
            updateAccountBalance(account, accountBalance.subtract(recordAmount));
        } else {
            throw new WithdrawCreationException(WITHDRAW_FAILED);
        }
    }

    private void transferOperation(Account account, Record record, String targetAccountUUID) {
        if (!targetAccountUUID.isEmpty()) {
            Account targetAccount = this.accountService.getAccountByUUID(targetAccountUUID);
            targetAccount.setBalance(targetAccount.getBalance().add(record.getAmount()));
            this.accountService.updateAccount(targetAccountUUID, this.modelMapper.map(targetAccount, AccountAddServiceModel.class));
            updateAccountBalance(account, account.getBalance().subtract(record.getAmount()));
        } else {
            throw new EntityNotFoundException(INVALID_ACCOUNT);
        }
    }

    private void updateAccountBalance(Account account, BigDecimal balance) {
        account.setBalance(balance);
    }
}
