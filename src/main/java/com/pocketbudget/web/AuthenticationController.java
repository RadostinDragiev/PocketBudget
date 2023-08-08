package com.pocketbudget.web;

import com.pocketbudget.common.annotation.TrackLatency;
import com.pocketbudget.event.UserRegisterEventPublisher;
import com.pocketbudget.model.binding.RegisterUserBindingModel;
import com.pocketbudget.model.service.RegisterUserServiceModel;
import com.pocketbudget.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final UserRegisterEventPublisher userRegisterEventPublisher;

    @Autowired
    public AuthenticationController(UserService userService, PasswordEncoder passwordEncoder, ModelMapper modelMapper, UserRegisterEventPublisher userRegisterEventPublisher) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.userRegisterEventPublisher = userRegisterEventPublisher;
    }

    @TrackLatency
    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody RegisterUserBindingModel registerUserBindingModel) {
        registerUserBindingModel.setPassword(this.passwordEncoder.encode(registerUserBindingModel.getPassword()));
        RegisterUserBindingModel registerUser = this.userService.registerUser(this.modelMapper.map(registerUserBindingModel, RegisterUserServiceModel.class));
        this.userRegisterEventPublisher.publishUserRegisteredEvent(registerUser.getEmail());
        return ResponseEntity.ok().build();
    }

}
