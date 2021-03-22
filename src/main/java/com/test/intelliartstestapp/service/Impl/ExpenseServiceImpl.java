package com.test.intelliartstestapp.service.Impl;

import com.test.intelliartstestapp.model.Currency;
import com.test.intelliartstestapp.model.Expense;
import com.test.intelliartstestapp.repository.ExpenseRepository;
import com.test.intelliartstestapp.service.ExpenseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {
    private ExpenseRepository expenseRepository;
    private BigDecimal totalInUAH = new BigDecimal("0.00");

    @Autowired
    public ExpenseServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public BigDecimal getTotal(Currency currency) {
        return totalInUAH;
    }

    @Override
    public void save(Expense expense) {
        log.info("IN ExpenseServiceImpl save {}", expense);
        expenseRepository.save(expense);
        totalInUAH = totalInUAH.add(expense.getAmount());
    }

    @Override
    public void delete(LocalDate localDate) {
        log.info("IN ExpenseServiceImpl delete {}", localDate);
        List<Expense> expenses = expenseRepository.findAll();
        expenses.stream()
                .filter(expense -> expense.getDate().equals(localDate))
                .forEach(expense -> {
                    totalInUAH = totalInUAH.subtract(expense.getAmount());
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
