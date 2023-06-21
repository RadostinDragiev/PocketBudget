package com.pocketbudget.model.binding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountAddBindingModel {
    private String UUID;
    private String name;
    private BigDecimal balance;
    private String currency;
}
