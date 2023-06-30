package com.pocketbudget.web;

import com.pocketbudget.model.binding.AccountAddBindingModel;
import com.pocketbudget.model.binding.AccountDetailsBindingModel;
import com.pocketbudget.model.service.AccountAddServiceModel;
import com.pocketbudget.service.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;
    private final ModelMapper modelMapper;

    @Autowired
    public AccountController(AccountService accountService, ModelMapper modelMapper) {
        this.accountService = accountService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/getAccounts")
    public ResponseEntity<List<AccountDetailsBindingModel>> getAllAccounts(@AuthenticationPrincipal UserDetails userDetails) {
        List<AccountDetailsBindingModel> allAccounts = this.accountService.getAllAccounts(userDetails.getUsername());
        return !allAccounts.isEmpty() ? ResponseEntity.ok(allAccounts) : ResponseEntity.notFound().build();
    }

    @GetMapping("/getAccount/{id}")
    public ResponseEntity<AccountDetailsBindingModel> getAccountById(@AuthenticationPrincipal UserDetails userDetails,
                                                                     @PathVariable("id") String accountUUID) {
        AccountDetailsBindingModel account = this.accountService.getAccountBindingModelByUUID(accountUUID, userDetails.getUsername());
        return ResponseEntity.ok(account);
    }

    @PostMapping("/createAccount")
    public ResponseEntity<AccountAddBindingModel> createAccount(@AuthenticationPrincipal UserDetails userDetails,
                                                                @Valid @RequestBody AccountAddBindingModel accountAddBindingModel,
                                                                UriComponentsBuilder uriComponentsBuilder) {
        AccountAddServiceModel accountAddServiceModel = this.modelMapper.map(accountAddBindingModel, AccountAddServiceModel.class);
        accountAddServiceModel.setUsername(userDetails.getUsername());
        AccountAddBindingModel account = this.accountService.createAccount(accountAddServiceModel);
        return ResponseEntity
                .created(uriComponentsBuilder
                        .path("/accounts/getAccount/{accountUUID}")
                        .buildAndExpand(account.getUUID())
                        .toUri())
                .build();
    }

    @PatchMapping("/updateAccount/{id}")
    public ResponseEntity<AccountAddBindingModel> updateAccount(@PathVariable("id") String accountUUID,
                                                                @Valid @RequestBody AccountAddBindingModel accountAddBindingModel) {
        AccountAddServiceModel accountAddServiceModel = this.modelMapper.map(accountAddBindingModel, AccountAddServiceModel.class);
        AccountAddBindingModel account = this.accountService.updateAccount(accountUUID, accountAddServiceModel);
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/deleteAccount/{id}")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable("id") String accountUUID) {
        return this.accountService.deleteAccount(accountUUID, userDetails.getUsername()) ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
