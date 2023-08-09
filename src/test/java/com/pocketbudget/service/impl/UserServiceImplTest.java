package com.pocketbudget.service.impl;

import com.pocketbudget.model.entity.User;
import com.pocketbudget.model.entity.UserRole;
import com.pocketbudget.model.entity.enums.UserRoleEnum;
import com.pocketbudget.model.service.RegisterUserServiceModel;
import com.pocketbudget.repository.UserRepository;
import com.pocketbudget.service.UserRoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.modelmapper.ModelMapper;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    private UserRoleService userRoleService;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

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

    // TODO: Refactor registerUser method
    @Test
    void testRegisterUserShouldReturnUser() {

        RegisterUserServiceModel registerUserServiceModel =
                new RegisterUserServiceModel(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);

        when(this.userRoleService.getRoles(UserRoleEnum.USER))
                .thenReturn(Set.of(new UserRole(UserRoleEnum.USER)));
        when(this.userRepository.saveAndFlush(any(User.class)))
                .thenAnswer((Answer<User>) invocation -> {
                    Object[] arguments = invocation.getArguments();

                    if (arguments != null && arguments.length > 0 && arguments[0] != null) {
                        User customer = (User) arguments[0];
                        customer.setUUID(UUID);
                        return customer;
                    }

                    return null;
                });

        this.userService.registerUser(registerUserServiceModel);

        verify(this.userRepository, times(1)).saveAndFlush(isA(User.class));
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
        user.setUUID(UUID);
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