package com.pocketbudget.service.impl;

import com.pocketbudget.model.binding.RegisterUserBindingModel;
import com.pocketbudget.model.entity.User;
import com.pocketbudget.model.entity.UserRoleEnum;
import com.pocketbudget.model.service.RegisterUserServiceModel;
import com.pocketbudget.repository.UserRepository;
import com.pocketbudget.service.UserRoleService;
import com.pocketbudget.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserRoleService userRoleService, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.userRoleService = userRoleService;
        this.modelMapper = modelMapper;
    }

    @Override
    public User getUserByUUID(String userUUID) {
        return this.userRepository.findById(userUUID).orElseThrow(RuntimeException::new);
    }

    @Override
    @Transactional
    public RegisterUserBindingModel registerUser(RegisterUserServiceModel registerUserServiceModel) {
        User user = this.modelMapper.map(registerUserServiceModel, User.class);
        user.setRoles(this.userRoleService.getRoles(UserRoleEnum.USER));
        user.setActive(true);
        user.setSoftDelete(false);
        setDateTimeValues(user);
        return this.modelMapper.map(this.userRepository.saveAndFlush(user), RegisterUserBindingModel.class);
    }

    private void setDateTimeValues(User user) {
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedDateTime(now);
        user.setLastLoginDateTime(now);
    }
}
