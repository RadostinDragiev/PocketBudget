package com.pocketbudget.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pocketbudget.model.binding.RegisterUserBindingModel;
import com.pocketbudget.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.pocketbudget.constant.ErrorMessages.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class AuthenticationControllerTest {

    private static final String USERNAME = "username";
    private static final String USERNAME_SHORTER = "user";
    private static final String USERNAME_LONGER = "usernameusernameuseru";
    private static final String PASSWORD = "Password123!";
    private static final String SHORTER_PASSWORD = "passwor";
    private static final String LONGER_PASSWORD = "PasswordPasswordPasswordPa";
    private static final String EMAIL = "email@email.com";
    private static final String EMAIL_WITHOUT_AT_SIGN = "emailemail.com";
    private static final String EMAIL_WITHOUT_DOMAIN = "email@";
    private static final String EMAIL_WITHOUT_USERNAME = "@email.com";
    private static final String FIRST_NAME = "First";
    private static final String LAST_NAME = "Last";
    private static final String SHORTER_NAME = "s";
    private static final String LONGER_NAME = "lastNamelastNamelastNamelastName";
    private final static String FIELD_USERNAME = "username";
    private final static String FIELD_PASSWORD = "password";
    private final static String FIELD_EMAIL = "email";
    private final static String FIELD_FIRST_NAME = "firstName";
    private final static String FIELD_LAST_NAME = "lastName";
    private final static String MUST_NOT_BE_NULL = "must not be null";
    private final static String STATUS_BAD_REQUEST = "BAD_REQUEST";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void clean() {
        this.userRepository.deleteAll();
    }

    @Test
    void testRegisterUserOK() throws Exception {
        RegisterUserBindingModel registerUserBindingModel = new RegisterUserBindingModel(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerUserBindingModel)))
                .andExpect(status().isOk());
    }

    @Test
    void testRegisterUserWithNullUsernameReturnBadRequest() throws Exception {
        RegisterUserBindingModel registerUserBindingModel = new RegisterUserBindingModel(null, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerUserBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].field").value(FIELD_USERNAME))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(MUST_NOT_BE_NULL));
    }

    @Test
    void testRegisterUserWithShorterUsernameReturnBadRequest() throws Exception {
        RegisterUserBindingModel registerUserBindingModel = new RegisterUserBindingModel(USERNAME_SHORTER, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerUserBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].field").value(FIELD_USERNAME))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(USERNAME_SIZE_VALIDATION));
    }

    @Test
    void testRegisterUserWithLongerUsernameReturnBadRequest() throws Exception {
        RegisterUserBindingModel registerUserBindingModel = new RegisterUserBindingModel(USERNAME_LONGER, PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerUserBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].field").value(FIELD_USERNAME))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(USERNAME_SIZE_VALIDATION));
    }

    @Test
    void testRegisterUserWithNullPasswordReturnBadRequest() throws Exception {
        RegisterUserBindingModel registerUserBindingModel = new RegisterUserBindingModel(USERNAME, null, EMAIL, FIRST_NAME, LAST_NAME);

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerUserBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].field").value(FIELD_PASSWORD))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(MUST_NOT_BE_NULL));
    }

    @Test
    void testRegisterUserWithShorterPasswordReturnBadRequest() throws Exception {
        RegisterUserBindingModel registerUserBindingModel = new RegisterUserBindingModel(USERNAME, SHORTER_PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerUserBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].field").value(FIELD_PASSWORD))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(PASSWORD_MIN_SIZE));
    }

    @Test
    void testRegisterUserWithLongerPasswordReturnBadRequest() throws Exception {
        RegisterUserBindingModel registerUserBindingModel = new RegisterUserBindingModel(USERNAME, LONGER_PASSWORD, EMAIL, FIRST_NAME, LAST_NAME);

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerUserBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].field").value(FIELD_PASSWORD))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(PASSWORD_MIN_SIZE));
    }

    @Test
    void testRegisterUserWithMissingEmailAtSymbolReturnBadRequest() throws Exception {
        RegisterUserBindingModel registerUserBindingModel = new RegisterUserBindingModel(USERNAME, PASSWORD, EMAIL_WITHOUT_AT_SIGN, FIRST_NAME, LAST_NAME);

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerUserBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].field").value(FIELD_EMAIL))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(EMAIL_VALIDATION));
    }

    @Test
    void testRegisterUserWithMissingEmailDomainReturnBadRequest() throws Exception {
        RegisterUserBindingModel registerUserBindingModel = new RegisterUserBindingModel(USERNAME, PASSWORD, EMAIL_WITHOUT_DOMAIN, FIRST_NAME, LAST_NAME);

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerUserBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].field").value(FIELD_EMAIL))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(EMAIL_VALIDATION));
    }

    @Test
    void testRegisterUserWithMissingEmailUsernameReturnBadRequest() throws Exception {
        RegisterUserBindingModel registerUserBindingModel = new RegisterUserBindingModel(USERNAME, PASSWORD, EMAIL_WITHOUT_USERNAME, FIRST_NAME, LAST_NAME);

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerUserBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].field").value(FIELD_EMAIL))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(EMAIL_VALIDATION));
    }

    @Test
    void testCreateUserWithNullFirstNameReturnBadRequest() throws Exception {
        RegisterUserBindingModel registerUserBindingModel = new RegisterUserBindingModel(USERNAME, PASSWORD, EMAIL, null, LAST_NAME);

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerUserBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].field").value(FIELD_FIRST_NAME))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(MUST_NOT_BE_NULL));
    }

    @Test
    void testCreateUserWithShorterFirstNameReturnBadRequest() throws Exception {
        RegisterUserBindingModel registerUserBindingModel = new RegisterUserBindingModel(USERNAME, PASSWORD, EMAIL, SHORTER_NAME, LAST_NAME);

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerUserBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].field").value(FIELD_FIRST_NAME))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(FIRST_NAME_VALIDATION));
    }

    @Test
    void testCreateUserWithLongerFirstNameReturnBadRequest() throws Exception {
        RegisterUserBindingModel registerUserBindingModel = new RegisterUserBindingModel(USERNAME, PASSWORD, EMAIL, LONGER_NAME, LAST_NAME);

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerUserBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].field").value(FIELD_FIRST_NAME))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(FIRST_NAME_VALIDATION));
    }

    @Test
    void testCreateUserWithNullLastNameReturnBadRequest() throws Exception {
        RegisterUserBindingModel registerUserBindingModel = new RegisterUserBindingModel(USERNAME, PASSWORD, EMAIL, FIRST_NAME, null);

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerUserBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].field").value(FIELD_LAST_NAME))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(MUST_NOT_BE_NULL));
    }

    @Test
    void testCreateUserWithShorterLastNameReturnBadRequest() throws Exception {
        RegisterUserBindingModel registerUserBindingModel = new RegisterUserBindingModel(USERNAME, PASSWORD, EMAIL, FIRST_NAME, SHORTER_NAME);

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerUserBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].field").value(FIELD_LAST_NAME))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(LAST_NAME_VALIDATION));
    }

    @Test
    void testCreateUserWithLongerLastNameReturnBadRequest() throws Exception {
        RegisterUserBindingModel registerUserBindingModel = new RegisterUserBindingModel(USERNAME, PASSWORD, EMAIL, FIRST_NAME, LONGER_NAME);

        this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerUserBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].field").value(FIELD_LAST_NAME))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(LAST_NAME_VALIDATION));
    }

    private static String json(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}