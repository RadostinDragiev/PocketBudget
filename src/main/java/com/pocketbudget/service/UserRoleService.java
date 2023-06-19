package com.pocketbudget.service;

import com.pocketbudget.model.entity.UserRole;
import com.pocketbudget.model.entity.enums.UserRoleEnum;

import java.util.Collection;
import java.util.Set;

public interface UserRoleService {
    Set<UserRole> getRoles(UserRoleEnum userRoleEnum);

    Set<UserRole> getRolesFromCollection(Collection<UserRoleEnum> userRoles);
}
