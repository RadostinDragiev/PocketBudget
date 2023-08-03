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

import static com.pocketbudget.constant.ErrorMessages.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecordAddBindingModel {
    private String UUID;

    @NotNull(message = ACTION_MUST_NOT_BE_NULL)
    private Action action;

    private String targetAccount;

    @Min(value = 0, message = AMOUNT_NOT_NEGATIVE)
    private BigDecimal amount;

    @NotNull(message = CATEGORY_MUST_NOT_BE_NULL)
    private Category category;

    private String notes;
}
