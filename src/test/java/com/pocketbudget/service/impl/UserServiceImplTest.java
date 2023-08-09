package com.pocketbudget.service.impl;

import com.pocketbudget.model.entity.User;
import com.pocketbudget.model.entity.UserRole;
import com.pocketbudget.model.entity.enums.UserRoleEnum;
import com.pocketbudget.model.service.RegisterUserServiceModel;
import com.pocketbudget.repository.UserRepository;
import com.pocketbudget.repository.UserRoleRepository;
import com.pocketbudget.service.UserRoleService;
import com.pocketbudget.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final String UUID = "123456789";
    private static final String INVALID_UUID = "12345";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email@email.com";
    private static final String FIRST_NAME = "First";
    private static final String LAST_NAME = "Last";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserService userService;


    @BeforeEach
    void setUp() {
        this.userService = new UserServiceImpl(userRepository, userRoleService, modelMapper);
        this.userRoleService = new UserRoleServiceImpl(this.userRoleRepository);
    }

    @Test()
    void testGetUserByUsernameShouldReturnUser() {
        User user = setUser();
        when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(user));

        User username = this.userService.getUserByUsername(USERNAME);
        assertEquals(USERNAME, username.getUsername());
    }

    @Test
    void testGetUserByUsernameWithInvalidUsernameShouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> this.userService.getUserByUsername(USERNAME));
    }

    @Test
    void testRegisterUserShouldReturnUser() {

        RegisterUserServiceModel registerUserServiceModel =
                new RegisterUserServiceModel(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);


        when(this.userRoleService.getRoles(UserRoleEnum.USER))
                .thenReturn(new HashSet<>(List.of(new UserRole(UserRoleEnum.USER))));

        this.userService.registerUser(registerUserServiceModel);

        //verify(this.userRoleRepository.getUserRoleByRole(any(UserRoleEnum.class)));
        verify(this.userRepository.saveAndFlush(any(User.class)));
    }

    @Test
    void testGetUserRolesShouldReturnUserRoles() {
        User user = setUser();
        user.setRoles(new HashSet<>(List.of(new UserRole(UserRoleEnum.USER))));
        when(this.userRepository.findById(UUID))
                .thenReturn(Optional.of(user));

        Set<UserRole> userRoles = this.userService.getUserRoles(UUID);
        assertEquals(userRoles.size(), 1);
        assertEquals(((UserRole) userRoles.toArray()[0]).getRole(), UserRoleEnum.USER);
    }

    @Test
    void testGetUserRolesShouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> this.userService.getUserRoles(INVALID_UUID));
    }

    @Test
    void testUpdateUserRolesShouldReturnTrue() {
        User user = setUser();
        when(this.userRepository.findById(UUID))
                .thenReturn(Optional.of(user));

        assertTrue(this.userService.updateUserRoles(UUID, new HashSet<>(List.of(UserRoleEnum.USER, UserRoleEnum.ADMIN))));
    }

    @Test
    void testUpdateUserRolesShouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> this.userService.getUserRoles(INVALID_UUID));
    }

    private User setUser() {
        User user = new User();
        user.setUUID("123456789");
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setEmail(EMAIL);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedDateTime(now);
        user.setLastLoginDateTime(now);

        return user;
    }
}