package com.pocketbudget.model.binding;

import com.pocketbudget.model.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecordDetailsBindingModel {
    private BigDecimal amount;
    private Category category;
    private String notes;
}
