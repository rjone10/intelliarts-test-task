package com.test.intelliartstestapp.service;

import com.test.intelliartstestapp.model.Currency;
import com.test.intelliartstestapp.model.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    BigDecimal getTotal(Currency currency);

    void save(Expense expense);

    void delete(LocalDate localDate);

    List<Expense> getAllExpenses();
}
