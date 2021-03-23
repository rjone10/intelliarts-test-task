package com.test.intelliartstestapp.rest.dto;

import com.test.intelliartstestapp.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TotalAmountAndCurrency {
    private BigDecimal total;
    private Currency currency;
}