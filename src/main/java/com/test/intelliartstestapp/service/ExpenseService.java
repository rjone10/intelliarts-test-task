package com.test.intelliartstestapp.service;

import com.test.intelliartstestapp.model.Currency;
import com.test.intelliartstestapp.model.Expense;
import com.test.intelliartstestapp.model.TotalAmountAndCurrency;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExpenseService {
    TotalAmountAndCurrency getTotalAmount(Currency currency);

    void save(Expense expense);

    void delete(LocalDate localDate);

    Map<LocalDate, List<Expense>> getAll();
}
