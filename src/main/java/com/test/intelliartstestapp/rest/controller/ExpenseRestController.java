package com.test.intelliartstestapp.rest.controller;

import com.test.intelliartstestapp.model.Currency;
import com.test.intelliartstestapp.model.Expense;
import com.test.intelliartstestapp.rest.dto.TotalAmountAndCurrency;
import com.test.intelliartstestapp.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class ExpenseRestController {
    private ExpenseService expenseService;

    @Autowired
    public ExpenseRestController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @RequestMapping(value = "/total", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TotalAmountAndCurrency> getTotal(@RequestParam("base") String base) {
        Currency currency = Currency.valueOf(base);

        TotalAmountAndCurrency totalAmountAndCurrency = expenseService.getTotalAmount(currency);

        return new ResponseEntity<>(totalAmountAndCurrency, HttpStatus.OK);
    }

    @RequestMapping(value = "/expenses", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<LocalDate, List<Expense>>> getExpenses() {
        Map<LocalDate, List<Expense>> expenses = expenseService.getAll();

        if (expenses.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    @RequestMapping(value = "/expenses", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Expense> addExpense(@RequestBody @Validated Expense expense) {
        if (expense == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        expenseService.save(expense);
        return new ResponseEntity<>(expense, HttpStatus.OK);
    }

    @RequestMapping(value = "/expenses", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Expense> deleteExpense(@RequestParam("date") String localDate) {
        if (localDate.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        LocalDate date = LocalDate.parse(localDate);

        if (date == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        expenseService.delete(date);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
