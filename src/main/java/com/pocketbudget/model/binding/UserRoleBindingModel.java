package com.pocketbudget.model.binding;

import com.pocketbudget.model.entity.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleBindingModel {
    private Set<UserRoleEnum> userRoles;
}
