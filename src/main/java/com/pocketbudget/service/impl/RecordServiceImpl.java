package com.pocketbudget.service.impl;

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

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        AccountAddServiceModel accountAddServiceModel = this.modelMapper.map(account, AccountAddServiceModel.class);
        BigDecimal accountBalance = accountAddServiceModel.getBalance();
        BigDecimal recordAmount = record.getAmount();
        switch (action) {
            case DEPOSIT:
                accountAddServiceModel.setBalance(accountBalance.add(recordAmount));
                break;
            case WITHDRAW:
                BigDecimal amountAfterWithdraw = account.getBalance().subtract(recordAmount);
                // -1 (less); 0 (equal); 1 (greater);
                int compareTo = amountAfterWithdraw.compareTo(BigDecimal.ZERO);
                if (compareTo == 1 || compareTo == 0) {
                    accountAddServiceModel.setBalance(accountBalance.subtract(recordAddServiceModel.getAmount()));
                } else {
                    // FIXME: Fix error handling
                    throw new UnsupportedOperationException();
                }
                break;
            case TRANSFER:
                String targetAccountUUID = recordAddServiceModel.getTargetAccountUUID();
                if (!targetAccountUUID.isEmpty()) {
                    Account targetAccount = this.accountService.getAccountByUUID(targetAccountUUID);
                    targetAccount.setBalance(targetAccount.getBalance().add(recordAmount));
                    this.accountService.updateAccount(targetAccountUUID, this.modelMapper.map(targetAccount, AccountAddServiceModel.class));

                    accountAddServiceModel.setBalance(account.getBalance().subtract(record.getAmount()));
                }
                // TODO: Fix error handling
                break;
        }

        this.accountService.updateAccount(accountUUID, accountAddServiceModel);
        Record savedRecord = this.recordRepository.saveAndFlush(record);

        return this.modelMapper.map(savedRecord, RecordAddBindingModel.class);
    }

    @Override
    public List<RecordDetailsBindingModel> getAllRecordsByAccountUUID(String accountUUID) {
        List<Record> allByAccountUuid = this.recordRepository.getAllByAccount_UUIDOrderByCreatedDateTimeDesc(accountUUID);
        return Arrays.stream(this.modelMapper.map(allByAccountUuid, RecordDetailsBindingModel[].class)).collect(Collectors.toList());
    }
}
