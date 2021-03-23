package com.test.intelliartstestapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TotalAmountAndCurrency {
    private BigDecimal total;
    private Currency currency;
}
