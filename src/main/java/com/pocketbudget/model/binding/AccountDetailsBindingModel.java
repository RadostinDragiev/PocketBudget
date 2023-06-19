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
public class AccountDetailsBindingModel {
    private String name;
    private String currency;
    private String currencyName;
    private BigDecimal balance;
}
