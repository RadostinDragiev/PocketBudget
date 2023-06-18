package com.pocketbudget.service;

import com.pocketbudget.model.entity.UserRole;
import com.pocketbudget.model.entity.UserRoleEnum;

import java.util.Set;

public interface UserRoleService {
    Set<UserRole> getRoles(UserRoleEnum userRoleEnum);
}
