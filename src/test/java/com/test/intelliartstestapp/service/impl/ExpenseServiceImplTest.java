package com.test.intelliartstestapp.service.impl;

import com.test.intelliartstestapp.model.Currency;
import com.test.intelliartstestapp.model.Expense;
import com.test.intelliartstestapp.repository.ExpenseRepository;
import com.test.intelliartstestapp.repository.TotalAmountRepository;
import com.test.intelliartstestapp.rest.dto.LatestCurrencyRateDto;
import com.test.intelliartstestapp.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ExpenseServiceImplTest {
    private ExpenseService expenseService;
    private TotalAmountRepository totalAmountRepository;

    @Autowired
    public ExpenseServiceImplTest(ExpenseService expenseService, TotalAmountRepository totalAmountRepository) {
        this.expenseService = expenseService;
        this.totalAmountRepository = totalAmountRepository;
    }

    @MockBean
    private ExpenseRepository expenseRepository;

    private Expense expense1;
    private Expense expense2;
    private Expense expense3;
    private Expense expense4;
    private Expense expense5;

    @BeforeEach
    void init() {
        expense1 = new Expense(1L, LocalDate.parse("2021-04-25"), new BigDecimal("2.85"), Currency.USD, "Yogurt");
        expense2 = new Expense(2L, LocalDate.parse("2021-04-22"), new BigDecimal("12"), Currency.USD, "Salmon");
        expense3 = new Expense(3L, LocalDate.parse("2021-04-25"), new BigDecimal("3"), Currency.USD, "French fries");
        expense4 = new Expense(4L, LocalDate.parse("2021-04-27"), new BigDecimal("4.75"), Currency.EUR, "Beer");
        expense5 = new Expense(5L, LocalDate.parse("2021-04-27"), new BigDecimal("25.5"), Currency.UAH, "Sweets");
    }

    @Test
    void getTotalAmount() {
        Map<String, String> r = new HashMap<>();
        r.put("USD", "1.186022");
        r.put("PLN", "4.614845");
        r.put("UAH", "32.956032");
        LatestCurrencyRateDto latestCurrencyRateDto = new LatestCurrencyRateDto(false, 0.00f, null, null, r);

        when(totalAmountRepository.getOne(1L).getTotalAmount()).thenReturn(new BigDecimal("16.75"));

//        when(ra)
    }

    @Test
    @Transactional
    void save() {
        boolean isSaved = expenseService.save(expense3);

        assertTrue(isSaved);
        verify(expenseRepository, times(1)).save(expense3);
    }

    @Test
    @Transactional
    void delete() {
        when(expenseRepository.findAll()).thenReturn(Arrays.asList(expense1, expense2, expense3));

        boolean isDeleted = expenseService.delete(LocalDate.parse("2021-04-25"));

        assertTrue(isDeleted);
        verify(expenseRepository, times(2)).delete(any(Expense.class));
    }

    @Test
    void getAll() {
        when(expenseRepository.findAll()).thenReturn(Arrays.asList(expense3, expense4, expense5));

        Map<LocalDate, List<Expense>> expectedMap = new TreeMap<>();
        expectedMap.put(LocalDate.parse("2021-04-25"), Collections.singletonList(expense3));
        expectedMap.put(LocalDate.parse("2021-04-27"), Arrays.asList(expense4, expense5));

        assertEquals(expectedMap, expenseService.getAll());
    }
}