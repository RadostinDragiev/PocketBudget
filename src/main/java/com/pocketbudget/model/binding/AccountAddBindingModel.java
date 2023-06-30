package com.pocketbudget.model.binding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static com.pocketbudget.constant.ErrorMessages.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountAddBindingModel {
    private String UUID;

    @NotNull
    private String name;

    @Min(value = 0, message = BALANCE_NOT_NEGATIVE)
    @NotNull
    private BigDecimal balance;

    @Length(min = 3, max = 3, message = INVALID_CURRENCY)
    @NotNull
    private String currency;
}
