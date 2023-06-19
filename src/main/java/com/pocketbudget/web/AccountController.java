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
        return ResponseEntity.ok(allAccounts);
    }

    @PostMapping("/createAccount")
    public ResponseEntity<AccountAddBindingModel> createAccount(@AuthenticationPrincipal UserDetails userDetails,
                                                                @RequestBody AccountAddBindingModel accountAddBindingModel) {
        AccountAddServiceModel accountAddServiceModel = this.modelMapper.map(accountAddBindingModel, AccountAddServiceModel.class);
        accountAddServiceModel.setUsername(userDetails.getUsername());

        return ResponseEntity.ok(this.accountService.createAccount(accountAddServiceModel));
    }

    @PatchMapping("/{id}/updateAccount")
    public ResponseEntity<AccountAddBindingModel> updateAccount(@PathVariable("id") String accountUUID, @RequestBody AccountAddBindingModel accountAddBindingModel) {
        return ResponseEntity.ok(this.accountService.updateAccount(accountUUID, this.modelMapper.map(accountAddBindingModel, AccountAddServiceModel.class)));
    }

    @DeleteMapping("/{id}/deleteAccount")
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") String accountUUID) {
        return this.accountService.deleteAccount(accountUUID) ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
