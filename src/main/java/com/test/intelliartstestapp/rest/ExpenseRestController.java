package com.test.intelliartstestapp.rest;

import com.test.intelliartstestapp.model.Expense;
import com.test.intelliartstestapp.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class ExpenseRestController {
    private ExpenseService expenseService;

    @Autowired
    public ExpenseRestController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @RequestMapping(value = "/expenses", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Expense>> getExpenses() {
        List<Expense> expenses = expenseService.getAllExpenses();


        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }
}
