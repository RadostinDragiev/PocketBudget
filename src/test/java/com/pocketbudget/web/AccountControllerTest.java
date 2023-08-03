package com.pocketbudget.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pocketbudget.model.binding.AccountAddBindingModel;
import com.pocketbudget.model.entity.Account;
import com.pocketbudget.model.entity.Record;
import com.pocketbudget.model.entity.User;
import com.pocketbudget.model.entity.enums.Action;
import com.pocketbudget.model.entity.enums.Category;
import com.pocketbudget.repository.AccountRepository;
import com.pocketbudget.repository.RecordRepository;
import com.pocketbudget.repository.UserRepository;
import com.pocketbudget.util.DateTimeApplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;

import static com.pocketbudget.constant.ErrorMessages.BALANCE_NOT_NEGATIVE;
import static com.pocketbudget.constant.ErrorMessages.INVALID_CURRENCY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
class AccountControllerTest {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email@email.com";
    private static final String FIRST_NAME = "First";
    private static final String LAST_NAME = "Last";
    private static final String ACCOUNT_NAME = "testAccount";
    private static final String CURRENCY = "BGN";
    private static final String INVALID_SHORTER_CURRENCY = "BG";
    private static final String INVALID_LONGER_CURRENCY = "BGNN";
    private static final String CURRENCY_NAME = Currency.getInstance(CURRENCY).getDisplayName();
    private static final BigDecimal ACCOUNT_BALANCE = new BigDecimal("1000.22");
    private static final BigDecimal NEGATIVE_ACCOUNT_BALANCE = new BigDecimal("-1000.22");
    private static final Action RECORD_ACTION_DEPOSIT = Action.DEPOSIT;
    private final static BigDecimal CORRECT_AMOUNT = new BigDecimal("20.11");
    private static final Category RECORD_CATEGORY_FOOD = Category.FOOD;
    private final static String NOTES = "The quick brown fox jumps over the lazy dog.";
    private static final String DUMMY_USERNAME = "usernameDummy";
    private static final String DUMMY_PASSWORD = "passwordDummy";
    private static final String DUMMY_EMAIL = "dummy@dummy.com";
    private static final String DUMMY_FIRST_NAME = "Dummy";
    private static final String DUMMY_LAST_NAME = "Dummy";
    private final static String DUMMY_UUID = "123456789";
    private final static String DUMMY_ACCOUNT_NAME = "dummyAccount";
    private final static String FIELD_NAME = "name";
    private final static String FIELD_BALANCE = "balance";
    private final static String FIELD_CURRENCY = "currency";
    private final static String FIELD_ERROR_MESSAGE = "must not be null";
    private final static String STATUS_NOT_FOUND = "NOT_FOUND";
    private final static String STATUS_BAD_REQUEST = "BAD_REQUEST";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DateTimeApplier dateTimeApplier;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setEmail(EMAIL);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedDateTime(now);
        user.setLastLoginDateTime(now);
        User savedUser = this.userRepository.save(user);

