package com.pocketbudget.web;

import com.pocketbudget.model.binding.UserAddBindingModel;
import com.pocketbudget.model.binding.UserLoginBindingModel;
import com.pocketbudget.model.service.UserAddServiceModel;
import com.pocketbudget.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

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
    public String register(UserAddBindingModel userAddBindingModel, UserDetails userDetails) {
        userAddBindingModel.setPassword(this.passwordEncoder.encode(userAddBindingModel.getPassword()));
        this.userService.createUser(this.modelMapper.map(userAddBindingModel, UserAddServiceModel.class));
        return null;
    }

}
