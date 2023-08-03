package com.pocketbudget.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pocketbudget.model.binding.RecordAddBindingModel;
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

import static com.pocketbudget.constant.ErrorMessages.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
class RecordControllerTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email@email.com";
    private static final String FIRST_NAME = "First";
    private static final String LAST_NAME = "Last";
    private static final String ACCOUNT_NAME = "testAccount";
    private static final String CURRENCY = "BGN";
    private static final String ACCOUNT_BALANCE = "1000";
    private static final Action RECORD_ACTION_DEPOSIT = Action.DEPOSIT;
    private static final Action RECORD_ACTION_WITHDRAW = Action.WITHDRAW;
    private final static BigDecimal CORRECT_AMOUNT = new BigDecimal("20.11");
    private final static BigDecimal NEGATIVE_AMOUNT = new BigDecimal("-20.11");
    private static final Category RECORD_CATEGORY_FOOD = Category.FOOD;
    private final static String NOTES = "The quick brown fox jumps over the lazy dog.";
    private final static String DUMMY_UUID = "123456789";
    private final static String STATUS_NOT_FOUND = "NOT_FOUND";
    private final static String STATUS_BAD_REQUEST = "BAD_REQUEST";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private AccountRepository accountRepository;

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

        Account account = new Account(ACCOUNT_NAME, new BigDecimal(ACCOUNT_BALANCE), CURRENCY, savedUser, new ArrayList<>());
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
    void testGetRecordReturnOK() throws Exception {
        Account account = this.accountRepository.findAll().get(0);
        Record record = new Record(RECORD_ACTION_DEPOSIT, CORRECT_AMOUNT, RECORD_CATEGORY_FOOD, NOTES, account, null);
        this.dateTimeApplier.applyDateTIme(record);
        Record savedRecord = this.recordRepository.saveAndFlush(record);

        this.mockMvc.perform(get("/records/{accountId}/getRecord/{recordId}", account.getUUID(), savedRecord.getUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.action").value(RECORD_ACTION_DEPOSIT.toString()))
                .andExpect(jsonPath("$.amount").value(CORRECT_AMOUNT))
                .andExpect(jsonPath("$.category").value(RECORD_CATEGORY_FOOD.toString()))
                .andExpect(jsonPath("$.notes").value(NOTES));
    }

    @WithMockUser(username = USERNAME)
    @Test
    void testGetRecordWithInvalidRecordUUIDReturnNotFound() throws Exception {
        Account account = this.accountRepository.findAll().get(0);

        this.mockMvc.perform(get("/records/{accountId}/getRecord/{recordId}", account.getUUID(), DUMMY_UUID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_NOT_FOUND));
    }

    @WithMockUser(username = USERNAME)
    @Test
    void testGetRecordWithInvalidAccountUUIDReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/records/{accountId}/getRecord/{recordId}", DUMMY_UUID, DUMMY_UUID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_NOT_FOUND));
    }

    @WithMockUser(username = USERNAME)
    @Test
    void testGetAllRecordsReturnOK() throws Exception {
        Account account = this.accountRepository.findAll().get(0);
        Record recordOne = new Record(RECORD_ACTION_DEPOSIT, CORRECT_AMOUNT, RECORD_CATEGORY_FOOD, NOTES, account, null);
        this.dateTimeApplier.applyDateTIme(recordOne);
        this.recordRepository.saveAndFlush(recordOne);
        Record recordTwo = new Record(Action.WITHDRAW, CORRECT_AMOUNT, RECORD_CATEGORY_FOOD, NOTES, account, null);
        this.dateTimeApplier.applyDateTIme(recordTwo);
        this.recordRepository.saveAndFlush(recordTwo);

        this.mockMvc.perform(get("/records/{accountId}/getAllRecords", account.getUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].action").value(RECORD_ACTION_WITHDRAW.toString()))
                .andExpect(jsonPath("$.[1].action").value(RECORD_ACTION_DEPOSIT.toString()));
    }

    @WithMockUser(username = USERNAME)
    @Test
    void testGetAllRecordsReturnNotFoundWhenNoRecords() throws Exception {
        Account account = this.accountRepository.findAll().get(0);

        this.mockMvc.perform(get("/records/{accountId}/getAllRecords", account.getUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = USERNAME)
    @Test
    void testCreateRecordWithNegativeAmountReturnBadRequest() throws Exception {
        RecordAddBindingModel recordAddBindingModel = new RecordAddBindingModel(null, RECORD_ACTION_DEPOSIT, null, NEGATIVE_AMOUNT, RECORD_CATEGORY_FOOD, NOTES);

        String uuid = this.accountRepository.findAll().get(0).getUUID();
        this.mockMvc.perform(post("/records/{id}/createRecord", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(recordAddBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionmessage.status").value(STATUS_BAD_REQUEST))
                .andExpect(jsonPath("$.exceptionmessage.errors.[0].message").value(AMOUNT_NOT_NEGATIVE));
    }

    @WithMockUser(username = USERNAME)
    @Test
    void testCreateRecordReturnOK() throws Exception {
        RecordAddBindingModel recordAddBindingModel = new RecordAddBindingModel(null, RECORD_ACTION_DEPOSIT, null, CORRECT_AMOUNT, RECORD_CATEGORY_FOOD, NOTES);

        String uuid = this.accountRepository.findAll().get(0).getUUID();
        this.mockMvc.perform(post("/records/{id}/createRecord", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(recordAddBindingModel)))
                .andExpect(status().isCreated());
    }

    @WithMockUser(username = USERNAME)
    @Test
    void testCreateRecordWithNullActionReturnBadRequest() throws Exception {
        RecordAddBindingModel recordAddBindingModel = new RecordAddBindingModel(null, null, null, CORRECT_AMOUNT, RECORD_CATEGORY_FOOD, NOTES);

        String uuid = this.accountRepository.findAll().get(0).getUUID();
        this.mockMvc.perform(post("/records/{id}/createRecord", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(recordAddBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(ACTION_MUST_NOT_BE_NULL)));
    }

    @WithMockUser(username = USERNAME)
    @Test
    void testCreateRecordWithNullCategoryReturnBadRequest() throws Exception {
        RecordAddBindingModel recordAddBindingModel = new RecordAddBindingModel(null, RECORD_ACTION_DEPOSIT, null, CORRECT_AMOUNT, null, NOTES);

        String uuid = this.accountRepository.findAll().get(0).getUUID();
        this.mockMvc.perform(post("/records/{id}/createRecord", uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(recordAddBindingModel)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(CATEGORY_MUST_NOT_BE_NULL)));
    }

    @WithMockUser(username = USERNAME)
    @Test
    void testDeleteRecordReturnOK() throws Exception {
        Account account = this.accountRepository.findAll().get(0);
        Record record = new Record(RECORD_ACTION_DEPOSIT, CORRECT_AMOUNT, RECORD_CATEGORY_FOOD, NOTES, account, null);
        this.dateTimeApplier.applyDateTIme(record);
        Record savedRecord = this.recordRepository.saveAndFlush(record);

        this.mockMvc.perform(delete("/records/{accountId}/deleteRecord/{recordId}", account.getUUID(), savedRecord.getUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private static String json(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}