package com.test.intelliartstestapp.service.impl;

import com.test.intelliartstestapp.model.Currency;
import com.test.intelliartstestapp.model.Expense;
import com.test.intelliartstestapp.repository.ExpenseRepository;
import com.test.intelliartstestapp.rest.dto.LatestCurrencyRateDto;
import com.test.intelliartstestapp.rest.dto.TotalAmountAndCurrency;
import com.test.intelliartstestapp.service.ExpenseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ExpenseServiceImplTest {
    private ExpenseService expenseService;
    private ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseServiceImplTest(ExpenseService expenseService, ExpenseRepository expenseRepository) {
        this.expenseService = expenseService;
        this.expenseRepository = expenseRepository;
    }

    @MockBean
    private RestTemplate restTemplate;

    private Expense expense1 = new Expense(1L, LocalDate.parse("2021-04-25"), new BigDecimal("2.85"), Currency.USD, "Yogurt");
    private Expense expense2 = new Expense(2L, LocalDate.parse("2021-04-22"), new BigDecimal("12"), Currency.USD, "Salmon");
    private Expense expense3 = new Expense(3L, LocalDate.parse("2021-04-25"), new BigDecimal("3"), Currency.USD, "French fries");

    @BeforeEach
    void addExpenses() {
        expenseService.save(expense1);
        expenseService.save(expense2);
        expenseService.save(expense3);
    }

    @AfterEach
    void deleteExpenses() {
        expenseService.delete(expense1.getDate());
        expenseService.delete(expense2.getDate());
        expenseService.delete(expense3.getDate());
    }

    @Test
    @Transactional
    void testGetTotalAmount() {
        Map<String, String> rates = new HashMap<>();
        rates.put("USD", "1.18");
        rates.put("PLN", "4.61");
        rates.put("UAH", "32.95");
        LatestCurrencyRateDto latestCurrencyRateDto = new LatestCurrencyRateDto(false, 0.00f, null, null, rates);

        when(restTemplate.getForEntity(
                anyString(), any(), eq(1)))
                .thenReturn(ResponseEntity.of(Optional.of(latestCurrencyRateDto)));

        TotalAmountAndCurrency expected = new TotalAmountAndCurrency(new BigDecimal("498.27"), Currency.UAH);
        assertEquals(expected, expenseService.getTotalAmount(Currency.UAH));
    }

    @Test
    @Transactional
    void testSave() {
        assertEquals(expense1, expenseRepository.findById(expense1.getId()).get());
        assertEquals(expense2, expenseRepository.findById(expense2.getId()).get());
        assertEquals(expense3, expenseRepository.findById(expense3.getId()).get());
    }

    @Test
    @Transactional
    void testDelete() {
        assertEquals(expense1, expenseRepository.findById(expense1.getId()).get());

        expenseService.delete(LocalDate.parse("2021-04-25"));

        assertNull(expenseService.getAll().get(expense1.getDate()));
        assertNotNull(expenseService.getAll().get(expense2.getDate()));
    }
}