package com.pocketbudget.model.binding;

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
public class RecordAddBindingModel {
    private Action action;
    private String targetAccountUUID;
    private BigDecimal amount;
    private Category category;
    private String notes;
}
