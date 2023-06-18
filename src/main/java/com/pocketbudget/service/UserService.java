package com.pocketbudget.service;

import com.pocketbudget.model.binding.RegisterUserBindingModel;
import com.pocketbudget.model.entity.User;
import com.pocketbudget.model.service.RegisterUserServiceModel;

public interface UserService {
    User getUserByUUID(String userUUID);

    RegisterUserBindingModel registerUser(RegisterUserServiceModel registerUserServiceModel);
}
