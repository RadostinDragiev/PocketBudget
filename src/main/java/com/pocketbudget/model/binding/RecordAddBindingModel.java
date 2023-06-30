package com.pocketbudget.model.binding;

import com.pocketbudget.model.entity.enums.Action;
import com.pocketbudget.model.entity.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static com.pocketbudget.constant.ErrorMessages.AMOUNT_NOT_NEGATIVE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecordAddBindingModel {
    private String UUID;

    @NotNull(message = "Action must not be null")
    private Action action;

    private String targetAccountUUID;

    @Min(value = 0, message = AMOUNT_NOT_NEGATIVE)
    private BigDecimal amount;

    @NotNull(message = "Category must not be null")
    private Category category;

    private String notes;
}
