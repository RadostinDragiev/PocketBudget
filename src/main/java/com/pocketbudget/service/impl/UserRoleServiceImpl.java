package com.pocketbudget.service.impl;

import com.pocketbudget.model.entity.UserRole;
import com.pocketbudget.model.entity.UserRoleEnum;
import com.pocketbudget.repository.UserRoleRepository;
import com.pocketbudget.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserRoleServiceImpl implements UserRoleService {
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public UserRoleServiceImpl(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public Set<UserRole> getRoles(UserRoleEnum userRoleEnum) {
        Set<UserRole> userRoleEnumSet = new HashSet<>();
        userRoleEnumSet.add(this.userRoleRepository.getUserRoleByRole(userRoleEnum));
        return userRoleEnumSet;
    }

    @Override
    public Set<UserRole> getRolesFromCollection(Collection<UserRoleEnum> userRoles) {
        return this.userRoleRepository.getUserRolesByRoleIn(userRoles);
    }
}
