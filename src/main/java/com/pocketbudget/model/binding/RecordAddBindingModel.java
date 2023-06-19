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
public class RecordAddBindingModel {
    private BigDecimal amount;
    private Category category;
    private String notes;
}
