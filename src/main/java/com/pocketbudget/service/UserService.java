package com.pocketbudget.service;

import com.pocketbudget.model.binding.UserAddBindingModel;
import com.pocketbudget.model.entity.User;
import com.pocketbudget.model.service.UserAddServiceModel;

public interface UserService {
    User getUserByUUID(String userUUID);

    UserAddServiceModel createUser(UserAddServiceModel userAddServiceModel);
}
