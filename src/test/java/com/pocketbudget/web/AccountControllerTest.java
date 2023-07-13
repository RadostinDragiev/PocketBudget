package com.pocketbudget.web;


import com.pocketbudget.model.entity.Account;
import com.pocketbudget.model.entity.User;
import com.pocketbudget.repository.AccountRepository;
import com.pocketbudget.repository.UserRepository;
import com.pocketbudget.util.DateTimeApplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DateTimeApplier dateTimeApplier;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setUsername("gosho");
        user.setPassword("parola");
        user.setEmail("email@email.com");
        user.setFirstName("Gosho");
        user.setLastName("Peshov");
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedDateTime(now);
        user.setLastLoginDateTime(now);

        Account account = new Account("testAccount", new BigDecimal(1000), "BGN", this.userRepository.save(user), new ArrayList<>());
        this.dateTimeApplier.applyDateTIme(account);
        this.accountRepository.save(account);
    }

    @AfterEach
    public void clean() {
        this.accountRepository.deleteAll();
        this.userRepository.deleteAll();
    }

    @WithMockUser(username = "gosho")
    @Test
    public void testGetAllAccountsOK() throws Exception {
        this.mockMvc.perform(get("/accounts/getAccounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name", is("testAccount")));
    }

    @WithMockUser(username = "pesho")
    @Test
    public void testGetAllAccountsNotFound() throws Exception {
        this.mockMvc.perform(get("/accounts/getAccounts"))
                .andExpect(status().isNotFound());
    }
}