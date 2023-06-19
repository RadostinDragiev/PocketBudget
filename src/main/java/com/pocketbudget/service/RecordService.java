package com.pocketbudget.service;

import com.pocketbudget.model.binding.RecordAddBindingModel;
import com.pocketbudget.model.binding.RecordDetailsBindingModel;
import com.pocketbudget.model.service.RecordAddServiceModel;

import java.util.List;

public interface RecordService {
    RecordAddBindingModel createRecord(String accountUUID, RecordAddServiceModel recordAddServiceModel);

    List<RecordDetailsBindingModel> getAllRecordsByAccountUUID(String accountUUID);
}
