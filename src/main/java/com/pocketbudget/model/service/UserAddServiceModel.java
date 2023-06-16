package com.pocketbudget.model.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddServiceModel {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
}
