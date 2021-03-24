package com.test.intelliartstestapp.service.impl;

import com.test.intelliartstestapp.model.Currency;
import com.test.intelliartstestapp.model.Expense;
import com.test.intelliartstestapp.model.TotalAmount;
import com.test.intelliartstestapp.repository.ExpenseRepository;
import com.test.intelliartstestapp.repository.TotalAmountRepository;
import com.test.intelliartstestapp.rest.dto.LatestCurrencyRateDto;
import com.test.intelliartstestapp.rest.dto.TotalAmountAndCurrency;
import com.test.intelliartstestapp.service.ExpenseService;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ExpenseServiceImplTest {
    private ExpenseService expenseService;

    @Autowired
    public ExpenseServiceImplTest(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @MockBean
    private ExpenseRepository expenseRepository;
    @MockBean
    private RestTemplate restTemplate;
    @MockBean
    private TotalAmountRepository totalAmountRepository;

    private Expense expense1 = new Expense(1L, LocalDate.parse("2021-04-25"), new BigDecimal("2.85"), Currency.USD, "Yogurt");
    private Expense expense2 = new Expense(2L, LocalDate.parse("2021-04-22"), new BigDecimal("12"), Currency.USD, "Salmon");
    private Expense expense3 = new Expense(3L, LocalDate.parse("2021-04-25"), new BigDecimal("3"), Currency.USD, "French fries");
    private Expense expense4 = new Expense(4L, LocalDate.parse("2021-04-27"), new BigDecimal("4.75"), Currency.EUR, "Beer");
    private Expense expense5 = new Expense(5L, LocalDate.parse("2021-04-27"), new BigDecimal("25.5"), Currency.UAH, "Sweets");

    @Test
    @Transactional
    void getTotalAmount() {
        Map<String, String> rates = new HashMap<>();
        rates.put("USD", "1.18");
        rates.put("PLN", "4.61");
        rates.put("UAH", "32.95");
        LatestCurrencyRateDto latestCurrencyRateDto = new LatestCurrencyRateDto(false, 0.00f, null, null, rates);

        TotalAmount totalAmount = new TotalAmount(1L, "16.75");

        when(totalAmountRepository.getOne(1L)).thenReturn(totalAmount);

        when(restTemplate.getForEntity(
                "http://data.fixer.io/api/latest?access_key=4ae67f6c83d66b76d987de1469e77131&symbols=UAH"
                , LatestCurrencyRateDto.class, 1))
                .thenReturn(ResponseEntity.of(Optional.of(latestCurrencyRateDto)));

        TotalAmountAndCurrency expected = new TotalAmountAndCurrency(new BigDecimal("552.32"), Currency.UAH);
        assertEquals(expected, expenseService.getTotalAmount(Currency.UAH));
    }

    @Test
    @Transactional
    void save() {
        TotalAmount totalAmount = new TotalAmount(1L, "16.75");

        when(totalAmountRepository.getOne(1L)).thenReturn(totalAmount);

        boolean isSaved = expenseService.save(expense3);

        assertTrue(isSaved);
        verify(expenseRepository, times(1)).save(expense3);
    }

    @Test
    @Transactional
    void delete() {
        TotalAmount totalAmount = new TotalAmount(1L, "16.75");

        when(totalAmountRepository.getOne(1L)).thenReturn(totalAmount);
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