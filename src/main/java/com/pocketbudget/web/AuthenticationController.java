package com.pocketbudget.web;

import com.pocketbudget.model.binding.RegisterUserBindingModel;
import com.pocketbudget.model.service.RegisterUserServiceModel;
import com.pocketbudget.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public AuthenticationController(UserService userService, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserBindingModel> registerUser(@RequestBody RegisterUserBindingModel registerUserBindingModel) {
        registerUserBindingModel.setPassword(this.passwordEncoder.encode(registerUserBindingModel.getPassword()));
        RegisterUserBindingModel registerUser = this.userService.registerUser(this.modelMapper.map(registerUserBindingModel, RegisterUserServiceModel.class));
        return ResponseEntity.ok(registerUser);
    }

}
