package com.test.intelliartstestapp.service;

import com.test.intelliartstestapp.model.Currency;
import com.test.intelliartstestapp.model.Expense;
import com.test.intelliartstestapp.rest.dto.TotalAmountAndCurrency;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExpenseService {
    TotalAmountAndCurrency getTotalAmount(Currency currency);

    Expense save(Expense expense);

    void delete(LocalDate localDate);

    Map<String, List<Expense>> getAll();
//    Map<LocalDate, List<Expense>> getAll();
}
