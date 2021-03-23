package com.test.intelliartstestapp.service.impl;

import com.test.intelliartstestapp.model.*;
import com.test.intelliartstestapp.repository.ExpenseRepository;
import com.test.intelliartstestapp.repository.TotalAmountRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;

@Service
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {
    private ExpenseRepository expenseRepository;
    private TotalAmountRepository totalAmountRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public ExpenseServiceImpl(ExpenseRepository expenseRepository, TotalAmountRepository totalAmountRepository, RestTemplateBuilder restTemplateBuilder) {
        this.expenseRepository = expenseRepository;
        this.totalAmountRepository = totalAmountRepository;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public TotalAmountAndCurrency getTotalAmount(Currency currency) {
        log.info("IN ExpenseServiceImpl getTotal {}", currency);
        BigDecimal totalAmount = totalAmountRepository.getOne(1L).getTotalAmount();

        LatestCurrencyRateDto latestCurrencyRateDto = getLatestCurrencyRate(currency);
        Map<String, String> rates = latestCurrencyRateDto.getRates();
        BigDecimal currentRate = new BigDecimal(rates.get(currency.toString()));

        return new TotalAmountAndCurrency(totalAmount.multiply(currentRate).setScale(2, RoundingMode.CEILING), currency);
    }


    private LatestCurrencyRateDto getLatestCurrencyRate(Currency currency) {
        String url = (String.format(
                "http://data.fixer.io/api/latest?access_key=4ae67f6c83d66b76d987de1469e77131&symbols=%s", currency.toString()));
        ResponseEntity<LatestCurrencyRateDto> responseEntity = this.restTemplate.getForEntity(url, LatestCurrencyRateDto.class, 1);
        return responseEntity.getBody();
    }

    private void saveOrDeleteAmount(Expense expense, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
        LatestCurrencyRateDto latestCurrencyRateDto = getLatestCurrencyRate(expense.getCurrency());
        Map<String, String> rates = latestCurrencyRateDto.getRates();
        BigDecimal currentRate = new BigDecimal(rates.get(expense.getCurrency().toString()));

        BigDecimal expenseAmount = expense.getAmount();
        BigDecimal expenseAmountInEUR = expenseAmount.divide(currentRate, 2, RoundingMode.CEILING);

        TotalAmount totalAmountEntity = totalAmountRepository.getOne(1L);
        BigDecimal totalAmount = totalAmountEntity.getTotalAmount();

        BigDecimal resultAmount = operation.apply(totalAmount, expenseAmountInEUR);

        totalAmountEntity.setTotalAmount(resultAmount);
        totalAmountRepository.save(totalAmountEntity);
    }

    @Override
    public void save(Expense expense) {
        log.info("IN ExpenseServiceImpl save {}", expense);
        saveOrDeleteAmount(expense, BigDecimal::add);
        expenseRepository.save(expense);
    }

    @Override
    public void delete(LocalDate localDate) {
        log.info("IN ExpenseServiceImpl delete {}", localDate);
        List<Expense> expenses = expenseRepository.findAll();
        expenses.stream()
                .filter(expense -> expense.getDate().equals(localDate))
                .forEach(expense -> {
                    saveOrDeleteAmount(expense, BigDecimal::subtract);
                    expenseRepository.delete(expense);
                });
    }

    @Override
    public Map<LocalDate, List<Expense>> getAll() {
        log.info("IN ExpenseServiceImpl getAllExpenses {}");
        List<Expense> list = new ArrayList<>(expenseRepository.findAll());

        Map<LocalDate, List<Expense>> map = new TreeMap<>();
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
