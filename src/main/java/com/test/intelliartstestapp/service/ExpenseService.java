package com.test.intelliartstestapp.service;

import com.test.intelliartstestapp.model.Currency;
import com.test.intelliartstestapp.model.Expense;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExpenseService {
    BigDecimal getTotal(Currency currency);

    void save(Expense expense);

    void delete(LocalDate localDate);

//    List<List<Expense>> getAllExpenses();
    Map<LocalDate, List<Expense>> getAll();
}
