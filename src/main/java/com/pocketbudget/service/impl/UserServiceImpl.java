package com.pocketbudget.service.impl;

import com.pocketbudget.model.binding.RegisterUserBindingModel;
import com.pocketbudget.model.entity.User;
import com.pocketbudget.model.entity.UserRole;
import com.pocketbudget.model.entity.enums.UserRoleEnum;
import com.pocketbudget.model.service.RegisterUserServiceModel;
import com.pocketbudget.repository.UserRepository;
import com.pocketbudget.service.UserRoleService;
import com.pocketbudget.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
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
    public User getUserByUsername(String username) {
        return this.userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
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

    @Override
    public Set<UserRole> getUserRoles(String UUID) {
        User byId = this.userRepository.findById(UUID).orElseThrow(EntityNotFoundException::new);
        return byId.getRoles();
    }

    @Override
    @Transactional
    public boolean updateUserRoles(String UUID, Set<UserRoleEnum> userRoles) {
        Set<UserRole> userNewRoles = this.userRoleService.getRolesFromCollection(userRoles);
        User user = this.userRepository.findById(UUID).orElseThrow(EntityNotFoundException::new);
        user.setRoles(userNewRoles);
        this.userRepository.saveAndFlush(user);
        return true;
    }

    private void setDateTimeValues(User user) {
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedDateTime(now);
        user.setLastLoginDateTime(now);
    }
}
