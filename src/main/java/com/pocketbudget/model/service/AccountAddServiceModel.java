package com.pocketbudget.model.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountAddServiceModel {
    private String name;
    private BigDecimal balance;
    private String currency;
    private String username;
}
