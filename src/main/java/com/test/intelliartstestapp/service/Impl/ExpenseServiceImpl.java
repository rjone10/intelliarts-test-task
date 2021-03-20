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
import java.util.List;

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
        return null;
    }

    @Override
    public void save(Expense expense) {
        log.info("IN ExpenseServiceImpl save {}", expense);
        expenseRepository.save(expense);
    }

    @Override
    public void delete(LocalDate localDate) {
        log.info("IN ExpenseServiceImpl delete {}", localDate);
        List<Expense> expenses = expenseRepository.findAll();
        expenses.stream()
                .filter(expense -> expense.getDate().equals(localDate))
                .forEach(expense -> expenseRepository.delete(expense));
    }

    @Override
    public List<Expense> getAllExpenses() {
        log.info("IN ExpenseServiceImpl getAllExpenses {}");
        return expenseRepository.findAll();
    }
}
