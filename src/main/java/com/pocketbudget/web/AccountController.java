package com.pocketbudget.web;

import com.pocketbudget.model.binding.AccountAddBindingModel;
import com.pocketbudget.model.service.AccountAddServiceModel;
import com.pocketbudget.service.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/createAccount")
    public ResponseEntity<AccountAddBindingModel> createAccount(@AuthenticationPrincipal UserDetails userDetails,
                                                                @RequestBody AccountAddBindingModel accountAddBindingModel) {
        AccountAddServiceModel accountAddServiceModel = this.modelMapper.map(accountAddBindingModel, AccountAddServiceModel.class);
        accountAddServiceModel.setUsername(userDetails.getUsername());

        return ResponseEntity.ok(this.accountService.createAccount(accountAddServiceModel));
    }
}
