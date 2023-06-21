package com.pocketbudget.service;

import com.pocketbudget.model.binding.RecordAddBindingModel;
import com.pocketbudget.model.binding.RecordDetailsBindingModel;
import com.pocketbudget.model.service.RecordAddServiceModel;

import java.util.List;
import java.util.Optional;

public interface RecordService {
    Optional<RecordAddBindingModel> createRecord(String accountUUID, RecordAddServiceModel recordAddServiceModel);

    Optional<RecordDetailsBindingModel> getRecordByUUID(String recordUUID, String accountUUID, String username);

    List<RecordDetailsBindingModel> getAllRecordsByAccountUUID(String accountUUID, String username);
}
