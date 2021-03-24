package com.test.intelliartstestapp.service.impl;

import com.test.intelliartstestapp.model.Currency;
import com.test.intelliartstestapp.model.Expense;
import com.test.intelliartstestapp.repository.ExpenseRepository;
import com.test.intelliartstestapp.rest.dto.LatestCurrencyRateDto;
import com.test.intelliartstestapp.rest.dto.TotalAmountAndCurrency;
import com.test.intelliartstestapp.service.ExpenseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {
    private ExpenseRepository expenseRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public ExpenseServiceImpl(ExpenseRepository expenseRepository, RestTemplateBuilder restTemplateBuilder) {
        this.expenseRepository = expenseRepository;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public TotalAmountAndCurrency getTotalAmount(Currency currency) {
        log.info("IN ExpenseServiceImpl getTotal {}", currency);
        LatestCurrencyRateDto latestCurrencyRateDto = getLatestCurrencyRate();
        Map<String, String> rates = latestCurrencyRateDto.getRates();

        Collection<List<Expense>> allExpenses = getAll().values();
        BigDecimal result = allExpenses.stream()
                .flatMap(Collection::stream)
                .map(expense -> expense.getAmount()
                        .divide(new BigDecimal(rates.get(expense.getCurrency().toString())), 2, RoundingMode.CEILING))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentRate = new BigDecimal(rates.get(currency.toString()));

        return new TotalAmountAndCurrency(result.multiply(currentRate).setScale(2, RoundingMode.CEILING), currency);
    }


    private LatestCurrencyRateDto getLatestCurrencyRate() {
        String url = ("http://data.fixer.io/api/latest?access_key=4ae67f6c83d66b76d987de1469e77131&symbols=USD,UAH,PLN,EUR");
        ResponseEntity<LatestCurrencyRateDto> responseEntity = this.restTemplate.getForEntity(url, LatestCurrencyRateDto.class, 1);
        return responseEntity.getBody();
    }

    @Override
    public void save(Expense expense) throws IllegalArgumentException {
        log.info("IN ExpenseServiceImpl save {}", expense);
        expenseRepository.save(expense);
    }

    @Override
    public void delete(LocalDate localDate) throws IllegalArgumentException {
        log.info("IN ExpenseServiceImpl delete {}", localDate);
        List<Expense> expenses = expenseRepository.findAll();

        expenses.stream()
                .filter(expense -> expense.getDate().equals(localDate))
                .forEach(expense -> expenseRepository.delete(expense));
    }

    @Override
    public Map<LocalDate, List<Expense>> getAll() {
        log.info("IN ExpenseServiceImpl getAllExpenses {}");
        Map<LocalDate, List<Expense>> map = new TreeMap<>();

        List<Expense> list = new ArrayList<>(expenseRepository.findAll());
        if (list.isEmpty()) {
            return map;
        }

        for (Expense expense : list) {
            LocalDate localDate = expense.getDate();
            if (!map.containsKey(localDate)) {
                map.put(localDate, List.of(expense));
            } else {
                List<Expense> expenseList = new ArrayList<>(map.get(localDate));
                expenseList.add(expense);
                map.put(localDate, expenseList);
            }
        }
        return map;
    }
}
