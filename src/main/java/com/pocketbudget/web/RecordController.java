package com.pocketbudget.web;

import com.pocketbudget.common.annotation.TrackLatency;
import com.pocketbudget.model.binding.RecordAddBindingModel;
import com.pocketbudget.model.binding.RecordDetailsBindingModel;
import com.pocketbudget.model.service.RecordAddServiceModel;
import com.pocketbudget.service.AccountService;
import com.pocketbudget.service.RecordService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
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

    @TrackLatency()
    @GetMapping("/{accountId}/getRecord/{recordId}")
    public ResponseEntity<RecordDetailsBindingModel> getRecord(@AuthenticationPrincipal UserDetails userDetails,
                                                               @PathVariable("accountId") String accountUUID,
                                                               @PathVariable("recordId") String recordUUID) {
        RecordDetailsBindingModel record = this.recordService.getRecordByUUID(recordUUID, accountUUID, userDetails.getUsername());

        return ResponseEntity.ok(record);
    }

    @TrackLatency()
    @GetMapping("/{accountId}/getAllRecords")
    public ResponseEntity<List<RecordDetailsBindingModel>> getAllRecords(@AuthenticationPrincipal UserDetails userDetails,
                                                                         @PathVariable("accountId") String accountUUID) {
        List<RecordDetailsBindingModel> records = this.recordService.getAllRecordsByAccountUUID(accountUUID, userDetails.getUsername());
        return !records.isEmpty() ? ResponseEntity.ok(records) : ResponseEntity.notFound().build();
    }

    @TrackLatency()
    @PostMapping("/{accountId}/createRecord")
    public ResponseEntity<RecordAddBindingModel> createRecord(@AuthenticationPrincipal UserDetails userDetails,
                                                              @PathVariable("accountId") String accountUUID,
                                                              @Valid @RequestBody RecordAddBindingModel recordAddBindingModel,
                                                              UriComponentsBuilder uriComponentsBuilder) {
        if (!this.accountService.isUserOwner(userDetails.getUsername(), accountUUID)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        RecordAddBindingModel record = this.recordService.createRecord(accountUUID, this.modelMapper.map(recordAddBindingModel, RecordAddServiceModel.class));

        return ResponseEntity
                .created(uriComponentsBuilder
                        .path("/records/{accountId}/getRecord/{recordId}")
                        .buildAndExpand(accountUUID, record.getUUID())
                        .toUri())
                .build();
    }

    @DeleteMapping("/{accountId}/deleteRecord/{recordId}")
    private ResponseEntity<Void> deleteRecord(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable("accountId") String accountUUID,
                                              @PathVariable("recordId") String recordUUID) {
        return this.recordService.deleteRecord(accountUUID, recordUUID, userDetails.getUsername()) ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
