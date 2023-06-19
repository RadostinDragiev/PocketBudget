package com.pocketbudget.web;

import com.pocketbudget.model.binding.RecordAddBindingModel;
import com.pocketbudget.model.binding.RecordDetailsBindingModel;
import com.pocketbudget.service.RecordService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/records")
public class RecordController {
    private final RecordService recordService;
    private final ModelMapper modelMapper;

    public RecordController(RecordService recordService, ModelMapper modelMapper) {
        this.recordService = recordService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{accountId}/getRecords")
    public ResponseEntity<List<RecordDetailsBindingModel>> getAllRecords(@PathVariable("accountId") String accountUUID) {
        return ResponseEntity.ok(this.recordService.getAllRecordsByAccountUUID(accountUUID));
    }

    @PostMapping("/{accountId}/createRecord")
    public ResponseEntity<RecordAddBindingModel> createRecord(@PathVariable("accountId") String accountUUID,
                                             @RequestBody RecordAddBindingModel recordAddBindingModel) {
        RecordAddBindingModel record = this.recordService.createRecord(accountUUID, recordAddBindingModel);
        return ResponseEntity.ok(record);
    }
}