        Account account = new Account(ACCOUNT_NAME, ACCOUNT_BALANCE, CURRENCY, savedUser, new ArrayList<>());
        this.dateTimeApplier.applyDateTIme(account);
        this.accountRepository.save(account);
    }

    @AfterEach
    public void clean() {
        this.accountRepository.deleteAll();
        this.userRepository.deleteAll();
    }

    @WithMockUser(username = USERNAME)
    @Test
    void testGetAllAccountsOK() throws Exception {
        this.mockMvc.perform(get("/accounts/getAccounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].name").value(ACCOUNT_NAME))
                .andExpect(jsonPath("$.[0].currency").value(CURRENCY))
                .andExpect(jsonPath("$.[0].currencyName").value(CURRENCY_NAME))
                .andExpect(jsonPath("$.[0].balance").value(ACCOUNT_BALANCE));
    }

    @WithMockUser(username = DUMMY_USERNAME)
    @Test
    void testGetAllAccountsNotFound() throws Exception {
        User user = new User();
        user.setUsername(DUMMY_USERNAME);
        user.setPassword(DUMMY_PASSWORD);
        user.setEmail(DUMMY_EMAIL);
        user.setFirstName(DUMMY_FIRST_NAME);
        user.setLastName(DUMMY_LAST_NAME);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedDateTime(now);
        user.setLastLoginDateTime(now);
        this.userRepository.save(user);

        this.mockMvc.perform(get("/accounts/getAccounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_NOT_FOUND));
    }

    @WithMockUser(username = USERNAME)
    @Test
    void testGetAccountByIdReturnOK() throws Exception {
        Account account = this.accountRepository.findAll().get(0);
        Record record = new Record(RECORD_ACTION_DEPOSIT, CORRECT_AMOUNT, RECORD_CATEGORY_FOOD, NOTES, account, null);
        this.dateTimeApplier.applyDateTIme(record);
        this.recordRepository.saveAndFlush(record);

        this.mockMvc.perform(get("/accounts/getAccount/{id}", account.getUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(ACCOUNT_NAME))
                .andExpect(jsonPath("$.currency").value(CURRENCY))
                .andExpect(jsonPath("$.currencyName").value(CURRENCY_NAME))
                .andExpect(jsonPath("$.balance").value(ACCOUNT_BALANCE))
                .andExpect(jsonPath("$.records.[0].action").value(RECORD_ACTION_DEPOSIT.toString()))
                .andExpect(jsonPath("$.records.[0].amount").value(CORRECT_AMOUNT))
                .andExpect(jsonPath("$.records.[0].category").value(RECORD_CATEGORY_FOOD.toString()))
                .andExpect(jsonPath("$.records.[0].notes").value(NOTES));
    }

    @WithMockUser(username = USERNAME)
    @Test
    void testGetAccountByIdReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/accounts/getAccount/{id}", DUMMY_UUID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_NOT_FOUND));
    }

    @WithMockUser(username = USERNAME)
    @Test
    void testCreateAccountReturnOK() throws Exception {
        AccountAddBindingModel accountAddBindingModel = new AccountAddBindingModel(null, DUMMY_ACCOUNT_NAME, ACCOUNT_BALANCE, CURRENCY);
        this.mockMvc.perform(post("/accounts/createAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(accountAddBindingModel)))
                .andExpect(status().isCreated());
    }

    @WithMockUser(username = USERNAME)
    @Test
    void testCreateAccountWithNullNameReturnBadRequest() throws Exception {
        AccountAddBindingModel accountAddBindingModel = new AccountAddBindingModel(null, null, ACCOUNT_BALANCE, CURRENCY);
        this.mockMvc.perform(post("/accounts/createAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(accountAddBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].field").value(FIELD_NAME))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(FIELD_ERROR_MESSAGE));
    }

    @WithMockUser(username = USERNAME)
    @Test
    void testCreateAccountWithNegativeBalanceNameReturnBadRequest() throws Exception {
        AccountAddBindingModel accountAddBindingModel = new AccountAddBindingModel(null, ACCOUNT_NAME, NEGATIVE_ACCOUNT_BALANCE, CURRENCY);
        this.mockMvc.perform(post("/accounts/createAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(accountAddBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].field").value(FIELD_BALANCE))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(BALANCE_NOT_NEGATIVE));
    }

    @WithMockUser(username = USERNAME)
    @Test
    void testCreateAccountWithInvalidShorterCurrencyReturnBadRequest() throws Exception {
        AccountAddBindingModel accountAddBindingModel = new AccountAddBindingModel(null, ACCOUNT_NAME, ACCOUNT_BALANCE, INVALID_SHORTER_CURRENCY);
        this.mockMvc.perform(post("/accounts/createAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(accountAddBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].field").value(FIELD_CURRENCY))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(INVALID_CURRENCY));
    }

    @WithMockUser(username = USERNAME)
    @Test
    void testCreateAccountWithInvalidLongerCurrencyReturnBadRequest() throws Exception {
        AccountAddBindingModel accountAddBindingModel = new AccountAddBindingModel(null, ACCOUNT_NAME, ACCOUNT_BALANCE, INVALID_LONGER_CURRENCY);
        this.mockMvc.perform(post("/accounts/createAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(accountAddBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].field").value(FIELD_CURRENCY))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(INVALID_CURRENCY));
    }

    @WithMockUser(username = USERNAME)
    @Test
    void testDeleteAccountReturnOK() throws Exception {
        Account account = this.accountRepository.findAll().get(0);

        this.mockMvc.perform(delete("/accounts/deleteAccount/{id}", account.getUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private static String json(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}