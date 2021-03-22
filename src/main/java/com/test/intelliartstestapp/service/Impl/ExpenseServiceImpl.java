package com.test.intelliartstestapp.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.intelliartstestapp.model.Currency;
import com.test.intelliartstestapp.model.Expense;
import com.test.intelliartstestapp.model.TotalAndCurrency;
import com.test.intelliartstestapp.repository.ExpenseRepository;
import com.test.intelliartstestapp.service.ExpenseService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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
    private BigDecimal totalInEUR = new BigDecimal("0.00");
    private final RestTemplate restTemplate;

    @Autowired
    public ExpenseServiceImpl(ExpenseRepository expenseRepository, RestTemplateBuilder restTemplateBuilder) {
        this.expenseRepository = expenseRepository;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public TotalAndCurrency getTotal(Currency currency) {
        if (totalInEUR.equals(new BigDecimal("0.00"))) {
            return new TotalAndCurrency(totalInEUR, currency);
        }
        log.info("IN ExpenseServiceImpl getTotal {}", currency);
        String json = getStringFromJson(currency);

        try {
            JSONObject jsonObject = new JSONObject(json);
            Map<String, Object> result = new ObjectMapper().readValue(String.valueOf(jsonObject.get("rates")), new TypeReference<>(){});
            BigDecimal currencyInEUR = new BigDecimal(result.get(currency.toString()).toString());

            return new TotalAndCurrency(totalInEUR.multiply(currencyInEUR).setScale(2, RoundingMode.CEILING), currency);
        } catch (JSONException | IOException e) {
            return null;
        }
    }

    //get string from json url
    private String getStringFromJson(Currency currency) {
        String url = (String.format(
                "http://data.fixer.io/api/latest?access_key=4ae67f6c83d66b76d987de1469e77131&symbols=" +
                        "%s" +
                        "&format=1", currency.toString()));
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity(url, String.class, 1);
        return responseEntity.getBody();
    }

    //save of delete operations
    private BigDecimal setAmountInEUR(Expense expense, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
        String currency = expense.getCurrency().toString();

        String json = getStringFromJson(expense.getCurrency());

        try {
            JSONObject jsonObject = new JSONObject(json);
            Map<String, Object> result = new ObjectMapper().readValue(String.valueOf(jsonObject.get("rates")), new TypeReference<>(){});
            BigDecimal expenseAmount = expense.getAmount();
            BigDecimal currencyInEUR = new BigDecimal(result.get(currency).toString());

            BigDecimal expenseInEur = expenseAmount.divide(currencyInEUR, 2, RoundingMode.CEILING);
            totalInEUR = operation.apply(totalInEUR, expenseInEur);
        } catch (JSONException | IOException e) {
            return null;
        }
        return totalInEUR;
    }

    @Override
    public void save(Expense expense) {
        log.info("IN ExpenseServiceImpl save {}", expense);
        expenseRepository.save(expense);
        totalInEUR = setAmountInEUR(expense, BigDecimal::add);
    }

    @Override
    public void delete(LocalDate localDate) {
        log.info("IN ExpenseServiceImpl delete {}", localDate);
        List<Expense> expenses = expenseRepository.findAll();
        expenses.stream()
                .filter(expense -> expense.getDate().equals(localDate))
                .forEach(expense -> {
                    totalInEUR = setAmountInEUR(expense, BigDecimal::subtract);
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
