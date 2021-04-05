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
import org.mockito.Mockito;
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
    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseServiceImplTest(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @MockBean
    private RestTemplate restTemplate;

    private Expense expense1;
    private Expense expense2;
    private Expense expense3;

    @BeforeEach
    void addExpenses() {
        expenseService = new ExpenseServiceImpl(expenseRepository, restTemplate);
        expense1 = expenseService.save(new Expense(1L, LocalDate.parse("2021-04-25"), new BigDecimal("2.85"), Currency.USD, "Yogurt"));
        expense2 = expenseService.save(new Expense(2L, LocalDate.parse("2021-04-22"), new BigDecimal("12.00"), Currency.USD, "Salmon"));
        expense3 = expenseService.save(new Expense(3L, LocalDate.parse("2021-04-25"), new BigDecimal("3.00"), Currency.USD, "French fries"));
    }

    @AfterEach
    void deleteExpenses() {
        expenseService.delete(expense1.getDate());
        expenseService.delete(expense2.getDate());
    }

    @Test
    @Transactional
    void testGetTotalAmount() {
        Map<String, String> rates = new HashMap<>();
        rates.put("USD", "1.18");
        rates.put("PLN", "4.61");
        rates.put("UAH", "32.95");
        LatestCurrencyRateDto latestCurrencyRateDto = new LatestCurrencyRateDto(false, 0.00f, null, null, rates);

        when(restTemplate.getForEntity(anyString(), any(), eq(1)))
                .thenReturn(ResponseEntity.of(Optional.of(latestCurrencyRateDto)));

        TotalAmountAndCurrency expected = new TotalAmountAndCurrency(new BigDecimal("498.53"), Currency.UAH);
        TotalAmountAndCurrency actual = expenseService.getTotalAmount(Currency.UAH);

        Mockito.verify(restTemplate, times(1)).getForEntity(anyString(), any(), eq(1));

        assertEquals(expected, actual);
    }

    @Test
    @Transactional
    void testSave() {
        assertEquals(expense1, expenseRepository.findById(expense1.getId()).orElseThrow());
        assertEquals(expense2, expenseRepository.findById(expense2.getId()).orElseThrow());
        assertEquals(expense3, expenseRepository.findById(expense3.getId()).orElseThrow());
    }

    @Test
    @Transactional
    void testDelete() {
        assertEquals(expense2, expenseRepository.findById(expense2.getId()).orElseThrow());

        expenseService.delete(LocalDate.parse("2021-04-25"));

        assertNull(expenseService.getAll().get(expense1.getDate().toString()));
        assertNotNull(expenseService.getAll().get(expense2.getDate().toString()));
    }
}