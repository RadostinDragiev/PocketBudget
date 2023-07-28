package com.pocketbudget.model.service;

import com.pocketbudget.model.entity.enums.Action;
import com.pocketbudget.model.entity.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecordAddServiceModel {
    private Action action;
    private String targetAccount;
    private BigDecimal amount;
    private Category category;
    private String notes;
}
