package com.pocketbudget.web;

import com.pocketbudget.model.binding.RecordAddBindingModel;
import com.pocketbudget.model.binding.RecordDetailsBindingModel;
import com.pocketbudget.model.service.RecordAddServiceModel;
import com.pocketbudget.service.AccountService;
import com.pocketbudget.service.RecordService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/records")
public class RecordController {
    private final RecordService recordService;
    private final AccountService accountService;
    private final ModelMapper modelMapper;

    @Autowired
    public RecordController(RecordService recordService, AccountService accountService, ModelMapper modelMapper) {
        this.recordService = recordService;
        this.accountService = accountService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{accountId}/getRecords")
    public ResponseEntity<List<RecordDetailsBindingModel>> getAllRecords(@PathVariable("accountId") String accountUUID) {
        return ResponseEntity.ok(this.recordService.getAllRecordsByAccountUUID(accountUUID));
    }

    @PostMapping("/{accountId}/createRecord")
    public ResponseEntity<RecordAddBindingModel> createRecord(@AuthenticationPrincipal UserDetails userDetails,
                                                              @PathVariable("accountId") String accountUUID,
                                                              @RequestBody RecordAddBindingModel recordAddBindingModel) {
        if (!this.accountService.isUserOwner(userDetails.getUsername(), accountUUID)) {
            return ResponseEntity.badRequest().build();
        }
        RecordAddBindingModel record = this.recordService.createRecord(accountUUID, this.modelMapper.map(recordAddBindingModel, RecordAddServiceModel.class));
        return ResponseEntity.ok(record);
    }
}
