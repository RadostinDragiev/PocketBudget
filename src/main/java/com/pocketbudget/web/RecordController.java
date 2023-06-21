package com.pocketbudget.web;

import com.pocketbudget.model.binding.RecordAddBindingModel;
import com.pocketbudget.model.binding.RecordDetailsBindingModel;
import com.pocketbudget.model.service.RecordAddServiceModel;
import com.pocketbudget.service.AccountService;
import com.pocketbudget.service.RecordService;
import com.pocketbudget.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/records")
public class RecordController {
    private final RecordService recordService;
    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    @Autowired
    public RecordController(RecordService recordService, AccountService accountService, ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.recordService = recordService;
        this.accountService = accountService;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @GetMapping("/{accountId}/getRecord/{recordId}")
    public ResponseEntity<RecordDetailsBindingModel> getRecord(@AuthenticationPrincipal UserDetails userDetails,
                                                               @PathVariable("accountId") String accountUUID,
                                                               @PathVariable("recordId") String recordUUID) {
        Optional<RecordDetailsBindingModel> record = this.recordService.getRecordByUUID(recordUUID, accountUUID, userDetails.getUsername());

        return record
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{accountId}/getAllRecords")
    public ResponseEntity<List<RecordDetailsBindingModel>> getAllRecords(@AuthenticationPrincipal UserDetails userDetails,
                                                                         @PathVariable("accountId") String accountUUID) {
        List<RecordDetailsBindingModel> records = this.recordService.getAllRecordsByAccountUUID(accountUUID, userDetails.getUsername());
        return !records.isEmpty() ? ResponseEntity.ok(records) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{accountId}/createRecord")
    public ResponseEntity<RecordAddBindingModel> createRecord(@AuthenticationPrincipal UserDetails userDetails,
                                                              @PathVariable("accountId") String accountUUID,
                                                              @RequestBody RecordAddBindingModel recordAddBindingModel,
                                                              UriComponentsBuilder uriComponentsBuilder) {
        if (!this.accountService.isUserOwner(userDetails.getUsername(), accountUUID)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Optional<RecordAddBindingModel> recordOpt = this.recordService.createRecord(accountUUID, this.modelMapper.map(recordAddBindingModel, RecordAddServiceModel.class));

        return recordOpt.<ResponseEntity<RecordAddBindingModel>>map(addBindingModel -> ResponseEntity
                .created(uriComponentsBuilder.path("/{accountId}/getRecord/{recordId}").buildAndExpand(accountUUID, addBindingModel.getUUID()).toUri())
                .build()).orElseGet(() -> ResponseEntity.unprocessableEntity().build());

    }
}
