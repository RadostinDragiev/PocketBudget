package com.pocketbudget.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
class UserControllerTest {
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getUserRoles() {
    }

    @Test
    void changeUserRoles() {
    }
}