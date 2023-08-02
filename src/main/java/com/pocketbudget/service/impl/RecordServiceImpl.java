package com.pocketbudget.service.impl;

import com.pocketbudget.exception.WithdrawCreationException;
import com.pocketbudget.model.binding.AccountAddBindingModel;
import com.pocketbudget.model.binding.RecordAddBindingModel;
import com.pocketbudget.model.binding.RecordDetailsBindingModel;
import com.pocketbudget.model.entity.Account;
import com.pocketbudget.model.entity.Record;
import com.pocketbudget.model.entity.enums.Action;
import com.pocketbudget.model.entity.enums.Category;
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
                account.setBalance(accountBalance.add(recordAmount));
                break;
            case WITHDRAW:
                withdrawOperation(account, record);
                break;
            case TRANSFER:
                transferOperation(account, record, recordAddServiceModel.getTargetAccount());
                break;
        }

        return Optional
                .ofNullable(this.modelMapper.map(saveRecord(record, account), RecordAddBindingModel.class))
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
    public RecordAddBindingModel updateRecord(String recordUUID, String accountUUID, String username, RecordAddServiceModel recordAddServiceModel) {
        List<Record> recordList = this.recordRepository.getAllByUUIDOrRelatedRecordUUIDAndAccount_UUIDAndAccount_User_Username(recordUUID, recordUUID, accountUUID, username);
        if (recordList.isEmpty()) {
            throw new EntityNotFoundException();
        }

        Record record = recordList.get(0);
        if (record.compareTo(this.modelMapper.map(recordAddServiceModel, Record.class)) == 0) {
            return this.modelMapper.map(record, RecordAddBindingModel.class);
        }

        Account account = this.accountService.getAccountByUUID(accountUUID);
        if (!record.getAction().equals(recordAddServiceModel.getAction())) {
            record.setAction(recordAddServiceModel.getAction());
            if (record.getAction().equals(Action.WITHDRAW)) {
                account.setBalance(account.getBalance().subtract(record.getAmount().multiply(new BigDecimal("2"))));
            } else if (record.getAction().equals(Action.DEPOSIT)) {
                account.setBalance(account.getBalance().add(record.getAmount().negate().multiply(new BigDecimal("2"))));
            }
            record.setAmount(record.getAmount().negate());
        }

        // FIXME: Remove check for negative funds on account
        BigDecimal recordAmount = record.getAmount();
        BigDecimal recordAddServiceModelAmount = recordAddServiceModel.getAmount();
        BigDecimal amountAfterWithdraw = recordAddServiceModelAmount.subtract(recordAmount);
        int compareTo = amountAfterWithdraw.compareTo(BigDecimal.ZERO);
        if (compareTo != 0) {
            account.setBalance(account.getBalance().add(amountAfterWithdraw));
            record.setAmount(recordAddServiceModelAmount);
        }

        if (!record.getCategory().equals(recordAddServiceModel.getCategory())) {
            record.setCategory(recordAddServiceModel.getCategory());
        }

        if (!record.getNotes().equals(recordAddServiceModel.getNotes())) {
            record.setNotes(recordAddServiceModel.getNotes());
        }

        Record updatedRecord = saveRecord(record, account);
        if (record.getRelatedRecord() != null && !record.getRelatedRecord().getAccount().getUUID().equals(recordAddServiceModel.getTargetAccount())) {
            RecordAddServiceModel relatedRecordAddServiceModel = this.modelMapper.map(record, RecordAddServiceModel.class);
            relatedRecordAddServiceModel.setCategory(record.getCategory().equals(Category.TRANSFER_WITHDRAW) ? Category.TRANSFER_DEPOSIT : Category.TRANSFER_WITHDRAW);
            relatedRecordAddServiceModel.setAmount(relatedRecordAddServiceModel.getAmount().negate());
            updateRecord(record.getRelatedRecord().getUUID(), record.getRelatedRecord().getAccount().getUUID(), username, relatedRecordAddServiceModel);
        }

        return this.modelMapper.map(updatedRecord, RecordAddBindingModel.class);
    }

    @Override
    @Transactional
    public boolean deleteRecord(String accountUUID, String recordUUID, String username) {
        Record record = this.recordRepository.getRecordByUUIDAndAccount_UUIDAndAccount_User_Username(recordUUID, accountUUID, username)
                .orElseThrow(EntityNotFoundException::new);
        int deleteResult = this.recordRepository.deleteRecordByUUIDAndAccount_UUID(recordUUID, accountUUID);
        if (deleteResult != 0) {
            Account account = this.accountService.getAccountByUUID(accountUUID);
            switch (record.getAction()) {
                case DEPOSIT:
                    account.setBalance(account.getBalance().subtract(record.getAmount()));
                    break;
                case WITHDRAW:
                    account.setBalance(account.getBalance().add(record.getAmount().negate()));
                    break;
                case TRANSFER:
                    Record relatedRecord = record.getRelatedRecord();
                    Account relatedAccount = this.accountService.getAccountByUUID(relatedRecord.getAccount().getUUID());
                    relatedAccount.setBalance(relatedAccount.getBalance().subtract(relatedRecord.getAmount()));
                    this.accountService.updateAccount(relatedRecord.getAccount().getUUID(), this.modelMapper.map(relatedAccount, AccountAddServiceModel.class));

                    account.setBalance(account.getBalance().add(record.getAmount().negate()));
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
     * @param firstAmount  BigDecimal
     * @param secondAmount BigDecimal
     */
    private boolean compareAmount(BigDecimal firstAmount, BigDecimal secondAmount) {
        boolean result = false;

        BigDecimal amountAfterWithdraw = firstAmount.subtract(secondAmount);
        int compareTo = amountAfterWithdraw.compareTo(BigDecimal.ZERO);
        if (compareTo == 1 || compareTo == 0) {
            result = true;
        }
        return result;
    }

    private void withdrawOperation(Account account, Record record) {
        BigDecimal accountBalance = account.getBalance();
        BigDecimal recordAmount = record.getAmount();
        if (compareAmount(accountBalance, recordAmount)) {
            account.setBalance(accountBalance.subtract(recordAmount));
            record.setAmount(record.getAmount().negate());
        } else {
            throw new WithdrawCreationException(INSUFFICIENT_FUNDS);
        }
    }

    private void transferOperation(Account account, Record record, String targetAccountUUID) {
        if (!targetAccountUUID.isEmpty()) {
            if (compareAmount(account.getBalance(), record.getAmount())) {
                record.setCategory(Category.TRANSFER_WITHDRAW);

                Account targetAccount = this.accountService.getAccountByUUID(targetAccountUUID);
                if (!account.getUser().getUUID().equals(targetAccount.getUser().getUUID())) {
                    throw new IllegalArgumentException(ACCOUNT_OWNED_BY_THE_USER);
                }
                targetAccount.setBalance(targetAccount.getBalance().add(record.getAmount()));

                Record targetAccountRecord = record.clone();
                targetAccountRecord.setCategory(Category.TRANSFER_DEPOSIT);
                targetAccountRecord.setAccount(targetAccount);
                targetAccountRecord.setRelatedRecord(record);

                record.setRelatedRecord(targetAccountRecord);

                withdrawOperation(account, record);

                this.accountService.updateAccount(targetAccountUUID, this.modelMapper.map(targetAccount, AccountAddServiceModel.class));
            } else {
                throw new WithdrawCreationException(INSUFFICIENT_FUNDS);
            }
        } else {
            throw new EntityNotFoundException(INVALID_ACCOUNT);
        }
    }

    private Record saveRecord(Record record, Account account) {
        this.accountService.updateAccount(account.getUUID(), this.modelMapper.map(account, AccountAddServiceModel.class));
        return this.recordRepository.saveAndFlush(record);
    }
}
