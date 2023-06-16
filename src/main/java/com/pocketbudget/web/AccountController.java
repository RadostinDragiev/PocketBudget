package com.pocketbudget.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    @GetMapping("/createAccount")
    public ModelAndView createAccount(@AuthenticationPrincipal UserDetails userDetails) {
        ModelAndView modelAndView = new ModelAndView("create-account");
        modelAndView.addObject("user", userDetails);
        return modelAndView;
    }
}
