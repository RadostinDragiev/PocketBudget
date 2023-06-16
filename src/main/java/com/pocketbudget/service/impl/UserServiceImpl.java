package com.pocketbudget.service.impl;

import com.pocketbudget.model.binding.UserAddBindingModel;
import com.pocketbudget.model.entity.User;
import com.pocketbudget.model.entity.UserRole;
import com.pocketbudget.model.entity.UserRoleEnum;
import com.pocketbudget.model.service.UserAddServiceModel;
import com.pocketbudget.repository.UserRepository;
import com.pocketbudget.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public User getUserByUUID(String userUUID) {
        return this.userRepository.findById(userUUID).orElseThrow(RuntimeException::new);
    }

    @Override
    @Transactional
    public UserAddServiceModel createUser(UserAddServiceModel userAddServiceModel) {
        User user = this.modelMapper.map(userAddServiceModel, User.class);
        new UserRole(UserRoleEnum.USER);
        user.setRoles(new HashSet<>() {{add(new UserRole(UserRoleEnum.USER)); add(new UserRole(UserRoleEnum.ADMIN));}});
        setDateTimeValues(user);
        return this.modelMapper.map(this.userRepository.saveAndFlush(user), UserAddServiceModel.class);
    }

    private void setDateTimeValues(User user) {
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedDateTime(now);
        user.setLastLoginDateTime(now);
    }
}
