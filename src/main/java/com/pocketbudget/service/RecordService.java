package com.pocketbudget.service;

import com.pocketbudget.model.binding.RecordAddBindingModel;
import com.pocketbudget.model.binding.RecordDetailsBindingModel;

import java.util.List;

public interface RecordService {
    RecordAddBindingModel createRecord(String accountUUID, RecordAddBindingModel recordAddBindingModel);

    List<RecordDetailsBindingModel> getAllRecordsByAccountUUID(String accountUUID);
}
