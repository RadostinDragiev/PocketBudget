package com.pocketbudget.model.binding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import static com.pocketbudget.constant.ErrorMessages.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserBindingModel {
    @NotNull
    @Length(min = 6, max = 20, message = USERNAME_SIZE_VALIDATION)
    private String username;

    @NotNull
    @Length(min = 8, max = 25, message = PASSWORD_MIN_SIZE)
    private String password;

    @NotNull
    @Email(message = EMAIL_VALIDATION)
    private String email;

    @NotNull
    @Length(min = 2, max = 30, message = FIRST_NAME_VALIDATION)
    private String firstName;

    @NotNull
    @Length(min = 2, max = 30, message = LAST_NAME_VALIDATION)
    private String lastName;
}
