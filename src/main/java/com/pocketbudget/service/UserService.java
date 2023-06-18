package com.pocketbudget.service;

import com.pocketbudget.model.binding.RegisterUserBindingModel;
import com.pocketbudget.model.entity.User;
import com.pocketbudget.model.entity.UserRole;
import com.pocketbudget.model.entity.UserRoleEnum;
import com.pocketbudget.model.service.RegisterUserServiceModel;

import java.util.Set;

public interface UserService {
    User getUserByUUID(String userUUID);

    RegisterUserBindingModel registerUser(RegisterUserServiceModel registerUserServiceModel);

    Set<UserRole> getUserRoles(String UUID);

    boolean updateUserRoles(String UUID, Set<UserRoleEnum> userRoles);
}
