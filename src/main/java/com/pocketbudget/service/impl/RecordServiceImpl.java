package com.pocketbudget.service.impl;

import com.pocketbudget.model.binding.RecordAddBindingModel;
import com.pocketbudget.model.binding.RecordDetailsBindingModel;
import com.pocketbudget.model.entity.Account;
import com.pocketbudget.model.entity.Record;
import com.pocketbudget.repository.RecordRepository;
import com.pocketbudget.service.AccountService;
import com.pocketbudget.service.RecordService;
import com.pocketbudget.util.DateTimeApplier;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public RecordAddBindingModel createRecord(String accountUUID, RecordAddBindingModel recordAddBindingModel) {
        Account account = this.accountService.getAccountByUUID(accountUUID);
        Record record = this.modelMapper.map(recordAddBindingModel, Record.class);
        record.setAccount(account);
        this.dateTimeApplier.applyDateTIme(record);

        Record savedRecord = this.recordRepository.saveAndFlush(record);
        return this.modelMapper.map(savedRecord, RecordAddBindingModel.class);
    }

    @Override
    public List<RecordDetailsBindingModel> getAllRecordsByAccountUUID(String accountUUID) {
        List<Record> allByAccountUuid = this.recordRepository.getAllByAccount_UUID(accountUUID);
        return Arrays.stream(this.modelMapper.map(allByAccountUuid, RecordDetailsBindingModel[].class)).collect(Collectors.toList());
    }
}
